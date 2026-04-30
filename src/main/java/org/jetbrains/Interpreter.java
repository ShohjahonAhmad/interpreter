package org.jetbrains;

import org.jetbrains.exception.InterpreterException;
import org.jetbrains.exception.ReturnException;
import org.jetbrains.node.*;

import java.util.*;

public class Interpreter {
    private final Map<String, Integer> variables = new LinkedHashMap<>();
    private final Map<String, FuncNode> functions = new HashMap<>();

    public void interpret(List<Node> program) {
        for (Node statement : program) {
            execute(statement);
        }
    }

    private int execute(Node node) {
        if(node instanceof NumberNode n){
            return n.value;
        } else if (node instanceof IdentifierNode n){
            if(!variables.containsKey(n.name)) throw new RuntimeException("Undefined variable: " + n.name);
            return variables.get(n.name);
        } else if(node instanceof AssignNode n){
            return getResultOfAssignNode(n);
        } else if(node instanceof BinaryOpNode n) {
            return getResultOfBinaryOpNode(n);
        } else if(node instanceof FuncCallNode n) {
            return getResultOfFuncCallNode(n);
        } else if (node instanceof ReturnNode n) {
            throw new ReturnException(execute(n.value));
        }else if(node instanceof IfNode n) {
            executeIfNode(n);
        } else if(node instanceof WhileNode n) {
            executeWhileNode(n);
        } else if(node instanceof FuncNode n) {
            functions.put(n.name, n);
        }

        return 0;
    }

    public void printVariables() {
        for(String key : variables.keySet()){
            System.out.println(key + ": " + variables.get(key));
        }
    }

    private int getResultOfAssignNode(AssignNode n){
        int value = execute(n.expression);
        variables.put(n.identifier, value);
        return value;
    }

    private void executeWhileNode(WhileNode n){
        while(execute(n.condition) != 0){
            for(Node statement : n.body){
                try{
                    execute(statement);
                } catch (ReturnException e){
                    throw new ReturnException(e.value);
                }
            }
        }
    }

    private void executeIfNode(IfNode n){
        int condition = execute(n.condition);

        if(condition != 0) {
            execute(n.trueBranch);
        } else if(n.falseBranch != null){
            execute(n.falseBranch);
        }
    }

    private int getResultOfFuncCallNode(FuncCallNode n){
        if(!functions.containsKey(n.name)) throw new InterpreterException("Undefined function: " + n.name);
        FuncNode func = functions.get(n.name);

        if(func.params.size() != n.args.size()) throw new InterpreterException("Argument count mismatch for function: " + n.name);

        List<Integer> args = new ArrayList<>();
        for(int i = 0; i < func.params.size(); i++){
            int value = execute(n.args.get(i));
            args.add(value);
        }

        Map<String, Integer> oldVars = new HashMap<>(variables);
        variables.clear();

        for(int i = 0; i < func.params.size(); i++){
            variables.put(func.params.get(i), args.get(i));
        }

        int result = 0;
        try {
            for(Node statement : func.body) {
                execute(statement);
            }
        } catch (ReturnException e) {
            result = e.value;
        }

        variables.clear();
        variables.putAll(oldVars);
        return result;
    }

    private int getResultOfBinaryOpNode(BinaryOpNode n) {
        int left = execute(n.left);
        int right = execute(n.right);

        int result;
        if(isComparison(n.operator)){
            result = compare(n.operator, left, right) ? 1 : 0;
        } else {
            result = compute(n.operator, left, right);
        }
        return result;
    }

    private boolean compare(String operator, int left, int right) {
        if("==".equals(operator)) return left == right;
        if(">=".equals(operator)) return left >= right;
        if("<=".equals(operator)) return left <= right;
        if("!=".equals(operator)) return left != right;
        if(">".equals(operator)) return left > right;
        if("<".equals(operator)) return left < right;
        throw new InterpreterException("Unknown operator: " + operator);
    }

    private int compute(String operator, int left, int right) {
        if("+".equals(operator)) return left + right;
        if("-".equals(operator)) return left - right;
        if("*".equals(operator)) return left * right;
        if("/".equals(operator)) return left / right;
        throw new InterpreterException("Unknown operator: " + operator);
    }

    private boolean isComparison(String operator) {
        return "==".equals(operator) || "!=".equals(operator) || "<".equals(operator) || ">".equals(operator) || "<=".equals(operator) || ">=".equals(operator);
    }
}
