
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class ShuntingYard {

    public ShuntingYard(){}

    private Queue<String> shuntingYard(String expression){
        Queue<String> list= new LinkedList<String>();
        Stack<String> operator=new Stack<>();
        List<String> token=getString(expression);
        for (int i=0; i<token.size();i++){
            if (!token.get(i).isEmpty() && isNumeric(token.get(i)))
                list.add(token.get(i));
            else{
                if (isOperator(token.get(i))) {
                    while (!operator.empty() && !isLeftBracket(operator.peek()) && (precedence(token.get(i)) < precedence(operator.peek()) ||
                            (precedence(token.get(i)) == precedence(operator.peek())) && isleftAssociativity(token.get(i)))){
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
                        throw new IllegalArgumentException("Missing left parentheses");
                }
            }
        }

        while(!operator.empty()){
            if (!isRightBracket(operator.peek()) && !isLeftBracket(operator.peek()))
                list.add(operator.pop());
            else
                throw new IllegalArgumentException("Mismatched parentheses");
        }

        return list;
    }

    private List<String> getString(String expression) {
        List<String> str=new ArrayList<>();

        char character;
        for (int i=0; i<expression.length();i++) {
            character=expression.charAt(i);
            if (isLeftBracket(Character.toString(character)) || isRightBracket(Character.toString(character))
                    || isOperator(Character.toString(character))){
                str.add(Character.toString(character));
            }else if (character>='0' && character<='9') {
                String hold=Character.toString(character);
                i++;
                if (i<expression.length())
                    character=expression.charAt(i);
                while (character >='0' && character<='9' && i<expression.length()) {
                    hold+=Character.toString(character);
                    i++;
                    character=expression.charAt(i);
                }
                i--;
                str.add(hold);
            }
        }
        return str;
    }

    public double compute(String expression){
        double LHS=0;
        double RHS=0;

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
    }

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

    private boolean isNumeric(String str)
    {

        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);
        return str.length() == pos.getIndex();

    }

    private boolean isOperator(String op){
        if (op.length() == 1) {
            char symbol = op.charAt(0);
            if (symbol == '^' ||symbol == '+' || symbol == '-' || symbol == '*' || symbol == '/' || symbol == 'x'
                    || symbol == 'X')
                return true;
        }
        return false;
    }

    private boolean isLeftBracket(String op){
        if (op.length() == 1) {
            char symbol = op.charAt(0);
            if (symbol == '(' )
                return true;
        }
        return false;
    }

    private boolean isRightBracket(String op){
        if (op.length() == 1) {
            char symbol = op.charAt(0);
            if (symbol == ')' )
                return true;
        }
        return false;
    }

    private boolean isleftAssociativity(String op){
        if (op.length() == 1) {
            char symbol = op.charAt(0);
            if (symbol == '+' || symbol == '-' || symbol == '*' || symbol == '/' || symbol == 'x'
                    || symbol == 'X')
                return true;
        }
        return false;
    }

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
