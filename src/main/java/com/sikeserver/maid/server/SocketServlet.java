package com.sikeserver.maid.server;

import com.sikeserver.maid.Maid;
import com.sikeserver.maid.util.Learner;
import com.sikeserver.maid.util.Logger;
import com.sikeserver.maid.util.Marcov;
import com.sikeserver.maid.util.MeCab;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SocketServlet extends WebSocketServlet {
    private static final long serialVersionUID = 1L;

    private final Map<String, String> mime = new HashMap<>();

    public SocketServlet() {
        mime.put("html", "text/html");
        mime.put("css", "text/css");
        mime.put("js", "text/javascript");
        mime.put("json", "application/json");
        mime.put("ico", "image/x-icon");
        mime.put("png", "image/png");
        mime.put("svg", "image/svg+xml");
        mime.put("xml", "application/xml");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        var path = request.getServletPath();
        if (path.equalsIgnoreCase("/api")) {
            try (var writer = response.getWriter()) {
                response.setContentType("text/plain; charset=utf-8");

                var sentence = Learner.generateSentence();
                writer.write(URLEncoder.encode(sentence, StandardCharsets.UTF_8));
                Logger.info(request.getRemoteAddr() + " generated: " + sentence);
            } catch (IOException | SQLException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

            return;
        } else if (path.equals("/")) {
            path = "/index.html";
        }

        var localPath = Maid.WEB_SOURCE + path;
        if (Maid.class.getResource(localPath) == null) {
            response.setStatus(404);
            return;
        }

        response.setContentType(getMIME(path) + "; charset=utf-8");

        try (var input = Maid.class.getResourceAsStream(localPath);
             var output = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            for (var length = input.read(buffer); length != -1; length = input.read(buffer)) {
                output.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/plain; charset=utf-8");

        var source = new StringBuilder();
        try (var reader = request.getReader()) {
            while (true) {
                var line = reader.readLine();
                if (line == null) {
                    break;
                }

                source.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        var decoded = URLDecoder.decode(source.toString(), StandardCharsets.UTF_8);
        try (var writer = response.getWriter()) {
            var result = MeCab.analyze(decoded);
            var words = new ArrayList<String>();
            for (var line : result) {
                words.add(line.split("\t")[0]);
            }

            var blocks = Marcov.generateBlocks(words);
            Learner.addBlocks(blocks);

            for (var block : blocks) {
                writer.write(
                    block.getFirst() + ", "
                        + block.getSecond() + ", "
                        + block.getThird() + "\n"
                );
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(WebSocketListener.class);
    }

    private String getMIME(String path) {
        int index = path.lastIndexOf(".");
        if (index == -1) return "";

        String ext = path.substring(index + 1);
        return mime.get(ext);
    }
}
