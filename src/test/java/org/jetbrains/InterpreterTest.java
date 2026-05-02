package org.jetbrains;

import org.junit.jupiter.api.Test;

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
}
