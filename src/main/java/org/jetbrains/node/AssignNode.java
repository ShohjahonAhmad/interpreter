package org.jetbrains.node;

public class AssignNode extends Node {
    public final String identifier;
    public final Node expression; // do you like word expression do you think it should be like result, computed value

    public AssignNode(String identifier, Node expression) {
        this.identifier = identifier;
        this.expression = expression;
    }

        @Override
        public String toString() {
            return "AssignNode{" +
                    "identifier='" + identifier + '\'' +
                    ", expression=" + expression +
                    '}';
        }
}
