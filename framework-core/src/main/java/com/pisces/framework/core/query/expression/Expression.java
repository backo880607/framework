package com.pisces.framework.core.query.expression;

import com.pisces.framework.core.config.CoreMessage;
import com.pisces.framework.core.exception.ExpressionException;
import com.pisces.framework.core.query.expression.calculate.*;

import java.util.AbstractMap;
import java.util.Map;

/**
 * @author jason
 * @date 2023/10/29
 */
public class Expression {
    public ExpressionNode root = null;
    public boolean hasField = false;

    public boolean parse(String str) {
        Map.Entry<Integer, ExpressionNode> entry = this.create(str, 0, false);
        if (entry.getKey() < str.length()) {
            return false;
        }

        this.root = entry.getValue();
        return this.root != null;
//        return getReturnClass() != null;
    }

    public Map.Entry<Integer, ExpressionNode> create(String str, int index, boolean bFun) {
        boolean bError = false;
        ExpressionNode root = null;
        while (index < str.length()) {
            char curChar = str.charAt(index);
            if (Character.isSpaceChar(curChar)) {
                ++index;
                continue;
            }

            if (curChar == '#') {
                Calculate cal = new DateTimeCalculate();
                index = cal.parse(str, index);
                root = createRightNode(root, cal);
            } else if (curChar == '\'') {
                Calculate cal = new TextCalculate();
                index = cal.parse(str, index);
                root = createRightNode(root, cal);
            } else if (curChar == '(') {
                BracketCalculate expCalc = new BracketCalculate();
                index = expCalc.parse(str, index);
                root = createRightNode(root, expCalc);
            } else if (curChar == ')') {
                break;
            } else if (curChar == ',') {
                if (!bFun) {
                    throw new ExpressionException(index, index + 1, CoreMessage.NotSupportOperation);
                }
                ++index;
                break;
            } else if (Character.isDigit(curChar)) {
                Map.Entry<Integer, Calculate> cal = this.getNumberCalculate(str, index);
                index = cal.getKey();
                root = createRightNode(root, cal.getValue());
            } else {
                Map.Entry<Integer, OperationType> calType = this.getCallType(str, index);
                index = calType.getKey();
                if (calType.getValue() != OperationType.DATA) {
                    Calculate cal = new OperTypeCalculate(calType.getValue());
                    if (root == null) {
                        root = new ExpressionNode(calType.getValue(), cal, null, null);
                    } else if (calType.getValue().value() < root.type.value()) {
                        // 优先级更高
                        root.rchild = new ExpressionNode(calType.getValue(), cal, root.rchild, null);
                    } else { // 作为优先级更小的左子树
                        root = new ExpressionNode(calType.getValue(), cal, root, null);
                    }
                } else {
                    // 自定义函数调用
                    Map.Entry<Integer, Calculate> cal = this.getFunOrFieldCalculate(str, index);
                    index = cal.getKey();
                    if (index < 0) {
                        bError = true;
                        break;
                    }

                    root = createRightNode(root, cal.getValue());
                }
            }
        }

        if (bError) {
            root = null;
        }

        return new AbstractMap.SimpleEntry<>(index, root);
    }

    private ExpressionNode createRightNode(ExpressionNode root, Calculate cal) {
        if (root == null) {
            root = new ExpressionNode(OperationType.DATA, cal, null, null);
        } else if (root.rchild == null) {
            root.rchild = new ExpressionNode(OperationType.DATA, cal, null, null);
        } else {
            root.rchild.rchild = new ExpressionNode(OperationType.DATA, cal, null, null);
        }

        return root;
    }

    private Map.Entry<Integer, OperationType> getCallType(String str, int index) {
        OperationType operType = OperationType.DATA;
        switch (str.charAt(index)) {
            case '+':
                operType = OperationType.PLUS;
                break;
            case '-':
                operType = OperationType.MINUS;
                break;
            case '*':
                operType = OperationType.MULTIPLIED;
                break;
            case '/':
                operType = OperationType.DIVIDED;
                break;
            case '%':
                operType = OperationType.MOD;
                break;
            case '&':
                if (str.charAt(++index) == '&') {
                    operType = OperationType.AND;
                }
                break;
            case '|':
                if (str.charAt(++index) == '|') {
                    operType = OperationType.OR;
                }
                break;
            case '=':
                operType = OperationType.EQUAL;
                if (str.charAt(index + 1) != '=') {
                    throw new ExpressionException(index, index + 1, CoreMessage.EqualTypeError, str.substring(index, index + 1));
                }
                ++index;
                break;
            case '!':
                if (str.charAt(index + 1) == '=') {
                    operType = OperationType.NOTEQUAL;
                    ++index;
                } else {
                    operType = OperationType.NOT;
                }
                break;
            case '>':
                if (str.charAt(index + 1) == '=') {
                    operType = OperationType.GREATEREQUAL;
                    ++index;
                } else {
                    operType = OperationType.GREATER;
                }
                break;
            case '<':
                if (str.charAt(index + 1) == '=') {
                    operType = OperationType.LESSEQUAL;
                    ++index;
                } else {
                    operType = OperationType.LESS;
                }
                break;
            default:
                break;
        }

        int rtIndex = operType != OperationType.DATA ? index + 1 : index;
        return new AbstractMap.SimpleEntry<>(rtIndex, operType);
    }

    /**
     * 解析除整数或者浮点数
     */
    private Map.Entry<Integer, Calculate> getNumberCalculate(String str, int index) {
        int temp = index;
        do {
            ++index;
        } while (index < str.length() && Character.isDigit(str.charAt(index)));

        if (index < str.length() && str.charAt(index) == '.') {
            if (!Character.isDigit(str.charAt(++index))) {
                throw new ExpressionException(temp, index, CoreMessage.DoubleError, str.substring(temp, index));
            }

            do {
                ++index;
            } while (index < str.length() && Character.isDigit(str.charAt(index)));
            DoubleCalculate calculate = new DoubleCalculate(Double.parseDouble(str.substring(temp, index)));
            return new AbstractMap.SimpleEntry<>(index, calculate);
        }

        LongCalculate calculate = new LongCalculate(Long.parseLong(str.substring(temp, index)));
        return new AbstractMap.SimpleEntry<>(index, calculate);
    }

    private Map.Entry<Integer, Calculate> getFunOrFieldCalculate(String str, int index) {
        int startIndex = index;
        int temp = index;
        while (temp < str.length()) {
            if (str.charAt(temp) == '.') {
                Calculate calculate = new EnumCalculate();
                index = calculate.parse(str, index);
                if (index == -1) {
                    // 非枚举类型
                    calculate = new PropertyCalculate();
                    index = calculate.parse(str, startIndex);
                    if (index == -1) {
                        break;
                    }
                    this.hasField = true;
                }

                return new AbstractMap.SimpleEntry<>(index, calculate);
            } else if (str.charAt(temp) == '(') {
//                Calculate calculate;
//                String name = str.substring(startIndex, temp);
//                if ("Reverse".equalsIgnoreCase(name)) {
//                    calculate = new ReverseCalculate();
//                    ++temp;
//                    index = calculate.parse(str, temp);
//                } else {
//                    calculate = new FunctionCalculate();
//                    index = calculate.parse(str, index);
//                }
//
//                if (index == -1) {
//                    break;
//                }
//
//                return new AbstractMap.SimpleEntry<>(index, calculate);
            }

            if (!Character.isAlphabetic(str.charAt(temp)) && !Character.isDigit(str.charAt(temp)) && str.charAt(temp) != '_') {
                Calculate calculate = new BeanCalculate();
                index = calculate.parse(str, index);
                return new AbstractMap.SimpleEntry<>(index, calculate);
            }
            ++temp;
        }
        return new AbstractMap.SimpleEntry<>(-1, null);
    }

    public Class<?> getReturnClass() {
        if (root == null) {
            return null;
        }
        return getReturnClassImpl(root);
    }

    private Class<?> getReturnClassImpl(ExpressionNode node) {
        if (node.type == OperationType.DATA) {
            return node.calculate.getReturnClass();
        }

        Class<?> lClass = null;
        if (node.lchild != null) {
            lClass = this.getReturnClassImpl(node.lchild);
        }
        Class<?> rClass = null;
        if (node.rchild != null) {
            rClass = this.getReturnClassImpl(node.rchild);
        }
        return node.<OperTypeCalculate>getCalculate().getReturnClass(lClass, rClass);
    }
}
