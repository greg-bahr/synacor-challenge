package com.gregorybahr;

/**
 * Created by greg on 2/5/2017.
 */
public abstract class Opcode {

    public String name;
    public int numParams;

    public Opcode(String name, int numParams) {
        this.name = name;
        this.numParams = numParams;
    }

    public abstract void execute();
}
