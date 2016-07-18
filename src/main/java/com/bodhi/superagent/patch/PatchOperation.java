package com.bodhi.superagent.patch;


public enum PatchOperation {

    ADD("add"), REMOVE("remove"), REPLACE("replace"),
    MOVE("move"), COPY("copy"), TEST("test");

    private String op;

    PatchOperation(String op) {
        this.op = op;
    }

    public String getOp() {
        return op;
    }
}
