package org.jetbrains;

import org.jetbrains.node.Node;

import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        String source = "y = (2 + 3) * 4";
        Lexer lexer = new Lexer(source);
//        List<Token> tokenList = lexer.tokenize()
//        Parser parser = new Parser(tokenList);
//        List<Node> program = parser.parseProgram();
//        program.forEach(System.out::println);
    }
}
