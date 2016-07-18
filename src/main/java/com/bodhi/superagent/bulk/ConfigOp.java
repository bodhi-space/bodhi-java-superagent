package com.bodhi.superagent.bulk;

public enum ConfigOp {

    INSERT("insert"), UPDATE("update"), UPSERT("upsert"), INVITE("invite");

    private String op;

    ConfigOp(String op) {
        this.op = op;
    }

    public String getOp() {
        return op;
    }
}
