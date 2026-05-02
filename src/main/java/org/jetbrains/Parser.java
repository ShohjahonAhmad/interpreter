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
            throw new ParseException("[Line " + token.line + "] Expected " + type + " but got" + token.type);
        }

        return token;
    }

    private Node parsePrimary() {
        int line = peek().line;
        if(peek().type == TokenType.NUMBER){
            NumberNode node = new NumberNode(Integer.parseInt(consume().value));
            node.line = line;
            return node;
        } else if(peek().type == TokenType.TRUE){
            consume();
            NumberNode node = new NumberNode(1);
            node.line = line;
            return node;
        } else if(peek().type == TokenType.FALSE){
            consume();
            NumberNode node = new NumberNode(0);
            node.line = line;
            return node;
        }
        else if (peek().type == TokenType.IDENTIFIER && peekNext().type == TokenType.L_PAREN) {
            return parseFuncCall();
        } else if (peek().type == TokenType.IDENTIFIER) {
            IdentifierNode node = new IdentifierNode(consume().value);
            node.line = line;
            return node;
        } else if (peek().type == TokenType.L_PAREN) {
            consume(); // consume '('
            Node expr = parseExpression();
            expect(TokenType.R_PAREN); // consume ')'
            return expr;
        }

        throw new ParseException("[Line " + peek().line + "] Unexpected token: " + peek());
    }

    private Node parseTerm() {
        Node left = parsePrimary();

        while(peek().type == TokenType.STAR || peek().type == TokenType.SLASH) {
            int line = peek().line;
            String op = consume().value;
            Node right = parsePrimary();
            BinaryOpNode node = new BinaryOpNode(left, op, right);
            node.line = line;
            left = node;
        }

        return left;
    }

    private Node parseExpression() {
        Node left = parseTerm();

        while(peek().type == TokenType.PLUS || peek().type == TokenType.MINUS) {
            int line = peek().line;
            String op = consume().value;
            Node right = parseTerm();
            BinaryOpNode node = new BinaryOpNode(left, op, right);
            node.line = line;
            left = node;
        }

        return left;
    }

    private Node parseComparison() {
        Node left = parseExpression();

        if(isComparison()) {
            int line = peek().line;
            String op = consume().value;
            Node right = parseExpression();
            BinaryOpNode node = new BinaryOpNode(left, op, right);
            node.line = line;
            left = node;
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
        int line = peek().line;
        String name = consume().value;
        consume();
        Node expr = parseComparison();
        AssignNode node = new AssignNode(name, expr);
        node.line = line;
        return node;
    }

    private Node parseIf() {
        int line = peek().line;
        consume(); // consume 'if'
        Node condition = parseComparison();
        expect(TokenType.THEN); // consume 'then'
        Node thenBranch = parseStatement();
        if(peek().type == TokenType.ELSE) {
            consume(); // consume 'else'
            Node elseBranch = parseStatement();
            IfNode node = new IfNode(condition, thenBranch, elseBranch);
            node.line = line;
            return node;
        }
        IfNode node = new IfNode(condition, thenBranch);
        node.line = line;
        return node;
    }

    private Node parseWhile() {
        int line = peek().line;
        consume(); // consume 'while'
        Node condition = parseComparison();
        expect(TokenType.DO); // consume 'do'
        List<Node> body = new ArrayList<>();

        body.add(parseStatement());
        while (peek().type == TokenType.COMMA){
            consume(); // consume the comma before parsing next statement
            body.add(parseStatement());
        }
        WhileNode node = new WhileNode(condition, body);
        node.line = line;
        return node;
    }

    private Node parseFunc() {
        int line = peek().line;
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
        FuncNode node = new FuncNode(name, params, body);
        node.line = line;
        return node;
    }

    private Node parseReturn() {
        int line = peek().line;
        consume(); // consume 'return'
        Node value = parseComparison();
        ReturnNode node = new ReturnNode(value);
        node.line = line;
        return node;
    }

    private Node parseFuncCall(){
        int line = peek().line;;
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
        FuncCallNode node = new FuncCallNode(name, args);
        node.line = line;
        return node;
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
