package org.jetbrains;

public enum TokenType {
    NUMBER,
    IDENTIFIER,
    PLUS,
    MINUS,
    STAR,
    SLASH,
    ASSIGN,
    AND,
    OR,
    IF,
    FUN,
    WHILE,
    DO,
    EOF, // end of program
    TRUE,
    FALSE,
    THEN,
    RETURN,
    ELSE,
    NOT_EQ, // is not equal
    EQ_EQ, // is equal
    LT, // less than
    LT_EQ, // less than or equal
    GT, // greater
    GT_EQ, // greater than or equal
    COMMA,
    L_PAREN, // left parenthesis
    R_PAREN, // right parenthesis
    L_BRACE, // left brace
    R_BRACE, // right brace
}
