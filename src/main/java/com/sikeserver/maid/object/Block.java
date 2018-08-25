package com.sikeserver.maid.object;

public class Block {
    private String id;
    private String first;
    private String second;
    private String third;

    public Block(String id, String first, String second, String third) {
        this.id = id;
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public String getId() {
        return id;
    }

    public String getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }

    public String getThird() {
        return third;
    }
}
