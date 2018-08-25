package com.sikeserver.maid.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MeCab {
    public static List<String> analyze(String text) throws IOException {
        var process = Runtime.getRuntime().exec("mecab");

        try (var stream = process.getOutputStream();
             var writer = new BufferedWriter(new OutputStreamWriter(stream))) {
            writer.write(text);
            writer.flush();
        }

        var result = new ArrayList<String>();
        try (var stream = process.getInputStream();
             var reader = new BufferedReader(new InputStreamReader(stream))) {
            while (true) {
                var line = reader.readLine();
                if (line == null) {
                    break;
                } else if (line.equals("EOS")) {
                    continue;
                }

                result.add(line);
            }
        }

        return result;
    }
}
