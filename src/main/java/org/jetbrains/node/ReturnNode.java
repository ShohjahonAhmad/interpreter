package org.jetbrains.node;

public class ReturnNode extends Node{
    public final Node value;

    public ReturnNode(Node value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ReturnNode{" +
                "value=" + value +
                '}';
    }
}
