import java.math.BigDecimal;

public class Calculater {
    public static String getResult(String exp) {
        //exp = exp.replaceAll(" ", "");

        //判断明显错误的表达式
        char lastCh = exp.charAt(exp.length() - 1);
        if (!Character.isDigit(lastCh))
            if (lastCh != ')')
                return null;
        char firstCh = exp.charAt(0);
        if (!Character.isDigit(firstCh))
            if (firstCh != '(' && firstCh != '-')
                return null;

        //去括号
        while (exp.indexOf('(') != -1 && exp.indexOf(')') != -1) {
            int start = -1;
            int end = -1;
            for (int i = 0; i < exp.length(); i++) {
                if (exp.charAt(i) == '(') start = i;
                else if (exp.charAt(i) == ')') {
                    end = i;
                    break;
                }
            }
            String str = exp.substring(start + 1, end);
            double result = Double.parseDouble(getResult(str));
            exp = exp.replace('(' + str + ')', "" + result);
        }

        Stack<BigDecimal> operand = new Stack<>();
        Stack<Character> operator = new Stack<>();

        //解析字符串，提取数字与运算符并压入对应栈中
        int differ = 0;
        for (int i = 0; i < exp.length(); i++) {
            char ch = exp.charAt(i);
            if ((differ == 0 && ch == '-') || Character.isDigit(ch) || ch == '.')   //提取数字
                differ++;
            else {
                if (differ != 0) {
                    String str = exp.substring(i - differ, i);
                    if (str.indexOf('.') != str.lastIndexOf('.')) {
                        return null;
                    }
                    operand.push(new BigDecimal(str));
                }
                operator.push(exp.charAt(i));
                differ = 0;
            }
        }
        operand.push(new BigDecimal(exp.substring(exp.length() - differ, exp.length())));

        if (operand.size() <= operator.size()) {
            return null;
        }

        //颠倒栈
        Stack<Character> operator_temp = new Stack<>();
        while (!operator.isEmpty())
            operator_temp.push(operator.pop());
        Stack<BigDecimal> operand_temp = new Stack<>();
        while (!operand.isEmpty())
            operand_temp.push(operand.pop());
        operand = operand_temp;
        operator = operator_temp;

        //运算
        while (operator.size() >= 2) {
            char op1 = operator.pop();
            char op2 = operator.pop();
            if (op2 == '+' || op2 == '-') {
                operator.push(op2);
                BigDecimal leftOperand = operand.pop();
                BigDecimal rightOperand = operand.pop();
                String str = simpleCalculate(leftOperand, rightOperand, op1);
                if (str == null)
                    return null;
                BigDecimal result = new BigDecimal(str);
                operand.push(result);
            } else {
                operator.push(op1);
                BigDecimal firstOperand = operand.pop();
                BigDecimal leftOperand = operand.pop();
                BigDecimal rightOperand = operand.pop();
                String str = simpleCalculate(leftOperand, rightOperand, op2);
                if (str == null)
                    return null;
                BigDecimal result = new BigDecimal(str);
                operand.push(result);
                operand.push(firstOperand);
            }
        }
        if (operand.size() == 1) {
            if (operator.size() > 0)
                return "-" + operand.pop() + "";
            else return operand.pop() + "";
        }
        BigDecimal leftOperand = operand.pop();
        BigDecimal rightOperand = operand.pop();
        return simpleCalculate(leftOperand, rightOperand, operator.pop());
    }

    private static String simpleCalculate(BigDecimal leftOperand, BigDecimal rightOperand, char op) {
        switch (op) {
            case '+':
                return leftOperand.add(rightOperand) + "";
            case '-':
                return leftOperand.subtract(rightOperand) + "";
            case '*':
                return leftOperand.multiply(rightOperand) + "";
            case '/':
                if (rightOperand.equals("0")) {
                    return null;
                } else return leftOperand.divide(rightOperand) + "";
            default:
                return null;
        }
    }
}