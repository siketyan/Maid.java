package com.sikeserver.maid;

import com.sikeserver.maid.server.SocketServlet;
import com.sikeserver.maid.util.NoLogging;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;

public class Maid {
    public static void main(String[] args) {
        var server = new Server(8080);
        var handlers = new HandlerCollection();
        var servlet = new SocketServlet();
        var handler = new ServletContextHandler(ServletContextHandler.SESSIONS);

        Log.setLog(new NoLogging());
        handler.addServlet(new ServletHolder(servlet), "/");
        handlers.addHandler(handler);
        server.setHandler(handlers);

        try {
            server.start();
            server.join();
        } catch (InterruptedException e) {
            System.out.println("Shutting down");
        } catch (Exception e) {
            System.out.println("Failed to start server");
        }
    }
}
