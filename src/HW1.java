import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by GShark on 05.03.2015.
 */

public class HW1 {
    public static void main(String[] args) throws Exception {
        long startTime = System.nanoTime();
        BufferedReader in = new BufferedReader(new FileReader("input.txt"));
        PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get("output.txt"), StandardCharsets.UTF_8));
        String line;
        // adding axioms
        ArrayList<Expression> axioms = new ArrayList<Expression>();
        axioms.add(getExpression("A->B->A"));
        axioms.add(getExpression("(A->B)->(A->B->C)->(A->C)"));
        axioms.add(getExpression("A&B->A"));
        axioms.add(getExpression("A&B->B"));
        axioms.add(getExpression("A->B->A&B"));
        axioms.add(getExpression("A->A|B"));
        axioms.add(getExpression("B->A|B"));
        axioms.add(getExpression("(A->Q)->(B->Q)->(A|B->Q)"));
        axioms.add(getExpression("(A->B)->(A->!B)->!A"));
        axioms.add(getExpression("!!A->A"));
        // expression reading
        ArrayList<Expression> expressions = new ArrayList<Expression>();
        ArrayList<String> input = new ArrayList<String>();
        while ((line = in.readLine()) != null) {
            expressions.add(getExpression(line));
            input.add(line);
        }
        HashMap<Expression, Integer> expressionNumbers = new HashMap<Expression, Integer>();
        HashSet<Expression> expressionSet = new HashSet<Expression>();
        HashMap<Expression, HashSet<Expression>> implicationMap = new HashMap<Expression, HashSet<Expression>>();
        for (int i = 0; i < expressions.size(); i++) {
            out.printf("(%d) %s ", i + 1, input.get(i));
            Expression currentExpression = expressions.get(i);
            //System.out.println(currentExpression.hashCode());
            String annotation = "Не доказано";
            boolean isTrue = false;
            for (int j = 0; j < axioms.size(); j++) {
                if (expressionMatch(axioms.get(j), currentExpression)) {
                    annotation = String.format("Сх. акс. %d", j + 1);
                    isTrue = true;
                    break;
                }
            }
            //System.out.println(currentExpression.equals(new Variable("B")));
            //System.out.println(currentExpression.hashCode());
            //System.out.println((new Variable("B")).hashCode());
            if (implicationMap.containsKey(currentExpression)) {
                HashSet<Expression> lefts = implicationMap.get(currentExpression);
                for (Expression expr : lefts) {
                    if (expressionSet.contains(expr)) {
                        int num1 = expressionNumbers.get(expr);
                        if (!expressionNumbers.containsKey(new Implication(expr, currentExpression))) {
                            throw new Exception("Unknown implication");
                        }
                        int num2 = expressionNumbers.get(new Implication(expr, currentExpression));
                        annotation = String.format("M.P. %d, %d", num1 + 1, num2 + 1);
                        isTrue = true;
                        break;
                    }
                }
            }
            if (isTrue) {
                if (currentExpression instanceof Implication) {
                    Expression first = ((Implication) currentExpression).first;
                    Expression second = ((Implication) currentExpression).second;
                    HashSet<Expression> lefts = implicationMap.get(second);
                    if (lefts == null) {
                        lefts = new HashSet<Expression>();
                    }
                    lefts.add(first);
                    implicationMap.put(second, lefts);
                }
                expressionNumbers.put(currentExpression, i);
                expressionSet.add(currentExpression);
            }
            out.println(String.format("(%s)", annotation));
        }
        in.close();
        out.close();
        long finishTime = System.nanoTime();
        System.out.println(((double)(finishTime - startTime) / 1000000000) + "sec");
    }

    private static Expression getExpression(String s) throws Exception {
        return getExpression(shuntingYard(doGood(s)));
    }

    static String doGood(String s) {
        return s.replaceAll("\\s", "").replaceAll("->", ">");
    }

    static boolean isOperator(char c) {
        return c == '>' || c == '!' || c == '|' || c == '&';
    }

    static boolean isIdent(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
    }

    static int operationPriority(char c) {
        if (c == '!') {
            return 4;
        } else if (c == '&') {
            return 3;
        } else if (c == '|') {
            return 2;
        } else if (c == '>') {
            return 1;
        }
        return 0;
    }

    static boolean isLeftAssoc(char c) {
        return c == '&' || c == '|';
    }

    static int argumentCount(char c) {
        if (c == '!') {
            return 1;
        } else if (c == '>' || c == '|' || c == '&') {
            return 2;
        }
        return 0;
    }

    static ArrayDeque<String> shuntingYard(String s) throws Exception {
        ArrayDeque<String> output = new ArrayDeque<String>();
        ArrayDeque<Character> stack = new ArrayDeque<Character>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (isIdent(c)) {
                String res = "";
                while (i < s.length() && isIdent(s.charAt(i))) {
                    res += s.charAt(i);
                    i++;
                }
                i--;
                output.addLast(res);
            } else if (isOperator(c)) {
                while (!stack.isEmpty()) {
                    char sc = stack.peekLast();
                    if (isOperator(sc) &&
                            ((isLeftAssoc(c) && (operationPriority(c) <= operationPriority(sc)))) ||
                            (!isLeftAssoc(c) && (operationPriority(c) < operationPriority(sc)))) {
                        output.addLast(String.valueOf(sc));
                        stack.removeLast();
                    } else {
                        break;
                    }
                }
                stack.addLast(c);
            } else if (c == '(') {
                stack.addLast(c);
            } else if (c == ')') {
                boolean pe = true;
                while (!stack.isEmpty()) {
                    char sc = stack.peekLast();
                    if (sc == '(') {
                        pe = false;
                        break;
                    } else {
                        output.addLast(String.valueOf(sc));
                        stack.removeLast();
                    }
                }
                if (pe) {
                    throw new Exception("'(' not found");
                }
                stack.removeLast();
            } else {
                throw new Exception("Unknown token");
            }
        }
        while (!stack.isEmpty()) {
            char sc = stack.removeLast();
            if (sc == '(' || sc == ')') {
                throw new Exception("Bad brackets");
            }
            output.addLast(String.valueOf(sc));
        }
        return output;
    }

    static Expression getExpression(ArrayDeque<String> input) throws Exception {
        ArrayDeque<Expression> stack = new ArrayDeque<Expression>();
        while (!input.isEmpty()) {
            String str = input.removeFirst();
            char c = str.charAt(0);
            if (isIdent(c)) {
                stack.addLast(new Variable(str));
            } else if (isOperator(c)) {
                if (stack.size() < argumentCount(c)) {
                    throw new Exception(String.format("Not enough arguments for '%c'", c));
                }
                if (c == '&') {
                    Expression second = stack.removeLast();
                    Expression first = stack.removeLast();
                    stack.addLast(new And(first, second));
                } else if (c == '|') {
                    Expression second = stack.removeLast();
                    Expression first = stack.removeLast();
                    stack.addLast(new Or(first, second));
                }
                if (c == '>') {
                    Expression second = stack.removeLast();
                    Expression first = stack.removeLast();
                    stack.addLast(new Implication(first, second));
                } else if (c == '!') {
                    Expression once = stack.removeLast();
                    stack.addLast(new Not(once));
                }
            }
        }
        if (stack.size() != 1) {
            throw new Exception("stack size not equal 1");
        }
        return stack.removeLast();
    }

    static boolean expressionMatch(Expression pattern, Expression expression) throws Exception {
        return expressionMatch(pattern, expression, new HashMap<String, Expression>());
    }

    static boolean expressionMatch(Expression pattern, Expression expression,
                                   HashMap<String, Expression> vars) throws Exception {
        if (!pattern.getClass().equals(Variable.class) &&
            !pattern.getClass().equals(expression.getClass())) {
            return false;
        }
        if (pattern instanceof Variable) {
            Variable variable = (Variable) pattern;
            if (vars.containsKey(variable.getName())) {
                return vars.get(variable.getName()).equals(expression);
            }
            vars.put(variable.getName(), expression);
            return true;
        } else if (pattern instanceof BinaryOperation) {
            BinaryOperation binOpPat = (BinaryOperation) pattern;
            BinaryOperation binOpExpr = (BinaryOperation) expression;
            return expressionMatch(binOpPat.first, binOpExpr.first, vars) &&
                   expressionMatch(binOpPat.second, binOpExpr.second, vars);
        } else if (pattern instanceof Not) {
            return expressionMatch(((Not) pattern).once, ((Not) expression).once, vars);
        }
        throw new Exception("Pattern contains something strange");
    }
}