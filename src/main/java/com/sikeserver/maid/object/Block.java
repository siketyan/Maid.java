package com.sikeserver.maid.object;

public class Block {
    private String first;
    private String second;
    private String third;

    public Block(String first, String second, String third) {
        this.first = first;
        this.second = second;
        this.third = third;
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
