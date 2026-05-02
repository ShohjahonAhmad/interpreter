package org.jetbrains;

import org.jetbrains.exception.InterpreterException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {

    private Map<String, Integer> run(String source) {
        Lexer lexer = new Lexer(source);
        Parser parser = new Parser(lexer.tokenize());
        Interpreter interpreter = new Interpreter();
        interpreter.interpret(parser.parseProgram());
        return interpreter.getVariables();
    }

    @Test
    public void testThatAssignAndMathOperationsWork() {
        String source = "x = 2\n" +
                        "y = (x + 2) * 2";

        Map<String, Integer> result = run(source);

        assertEquals(2, result.get("x"));
        assertEquals(8, result.get("y"));
    }

    @Test
    public void testThatIfThenBodyExecutedWhenTrue() {
        String source = "x = 20\n" +
                        "if x > 10 then y = 100 else y = 0";

        Map<String, Integer> result = run(source);

        assertEquals(100, result.get("y"));
        assertEquals(20, result.get("x"));
    }

    @Test
    public void testThatIfElseExecutedWhenFalse() {
        String source = "x = 20\n" +
                        "if x < 10 then y = 100 else y = 0";

        Map<String, Integer> result = run(source);

        assertEquals(0, result.get("y"));
        assertEquals(20, result.get("x"));
    }

    @Test
    public void testThatWhileWorks(){
        String source = "x = 0\n" +
                        "y = 0\n" +
                        "while x < 3 do if x == 1 then y = 10 else y = y + 1, x = x + 1";

        Map<String, Integer> result = run(source);

        assertEquals(3, result.get("x"));
        assertEquals(11, result.get("y"));
    }

    @Test
    public void testThatFunctionWorks(){
        String source = "fun add(a, b) { return a + b } " +
                        "four = add( 2, 2)";

        Map<String, Integer> result = run(source);

        assertEquals(4, result.get("four"));
    }

    @Test
    public void tesThatRecursionWorks(){
        String source = "fun fact_rec(n) { if n <= 0 then return 1 else return n*fact_rec(n-1) }\n" +
                        "a = fact_rec(5)";

        Map<String, Integer> result = run(source);

        assertEquals(120, result.get("a"));
    }

    @Test
    public void testThatIterationWorks(){
        String source = "fun fact_iter(n) { r = 1, while true do if n == 0 then return r else r = r * n, n = n - 1 }\n" +
                        "b = fact_iter(5)";

        Map<String, Integer> result = run(source);

        assertEquals(120, result.get("b"));
    }

    @Test
    public void testThatLexerTokenizesAssignment() {
        Lexer lexer = new Lexer("x = 2");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.IDENTIFIER, tokens.get(0).type);
        assertEquals("x", tokens.get(0).value);
        assertEquals(TokenType.ASSIGN, tokens.get(1).type);
        assertEquals(TokenType.NUMBER, tokens.get(2).type);
        assertEquals("2", tokens.get(2).value);
        assertEquals(TokenType.EOF, tokens.get(3).type);
    }

    @Test
    public void testThatLexerTokenizesComparisonOps() {
        Lexer lexer = new Lexer("x >= 10");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.IDENTIFIER, tokens.get(0).type);
        assertEquals("x", tokens.get(0).value);
        assertEquals(TokenType.GT_EQ, tokens.get(1).type);
        assertEquals(TokenType.NUMBER, tokens.get(2).type);
        assertEquals("10", tokens.get(2).value);
        assertEquals(TokenType.EOF, tokens.get(3).type);

        lexer = new Lexer("x == 10");
        tokens = lexer.tokenize();

        assertEquals(TokenType.IDENTIFIER, tokens.get(0).type);
        assertEquals("x", tokens.get(0).value);
        assertEquals(TokenType.EQ_EQ, tokens.get(1).type);
        assertEquals(TokenType.NUMBER, tokens.get(2).type);
        assertEquals("10", tokens.get(2).value);
        assertEquals(TokenType.EOF, tokens.get(3).type);
    }

    @Test
    public void testThatLexerTokenizesIfElse() {
        Lexer lexer = new Lexer("if x > 10 then y = 100 else y = 0");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.IF, tokens.get(0).type);
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).type);
        assertEquals("x", tokens.get(1).value);
        assertEquals(TokenType.GT, tokens.get(2).type);
        assertEquals(TokenType.NUMBER, tokens.get(3).type);
        assertEquals("10", tokens.get(3).value);
        assertEquals(TokenType.THEN, tokens.get(4).type);
        assertEquals(TokenType.IDENTIFIER, tokens.get(5).type);
        assertEquals("y", tokens.get(5).value);
        assertEquals(TokenType.ASSIGN, tokens.get(6).type);
        assertEquals(TokenType.NUMBER, tokens.get(7).type);
        assertEquals("100", tokens.get(7).value);
        assertEquals(TokenType.ELSE, tokens.get(8).type);
        assertEquals(TokenType.IDENTIFIER, tokens.get(9).type);
        assertEquals("y", tokens.get(9).value);
        assertEquals(TokenType.ASSIGN, tokens.get(10).type);
        assertEquals(TokenType.NUMBER, tokens.get(11).type);
        assertEquals("0", tokens.get(11).value);
        assertEquals(TokenType.EOF, tokens.get(12).type);
    }

    @Test
    public void testThatLexerRecognizesKeywords() {
        Lexer lexer = new Lexer("if then else while do fun return");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.IF, tokens.get(0).type);
        assertEquals(TokenType.THEN, tokens.get(1).type);
        assertEquals(TokenType.ELSE, tokens.get(2).type);
        assertEquals(TokenType.WHILE, tokens.get(3).type);
        assertEquals(TokenType.DO, tokens.get(4).type);
        assertEquals(TokenType.FUN, tokens.get(5).type);
        assertEquals(TokenType.RETURN, tokens.get(6).type);
    }

    @Test
    public void testThatLexerRecognizesTwoCharOperators() {
        Lexer lexer = new Lexer("== != <= >=");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.EQ_EQ, tokens.get(0).type);
        assertEquals(TokenType.NOT_EQ, tokens.get(1).type);
        assertEquals(TokenType.LT_EQ, tokens.get(2).type);
        assertEquals(TokenType.GT_EQ, tokens.get(3).type);
    }

    @Test
    public void testThatLexerRecognizesIdentifierAndNumber() {
        Lexer lexer = new Lexer("variable1  42");
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.IDENTIFIER, tokens.get(0).type);
        assertEquals("variable1", tokens.get(0).value);
        assertEquals(TokenType.NUMBER, tokens.get(1).type);
        assertEquals("42", tokens.get(1).value);
        assertEquals(TokenType.EOF, tokens.get(2).type);
    }

    @Test
    public void testThatUndefinedVariableThrowsError() {
        assertThrows(InterpreterException.class, () -> run("x = y + 2"));
    }

    @Test
    public void testThatUndefineFunctionThrowsError() {
        assertThrows(InterpreterException.class, () -> run("x = add(2, 3)"));
    }

    @Test
    public void testThatArgumentCountMismatchThrowsError() {
        String source = "fun add(a, b) { return a + b }\n" +
                        "x = add(2)";

        assertThrows(InterpreterException.class, () -> run(source));
    }

    @Test
    public void testThatDivisionByZeroThrowsError() {
        String source = "x = 100 / 0";

        assertThrows(InterpreterException.class, () -> run(source));
    }

    @Test
    public void testThatInvalidSyntaxThrowError() {
        String source = "while x < 10 do y = y + 1 x = x + 1"; // missing comma between statements in while body

        assertThrows(InterpreterException.class, () -> run(source));
    }
}

