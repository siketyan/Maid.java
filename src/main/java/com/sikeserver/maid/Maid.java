package com.sikeserver.maid;

import com.sikeserver.maid.server.SocketServlet;
import com.sikeserver.maid.util.Logger;
import com.sikeserver.maid.util.NoLogging;
import com.sikeserver.maid.util.SQLManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Properties;

public class Maid {
    public static final String WEB_SOURCE = "/web";
    private static final String CONFIG_FILE = "maid.properties";

    private static Properties conf;
    private static SQLManager sql;

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

        try {
            sql = new SQLManager(
                conf.getProperty("SQL.Type", "mysql"),
                conf.getProperty("SQL.Host", "localhost"),
                Integer.parseUnsignedInt(conf.getProperty("SQL.Port", "3306")),
                conf.getProperty("SQL.Database", "maid"),
                conf.getProperty("SQL.User", "maid"),
                conf.getProperty("SQL.Password", ""),
                Integer.parseUnsignedInt(conf.getProperty("SQL.Timeout", "1"))
            );

            Logger.success("Connected to SQL server.");
        } catch (SQLException e) {
            Logger.error("Failed to connect to SQL server");
            e.printStackTrace();

            return;
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

    public static SQLManager getSQL() {
        return sql;
    }
}
