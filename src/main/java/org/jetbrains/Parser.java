package org.jetbrains;

import org.jetbrains.exception.ParseException;
import org.jetbrains.node.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser converts list of tokens into Abstract Syntax Tree (AST). <br>
 * Uses recursive descent parsing to with following order: <br>
 *  parseLogical()      -> and, or
 *  parseComparison()   -> ==, !=, <, <=, >, >=
 *  parseExpression()   -> +, -
 *  parseTerm()         -> *, /, %
 *  parsePrimary()      -> numbers, identifiers, function calls, parentheses
 */
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

    private void expect(TokenType type) {
        Token token = consume();
        if(token.type != type) {
            throw new ParseException("[Line " + token.line + "] Expected " + type + " but got " + token.type);
        }
    }

    /**
     * Parses tokens into list of statements in the program. <br>
     * Called with tokenized the source code
     * @return a list of statements
     */
    public List<Node> parseProgram() {
        List<Node> statements = new ArrayList<>();

        while(peek().type != TokenType.EOF){
            statements.add(parseStatement());
        }

        return statements;
    }

    private Node parsePrimary() {
        int line = peek().line;
        if(peek().type == TokenType.NUMBER){
            NumberNode node = new NumberNode(Integer.parseInt(consume().value));
            node.line = line;
            return node;
        } else if(peek().type == TokenType.TRUE){
            expect(TokenType.TRUE); // consume 'true'
            NumberNode node = new NumberNode(1);
            node.line = line;
            return node;
        } else if(peek().type == TokenType.FALSE){
            expect(TokenType.FALSE); // consume 'false'
            NumberNode node = new NumberNode(0);
            node.line = line;
            return node;
        } else if(peek().type == TokenType.MINUS) {
            expect(TokenType.MINUS); // consume '-'
            Node operand = parsePrimary();
            BinaryOpNode node = new BinaryOpNode(new NumberNode(0), "-", operand);
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
            expect(TokenType.L_PAREN); // consume '('
            Node expr = parseExpression();
            expect(TokenType.R_PAREN); // consume ')'
            return expr;
        }

        throw new ParseException("[Line " + peek().line + "] Unexpected token: " + peek());
    }

    private Node parseTerm() {
        Node left = parsePrimary();

        while(peek().type == TokenType.STAR || peek().type == TokenType.SLASH || peek().type == TokenType.MODULO) {
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

    private Node parseLogical() {
        Node left = parseComparison();

        while (peek().type == TokenType.AND || peek().type == TokenType.OR) {
            int line = peek().line;
            String op = consume().value;
            Node right = parseComparison();
            BinaryOpNode node = new BinaryOpNode(left, op, right);
            node.line = line;
            left = node;
        }

        return left;
    }

    /**
     * Checks first token of the statement and choose correct parser
     * @return
     */
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
            return parseLogical();
        }
    }

    private Node parseAssign() {
        int line = peek().line;
        String name = consume().value;
        expect(TokenType.ASSIGN); // consume '='
        Node expr = parseLogical();
        AssignNode node = new AssignNode(name, expr);
        node.line = line;
        return node;
    }

    private Node parseIf() {
        int line = peek().line;
        expect(TokenType.IF); // consume 'if'
        Node condition = parseLogical();
        expect(TokenType.THEN); // consume 'then'
        Node thenBranch = parseStatement();
        if(peek().type == TokenType.ELSE) {
            expect(TokenType.ELSE); // consume 'else'
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
        expect(TokenType.WHILE); // consume 'while'
        Node condition = parseLogical();
        expect(TokenType.DO); // consume 'do'
        List<Node> body = new ArrayList<>();

        body.add(parseStatement());
        while (peek().type == TokenType.COMMA){
            expect(TokenType.COMMA); // consume the comma before parsing next statement
            body.add(parseStatement());
        }
        WhileNode node = new WhileNode(condition, body);
        node.line = line;
        return node;
    }

    private Node parseFunc() {
        int line = peek().line;
        expect(TokenType.FUN); // consume 'fun'
        String name = consume().value;
        expect(TokenType.L_PAREN); // consume '('
        List<String> params = new ArrayList<>();
        if(peek().type != TokenType.R_PAREN){
            params.add(consume().value);
            while(peek().type == TokenType.COMMA){
                expect(TokenType.COMMA); // consume the comma before parsing next parameter
                params.add(consume().value);
            }
        }
        expect(TokenType.R_PAREN); // consume ')'
        expect(TokenType.L_BRACE); // consume '{'
        List<Node> body = new ArrayList<>();
        if(peek().type != TokenType.R_BRACE){
            body.add(parseStatement());
            while (peek().type == TokenType.COMMA){
                expect(TokenType.COMMA); // consume the comma before parsing next statement
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
        expect(TokenType.RETURN); // consume 'return'
        Node value = parseLogical();
        ReturnNode node = new ReturnNode(value);
        node.line = line;
        return node;
    }

    private Node parseFuncCall(){
        int line = peek().line;
        String name = consume().value;
        expect(TokenType.L_PAREN); // consume '('
        List<Node> args = new ArrayList<>();
        if(peek().type != TokenType.R_PAREN){
            args.add(parseLogical());
            while(peek().type == TokenType.COMMA) {
                expect(TokenType.COMMA); // consume the comma before parsing next argument
                args.add(parseLogical());
            }
        }
        expect(TokenType.R_PAREN); // consume ')'
        FuncCallNode node = new FuncCallNode(name, args);
        node.line = line;
        return node;
    }

    private boolean isComparison() {
        return  peek().type == TokenType.LT || peek().type == TokenType.LT_EQ ||
                peek().type == TokenType.EQ_EQ || peek().type == TokenType.NOT_EQ ||
                peek().type == TokenType.GT || peek().type == TokenType.GT_EQ;
    }
}
