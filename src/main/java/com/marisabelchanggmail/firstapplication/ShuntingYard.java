package com.marisabelchanggmail.firstapplication;


import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 *  Parsing mathematical expressions specified in infix notation. Evaluate and compute the expression
 */
public class ShuntingYard {

    public ShuntingYard(){}

    /**
     * Parsing mathematical expressions specified in infix notation and produce an abstract syntax tree
     * @param expression infix notation
     * @return a queue with the abstract syntax tree
     * @throws NumberFormatException if there is more operators than numbers or the parentheses are mismatched
     */
    private Queue<String> shuntingYard(String expression) throws NumberFormatException{
        Queue<String> list= new LinkedList<String>();
        Stack<String> operator=new Stack<>();
        try {
            List<String> token=getString(expression);
            for (int i=0; i<token.size();i++){
                if (!token.get(i).isEmpty() && isNumeric(token.get(i)))
                    list.add(token.get(i));
                else{
                    if (isOperator(token.get(i))) {
                        while (!operator.empty() && !isLeftBracket(operator.peek()) && (precedence(token.get(i)) < precedence(operator.peek()) ||
                                (precedence(token.get(i)) == precedence(operator.peek())) && isLeftAssociativity(token.get(i)))){
                            list.add(operator.pop());
                        }
                        operator.push(token.get(i));
                    }
                    else if (isLeftBracket(token.get(i)))
                        operator.push(token.get(i));
                    else if (isRightBracket(token.get(i))){
                        while (!operator.empty() && !isLeftBracket(operator.peek())){
                            list.add(operator.pop());
                        }
                        if (isLeftBracket(operator.peek()))
                            operator.pop();
                        else
                            throw new NumberFormatException("Missing left parentheses");
                    }
                }
            }
        }catch(NumberFormatException e){
            throw new  NumberFormatException(e.getMessage());
        }

        while(!operator.empty()){
            if (!isRightBracket(operator.peek()) && !isLeftBracket(operator.peek()))
                list.add(operator.pop());
            else
                throw new NumberFormatException("Mismatched parentheses");
        }

        return list;
    }

    /**
     * Separate the expression into tokens
     * @param expression an expression in infix notation
     * @return a string in infix notation
     * @throws NumberFormatException if there is more operators than numbers or the parentheses are mismatched
     */
    private List<String> getString(String expression)throws NumberFormatException {
        List<String> str=new ArrayList<>();
        int numOp=0;
        int num=0;
        char character;
        for (int i=0; i<expression.length();i++) {
            character=expression.charAt(i);
            if (isLeftBracket(Character.toString(character)) || isRightBracket(Character.toString(character))){
                str.add(Character.toString(character));
            }else if (character == '-' && (i == 0 || isPreviewOperator(i-1, expression)) ||(character>='0' && character<='9')){
                String hold=Character.toString(character);
                i++;
                if (i<expression.length())
                    character=expression.charAt(i);
                while (character >='0' && character<='9' && i<expression.length()) {
                    hold+=Character.toString(character);
                    i++;
                    if (i<expression.length())
                        character=expression.charAt(i);
                }
                i--;
                str.add(hold);
                num+=1;
            }else if (isOperator(Character.toString(character))) {
                str.add(Character.toString(character));
                numOp+=1;
            }
        }
        System.out.println(str);
        if ((num-numOp) == 1)
            return str;
        throw new NumberFormatException("There is more operator than operand");
    }

    /**
     * Check if the previous token is an operator or left parenthesis
     * @param pos the position of the current token
     * @param expression an expression in infix notation
     * @return true if the previous token is an left parenthesis or an operator otherwise false
     */
    private boolean isPreviewOperator(int pos,String expression) {
        for (;pos>=0 ; pos--) {
            if (isOperator(Character.toString(expression.charAt(pos)))|| isLeftBracket(Character.toString(expression.charAt(pos)))) {
                return true;
            }else if(expression.charAt(pos)!= ' ') {
                return false;
            }
        }
        return false;
    }

    /**
     * Compute the expression
     * @param expression an expression in infix notation
     * @return the total as a double
     * @throws NumberFormatException if there is more operators than numbers or the parentheses are mismatched
     */
    public double compute(String expression)throws NumberFormatException{
        double LHS=0;
        double RHS=0;

        try {
            Queue<String> list= shuntingYard(expression);
            Stack<Double> total=new Stack<>();


            while(!list.isEmpty()){
                if (isNumeric(list.peek()))
                    total.push(Double.parseDouble(list.remove()));
                else{
                    RHS=total.pop();
                    LHS=total.pop();
                    total.push(compute(LHS, RHS,list.remove()));
                }
            }

            return total.pop();
        }catch ( NumberFormatException e) {
            throw new  NumberFormatException(e.getMessage());
        }
    }

    /**
     * Compute the specific operator
     * @param LHS LHS of the operand
     * @param RHS RHS of the operand
     * @param op operator
     * @return the result of the computation
     */
    private double compute(double LHS, double RHS,String op){
        switch (op){
            case "+": return LHS + RHS;
            case "-": return LHS - RHS;
            case "/": return LHS / RHS;
            case "*":
            case "x":
            case "X": return LHS * RHS;
            case "^": return Math.pow(LHS, RHS);
        }
        return 0;
    }

    /**
     * Check if the token is a number
     * @param str the token to be evaluated
     * @return true if the token is a number otherwise false
     */
    private boolean isNumeric(String str)
    {

        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);
        return str.length() == pos.getIndex();

    }

    /**
     * check if the token is an operator
     * @param op operator
     * @return true if the token is an operator otherwise false
     */
    private boolean isOperator(String op){
        if (op.length() == 1) {
            char symbol = op.charAt(0);
            if (symbol == '^' ||symbol == '+' || symbol == '-' || symbol == '*' || symbol == '/' || symbol == 'x'
                    || symbol == 'X')
                return true;
        }
        return false;
    }

    /**
     * check if the token is a left bracket
     * @param op operator
     * @return true if the token is a left bracket otherwise false
     */
    private boolean isLeftBracket(String op){
        if (op.length() == 1) {
            char symbol = op.charAt(0);
            if (symbol == '(' )
                return true;
        }
        return false;
    }

    /**
     * check if the token is a right bracket
     * @param op operator
     * @return true if the token is a right bracket otherwise false
     */
    private boolean isRightBracket(String op){
        if (op.length() == 1) {
            char symbol = op.charAt(0);
            if (symbol == ')' )
                return true;
        }
        return false;
    }

    /**
     * check if the operator is a left associative
     * @param op operator
     * @return true if the operator is a left associative otherwise false
     */
    private boolean isLeftAssociativity(String op){
        if (op.length() == 1) {
            char symbol = op.charAt(0);
            if (symbol == '+' || symbol == '-' || symbol == '*' || symbol == '/' || symbol == 'x'
                    || symbol == 'X')
                return true;
        }
        return false;
    }

    /**
     * check if the precedence of an operator
     * @param op operator
     * @return the value of the precedence of the operator
     */
    private int precedence(String op){
        int value=0;
        switch (op){
            case "^":
                value=3;
                break;
            case "/":
            case "*":
            case "x":
            case "X":
                value=2;
                break;
            case "+":
            case "-":
                value=1;
                break;
            default:
                throw new IllegalArgumentException(op + " IS NOT A OPERATOR");
        }
        return value;
    }
}