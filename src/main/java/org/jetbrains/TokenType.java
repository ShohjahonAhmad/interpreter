package org.jetbrains;

/**
 * All token types recognized by the Lexer and used by the Parser.
 */
public enum TokenType {

    // Literals
    NUMBER,
    IDENTIFIER,
    TRUE,       // true
    FALSE,      // false

    // Arithmetic operators
    PLUS,       // +
    MINUS,      // -
    STAR,       // *
    SLASH,      // /
    MODULO,     // %

    // Comparison operators
    NOT_EQ,     // ==
    EQ_EQ,      // !=
    LT,         // <
    LT_EQ,      // <=
    GT,         // >
    GT_EQ,      // >=

    // Logical operators
    AND,        // and
    OR,         // or

    // Assignment
    ASSIGN,     // =

    // Keywords
    IF,
    THEN,
    ELSE,
    WHILE,
    DO,
    FUN,
    RETURN,

    // Delimiters
    L_PAREN,    // (
    R_PAREN,    // )
    L_BRACE,    // {
    R_BRACE,    // }
    COMMA,

    // Special
    EOF,        // End Of Program
}
