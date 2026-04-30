package org.jetbrains.node;

import java.util.List;

public class WhileNode extends Node{
    public Node condition;
    public List<Node> body;

    public WhileNode(Node condition, List<Node> body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String toString() {
        return "WhileNode{" +
                "condition=" + condition +
                ", body=" + body +
                '}';
    }
}
