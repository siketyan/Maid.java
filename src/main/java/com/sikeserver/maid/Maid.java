package com.sikeserver.maid;

import com.sikeserver.maid.server.SocketServlet;
import com.sikeserver.maid.util.Logger;
import com.sikeserver.maid.util.NoLogging;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Maid {
    private static final String CONFIG_FILE = "maid.properties";

    private static Properties conf;

    public static void main(String[] args) {
        conf = new Properties();
        try (var stream = new FileInputStream(CONFIG_FILE);
             var reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            conf.load(reader);
            Logger.success("Loaded config.");
        } catch (FileNotFoundException e) {
            Logger.info("Configuration file does not exists, applying default");
        } catch (IOException e) {
            e.printStackTrace();
        }

        var server = new Server(Integer.parseUnsignedInt(conf.getProperty("Server.Port", "8080")));
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

    public static Properties getConfig() {
        return conf;
    }
}
