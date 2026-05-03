package org.jetbrains;

import org.jetbrains.exception.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Lexer converts raw source code into a list tokens.<br>
 * Tracks line number for error reporting.
 */
public class Lexer {

    private final String source;
    private int pos = 0;
    private int line = 1;

    public Lexer(String source) {
         this.source = source;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while(pos < source.length()) {
            char c = source.charAt(pos);

            if (Character.isDigit(c)) {
                tokens.add(getNumberToken());
            } else if (Character.isLetter(c) || c == '_') {
                tokens.add(getIdentifierOrKeywordToken());
            } else if (isWhiteSpace(c)){
                // skip whitespace
                if(c == '\n') line++;
            } else if (c == '+') {
                tokens.add(new Token(TokenType.PLUS, "+", line));
            } else if (c == '-') {
                tokens.add(new Token(TokenType.MINUS, "-", line));
            } else if (c == '*') {
                tokens.add(new Token(TokenType.STAR, "*", line));
            } else if (c == '/') {
                tokens.add(new Token(TokenType.SLASH, "/", line));
            } else if (c == '=') {
                tokens.add(getAssignOrEqualToken());
            } else if (c == '(') {
                tokens.add(new Token(TokenType.L_PAREN, "(", line));
            } else if (c == ')') {
                tokens.add(new Token(TokenType.R_PAREN, ")", line));
            } else if (c == '{') {
                tokens.add(new Token(TokenType.L_BRACE, "{", line));
            } else if (c == '}') {
                tokens.add(new Token(TokenType.R_BRACE, "}", line));
            } else if (c == ',') {
                tokens.add(new Token(TokenType.COMMA, ",", line));
            } else if (c == '<'){
                tokens.add(getLessThanOrLessThanEqualToken());
            } else if (c == '>'){
                tokens.add(getGreaterThanOrGreaterThanEqualToken());
            } else if (c == '!'){
                tokens.add(getNotEqualToken());
            } else if (c == '%'){
                tokens.add(new Token(TokenType.MODULO, "%", line));
            } else {
                throw new ParseException("[Line " + line + "] Unexpected character: '" + c + "'");
            }

            pos++;
        }

        tokens.add(new Token(TokenType.EOF, "", line));
        return tokens;
    }


    ///  TOKEN CHECK METHODS
    private boolean isIdentifier(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    private boolean isWhiteSpace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }


    /// GETTING TOKEN METHODS
    private Token getNumberToken() {
        StringBuilder number = new StringBuilder();
        number.append(source.charAt(pos));

        while (pos + 1 < source.length() && Character.isDigit(source.charAt(pos + 1))) {
            pos++;
            number.append(source.charAt(pos));
        }

        return new Token(TokenType.NUMBER, number.toString(), line);
    }

    private Token getIdentifierOrKeywordToken() {
        StringBuilder identifier = new StringBuilder();
        identifier.append(source.charAt(pos));

        while (pos + 1 < source.length() && isIdentifier(source.charAt(pos + 1))) {
            pos++;
            identifier.append(source.charAt(pos));
        }

        String word = identifier.toString();
        TokenType type = switch (word) {
            case "if" -> TokenType.IF;
            case "then" -> TokenType.THEN;
            case "else" -> TokenType.ELSE;
            case "while" -> TokenType.WHILE;
            case "do" -> TokenType.DO;
            case "fun" -> TokenType.FUN;
            case "return" -> TokenType.RETURN;
            case "true" -> TokenType.TRUE;
            case "false" -> TokenType.FALSE;
            case "and" -> TokenType.AND;
            case "or" -> TokenType.OR;
            default -> TokenType.IDENTIFIER;
        };

        return new Token(type, word, line);
    }

    private Token getAssignOrEqualToken() {
        if(pos + 1 < source.length() && source.charAt(pos + 1) == '=') {
            pos++; // skip the next '='
            return new Token(TokenType.EQ_EQ, "==", line);
        }
        return new Token(TokenType.ASSIGN, "=", line);
    }

    private Token getLessThanOrLessThanEqualToken() {
        if(pos + 1 < source.length() && source.charAt(pos + 1) == '=') {
            pos++; // skip the next '='
            return new Token(TokenType.LT_EQ, "<=", line);
        }
        return new Token(TokenType.LT, "<", line);
    }

    private Token getGreaterThanOrGreaterThanEqualToken() {
        if(pos + 1 < source.length() && source.charAt(pos + 1) == '=') {
            pos++; // skip the next '='
            return new Token(TokenType.GT_EQ, ">=", line);
        }
        return new Token(TokenType.GT, ">", line);
    }

    private Token getNotEqualToken() {
        if(pos + 1 < source.length() && source.charAt(pos + 1) == '=') {
            pos++; // skip the next '='
            return new Token(TokenType.NOT_EQ, "!=", line);
        }

        throw new ParseException("[Line " + line + "] Invalid syntax");
    }

}
