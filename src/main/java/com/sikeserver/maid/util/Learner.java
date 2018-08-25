package com.sikeserver.maid.util;

import com.sikeserver.maid.Maid;
import com.sikeserver.maid.object.Block;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Learner {
    private static void addBlock(Block block) throws SQLException {
        try (var stmt = Maid.getSQL().getPreparedStatement(
            "INSERT INTO `blocks`(`id`, `first`, `second`, `third`) VALUES(?, ?, ?, ?)"
        )) {
            stmt.setString(1, block.getId());
            stmt.setString(2, block.getFirst());
            stmt.setString(3, block.getSecond());
            stmt.setString(4, block.getThird());
            stmt.execute();
        }
    }

    public static void addBlocks(List<Block> blocks) throws SQLException {
        for (var block : blocks) {
            addBlock(block);
        }
    }

    private static Block findBlock(String word) throws SQLException {
        try (var stmt = Maid.getSQL().getPreparedStatement(
            "SELECT * FROM `blocks` WHERE `first` <=> ?"
        )) {
            stmt.setString(1, word);

            var suggestions = new ArrayList<Block>();
            try (var rs = stmt.executeQuery()) {
                while (rs.next()) {
                    suggestions.add(
                        new Block(
                            rs.getString("id"),
                            rs.getString("first"),
                            rs.getString("second"),
                            rs.getString("third")
                        )
                    );
                }
            }

            if (suggestions.size() == 0) {
                return null;
            } else if (suggestions.size() == 1) {
                return suggestions.get(0);
            }

            var random = ThreadLocalRandom.current().nextInt(0, suggestions.size());
            return suggestions.get(random);
        }
    }

    private static List<Block> findBlocks() throws SQLException {
        String previous = null;
        var blocks = new ArrayList<Block>();
        while (blocks.size() == 0 || blocks.get(blocks.size() - 1).getThird() != null) {
            var block = findBlock(previous);
            if (block == null) {
                break;
            }

            blocks.add(block);
            previous = block.getThird();
        }

        return blocks;
    }

    public static String generateSentence() throws SQLException {
        var blocks = findBlocks();
        var sentence = new StringBuilder();
        for (var block : blocks) {
            sentence.append(block.getSecond());

            var third = block.getThird();
            if (third != null) {
                sentence.append(third);
            }
        }

        return sentence.toString();
    }
}
