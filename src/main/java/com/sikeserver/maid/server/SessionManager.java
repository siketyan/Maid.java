package com.sikeserver.maid.server;

import com.sikeserver.maid.Maid;
import com.sikeserver.maid.task.KeepAliveTask;
import com.sikeserver.maid.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SessionManager {
    private static SessionManager instance = new SessionManager();
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static KeepAliveTask task = new KeepAliveTask();

    private boolean isTaskRunning;
    private int keepAliveDelay = Integer.parseUnsignedInt(Maid.getConfig().getProperty("Server.KeepAlive", "60000"));
    private ScheduledFuture future;
    private List<WebSocketListener> sessions = new ArrayList<>();

    void add(WebSocketListener socket){
        sessions.add(socket);

        if (!isTaskRunning) {
            future = scheduler.scheduleWithFixedDelay(
                task,
                keepAliveDelay,
                keepAliveDelay,
                TimeUnit.MILLISECONDS
            );
            isTaskRunning = true;

            Logger.info("WebSocket task started.");
        }
    }

    void remove(WebSocketListener socket){
        sessions.remove(socket);

        if (sessions.isEmpty()) {
            future.cancel(true);
            isTaskRunning = false;

            Logger.info("WebSocket task stopped.");
        }
    }

    public void broadcast(String message){
        for (WebSocketListener session: sessions) {
            session.send(message);
        }
    }

    public static SessionManager getInstance(){
        return instance;
    }
}
