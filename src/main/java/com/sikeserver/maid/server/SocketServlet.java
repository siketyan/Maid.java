package com.sikeserver.maid.server;

import com.sikeserver.maid.util.Learner;
import com.sikeserver.maid.util.Marcov;
import com.sikeserver.maid.util.MeCab;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;

public class SocketServlet extends WebSocketServlet {
    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/plain; charset=utf-8");

        try (var writer = response.getWriter()) {
            writer.write(Learner.generateSentence());
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
}
