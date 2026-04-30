package org.jetbrains.node;

public class IfNode extends Node{
    public final Node condition;
    public final Node trueBranch;
    public final Node falseBranch;

    public IfNode(Node condition, Node trueBranch, Node falseBranch) {
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    public IfNode(Node condition, Node trueBranch) {
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.falseBranch = null;
    }

    @Override
    public String toString() {
        return "IfNode{" +
                "condition=" + condition +
                ", trueBranch=" + trueBranch +
                ", falseBranch=" + falseBranch +
                '}';
    }
}
