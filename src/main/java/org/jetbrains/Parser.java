package org.jetbrains;

import org.jetbrains.node.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int pos = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token peek(){
        return tokens.get(pos);
    }

    private Token peekNext() {
        if(pos + 1 < tokens.size()) return tokens.get(pos + 1);
        return tokens.getLast(); // return EOF
    }

    private Token consume() {
        return tokens.get(pos++);
    }

    private Token expect(TokenType type) {
        Token token = consume();
        if(token.type != type) {
            throw new RuntimeException("Expected " + type + " but got" + token.type);
        }

        return token;
    }

    private Node parsePrimary() {
        if(peek().type == TokenType.NUMBER){
            return new NumberNode(Integer.parseInt(consume().value));
        } else if (peek().type == TokenType.IDENTIFIER) {
            return new IdentifierNode(consume().value);
        } else if (peek().type == TokenType.L_PAREN) {
            consume(); // consume '('
            Node expr = parseExpression();
            expect(TokenType.R_PAREN); // consume ')'
            return expr;
        }

        throw new RuntimeException("Unexpected token: " + peek());
    }

    private Node parseTerm() {
        Node left = parsePrimary();

        while(peek().type == TokenType.STAR || peek().type == TokenType.SLASH) {
            String op = consume().value;
            Node right = parsePrimary();
            left = new BinaryOpNode(left, op, right);
        }

        return left;
    }

    private Node parseExpression() {
        Node left = parseTerm();

        while(peek().type == TokenType.PLUS || peek().type == TokenType.MINUS) {
            String op = consume().value;
            Node right = parseTerm();
            left = new BinaryOpNode(left, op, right);
        }

        return left;
    }

    private Node parseComparison() {
        Node left = parseExpression();

        if(isComparison()) {
            String op = consume().value;
            Node right = parseExpression();
            left = new BinaryOpNode(left, op, right);
        }

        return left;
    }

    private Node parseStatement() {
        if(peek().type == TokenType.IDENTIFIER && peekNext().type == TokenType.ASSIGN){
            String name = consume().value;
            consume();
            Node expr = parseComparison();
            return new AssignNode(name, expr);
        } else if(peek().type == TokenType.IF){
            return parseIf();
        }
        else {
            return parseComparison();
        }
    }

    private Node parseIf() {
        consume(); // consume 'if'
        Node condition = parseComparison();
        expect(TokenType.THEN); // consume 'then'
        Node thenBranch = parseStatement();
        if(peek().type == TokenType.ELSE) {
            consume(); // consume 'else'
            Node elseBranch = parseStatement();
            return new IfNode(condition, thenBranch, elseBranch);
        }

        return new IfNode(condition, thenBranch);
    }

    private List<Node> parseProgram() {
        List<Node> statements = new ArrayList<>();

        while(peek().type != TokenType.EOF){
            statements.add(parseStatement());
        }

        return statements;
    }


    private boolean isComparison() {
        return  peek().type == TokenType.LT || peek().type == TokenType.LT_EQ ||
                peek().type == TokenType.EQ_EQ || peek().type == TokenType.NOT_EQ ||
                peek().type == TokenType.GT || peek().type == TokenType.GT_EQ;
    }
}
