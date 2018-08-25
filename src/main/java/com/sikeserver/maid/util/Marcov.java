package com.sikeserver.maid.util;

import com.sikeserver.maid.object.Block;

import java.util.ArrayList;
import java.util.List;

public class Marcov {
    public static List<Block> generateBlocks(List<String> words) {
        words.add(0, null);
        words.add(null);

        var blocks = new ArrayList<Block>();
        for (var i = 0; i < words.size() - 2; i++) {
            blocks.add(
                new Block(
                    words.get(i),
                    words.get(i + 1),
                    words.get(i + 2)
                )
            );
        }

        return blocks;
    }
}
