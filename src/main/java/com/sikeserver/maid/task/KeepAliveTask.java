package com.sikeserver.maid.task;

import com.sikeserver.maid.server.SessionManager;

public class KeepAliveTask implements Runnable {
    @Override
    public void run() {
        SessionManager.getInstance().broadcast("[Keep-Alive]");
    }
}
