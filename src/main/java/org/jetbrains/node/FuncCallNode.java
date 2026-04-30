package org.jetbrains.node;

import java.util.List;

public class FuncCallNode extends Node{
    public final String name;
    public final List<Node> args;

    public FuncCallNode(String name, List<Node> args) {
        this.name = name;
        this.args = args;
    }

    @Override
    public String toString() {
        return "FuncCall{" +
                "name=" + name +
                ", args=" + args +
                "}";
    }
}
