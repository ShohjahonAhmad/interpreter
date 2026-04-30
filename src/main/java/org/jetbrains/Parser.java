package org.jetbrains;

import org.jetbrains.exception.ParseException;
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
            throw new ParseException("Expected " + type + " but got" + token.type);
        }

        return token;
    }

    private Node parsePrimary() {
        if(peek().type == TokenType.NUMBER){
            return new NumberNode(Integer.parseInt(consume().value));
        } else if(peek().type == TokenType.TRUE){
            consume();
            return new NumberNode(1);
        } else if(peek().type == TokenType.FALSE){
            consume();
            return new NumberNode(0);
        }
        else if (peek().type == TokenType.IDENTIFIER && peekNext().type == TokenType.L_PAREN) {
            return parseFuncCall();
        } else if (peek().type == TokenType.IDENTIFIER) {
            return new IdentifierNode(consume().value);
        } else if (peek().type == TokenType.L_PAREN) {
            consume(); // consume '('
            Node expr = parseExpression();
            expect(TokenType.R_PAREN); // consume ')'
            return expr;
        }

        throw new ParseException("Unexpected token: " + peek());
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
        if(peek().type == TokenType.FUN) {
            return parseFunc();
        } else if(peek().type == TokenType.IDENTIFIER && peekNext().type == TokenType.ASSIGN){
            return parseAssign();
        } else if (peek().type == TokenType.IDENTIFIER && peekNext().type == TokenType.L_PAREN) {
            return parseFuncCall();
        } else if(peek().type == TokenType.IF){
            return parseIf();
        } else if(peek().type == TokenType.WHILE) {
            return parseWhile();
        } else if (peek().type == TokenType.RETURN){
            return parseReturn();
        } else {
            return parseComparison();
        }
    }

    private Node parseAssign() {
        String name = consume().value;
        consume();
        Node expr = parseComparison();
        return new AssignNode(name, expr);
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

    private Node parseWhile() {
        consume(); // consume 'while'
        Node condition = parseComparison();
        expect(TokenType.DO); // consume 'do'
        List<Node> body = new ArrayList<>();

        body.add(parseStatement());
        while (peek().type == TokenType.COMMA){
            consume(); // consume the comma before parsing next statement
            body.add(parseStatement());
        }

        return new WhileNode(condition, body);
    }

    private Node parseFunc() {
        consume(); // consume 'func'
        String name = consume().value;
        expect(TokenType.L_PAREN); // consume '('
        List<String> params = new ArrayList<>();
        if(peek().type != TokenType.R_PAREN){
            params.add(consume().value);
            while(peek().type == TokenType.COMMA){
                consume(); // consume the comma before parsing next parameter
                params.add(consume().value);
            }
        }
        expect(TokenType.R_PAREN); // consume ')'
        expect(TokenType.L_BRACE); // consume '{'
        List<Node> body = new ArrayList<>();
        if(peek().type != TokenType.R_BRACE){
            body.add(parseStatement());
            while (peek().type == TokenType.COMMA){
                consume(); // consume the comma before parsing next statement
                body.add(parseStatement());
            }
        }
        expect(TokenType.R_BRACE); // consume '}'

        return new FuncNode(name, params, body);
    }

    private Node parseReturn() {
        consume(); // consume 'return'
        Node value = parseComparison();
        return new ReturnNode(value);
    }

    private Node parseFuncCall(){
        String name = consume().value;
        consume(); // consume '('
        List<Node> args = new ArrayList<>();
        if(peek().type != TokenType.R_PAREN){
            args.add(parseComparison());
            while(peek().type == TokenType.COMMA) {
                consume(); // consume the comma before parsing next argument
                args.add(parseComparison());
            }
        }
        expect(TokenType.R_PAREN); // consume ')'
        return new FuncCallNode(name, args);
    }

    public List<Node> parseProgram() {
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
