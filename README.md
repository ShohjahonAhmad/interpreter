# Language Interpreter

A interpreter for an artificial programming language, built in Java. The interpreter reads a source program from standard input, executes it, and prints the values of all variables to standard output.

Implemented using a classic **Lexer → Parser → Interpreter** pipeline:
- **Lexer** — tokenizes raw source text into a list of tokens
- **Parser** — builds an Abstract Syntax Tree (AST) using recursive descent parsing
- **Interpreter** — walks the AST and executes it

## Requirements

- Java 17 or higher
- Maven

## Build

From the project root directory:

```bash
mvn package
```

This produces a jar file in the `target/` directory.

## Run

**Option 1 — Terminal**
```bash
java -jar target/interpreter-1.0-SNAPSHOT.jar
```

**Option 2 — IntelliJ IDEA**

Open the project in IntelliJ IDEA and click the green Run button next to the `main` method in `Main.java`.

**Option 3 — Direct Java (no build needed)**
```bash
java src/main/java/org/jetbrains/Main.java
```

Type your program line by line. When finished, type `quit` on a new line, or submit an empty line to execute.

## Language Features

| Feature | Syntax |
|---|---|
| Variable assignment | `x = 2` |
| Arithmetic | `y = (x + 2) * 2` |
| If / else | `if x > 10 then y = 1 else y = 0` |
| While loop | `while x < 3 do x = x + 1` |
| Multiple statements | `stmt1, stmt2, stmt3` |
| Function definition | `fun add(a, b) { return a + b }` |
| Function call | `result = add(2, 2)` |
| Recursive functions | `fun fact(n) { if n <= 0 then return 1 else return n * fact(n - 1) }` |
| Boolean literals | `true`, `false` |

### Operators

- Arithmetic: `+`, `-`, `*`, `/`
- Comparison: `==`, `!=`, `<`, `<=`, `>`, `>=`

### Operator Precedence (highest to lowest)

1. Parentheses `( )`
2. `*`, `/`
3. `+`, `-`
4. `==`, `!=`, `<`, `<=`, `>`, `>=`

## Examples

**Arithmetic**
```
x = 2
y = (x + 2) * 2
quit
```
```
x: 2
y: 8
```

**If / else**
```
x = 20
if x > 10 then y = 100 else y = 0
quit
```
```
x: 20
y: 100
```

**While loop**
```
x = 0
y = 0
while x < 3 do if x == 1 then y = 10 else y = y + 1, x = x + 1
quit
```
```
x: 3
y: 11
```

**Recursive function**
```
fun fact_rec(n) { if n <= 0 then return 1 else return n * fact_rec(n - 1) }
a = fact_rec(5)
quit
```
```
a: 120
```

## Error Handling

The interpreter throws descriptive errors for:
- Undefined variables
- Undefined functions
- Argument count mismatch
- Invalid syntax
- Unknown operators
