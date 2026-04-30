package org.jetbrains.node;

import java.util.List;

public class FuncNode extends Node{
    public final String name;
    public final List<String> params;
    public final List<Node> body;

    public FuncNode(String name, List<String> params, List<Node> body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }

    @Override
    public String toString() {
        return "FuncNode{" +
                "name=" + name +
                ", params=" + params +
                ", body=" + body +
                '}';
    }
}
