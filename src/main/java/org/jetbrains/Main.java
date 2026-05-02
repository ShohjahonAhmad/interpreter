package org.jetbrains;

import org.jetbrains.node.Node;

import java.util.List;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        Scanner scanner = new Scanner(System.in);
        StringBuilder source = new StringBuilder();

        System.out.println("Enter your code (type 'quit' or press Enter on an empty line to finish):");

        while(scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if("quit".equals(input) || input.isEmpty()) break;
            source.append(input).append('\n');
        }

        Lexer lexer = new Lexer(source.toString());
        List<Token> tokens = lexer.tokenize();

        Parser parser = new Parser(tokens);
        List<Node> program = parser.parseProgram();

        Interpreter interpreter = new Interpreter();
        interpreter.interpret(program);
        interpreter.printVariables();
    }
}
