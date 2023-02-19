package com.kagg886.seiko.dic;

import com.kagg886.seiko.dic.exception.DictionaryOnRunningException;
import com.kagg886.seiko.dic.session.AbsRuntime;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic
 * @className: DictionaryUtil
 * @author: kagg886
 * @description: 伪代码常用操作
 * @date: 2023/1/16 19:06
 * @version: 1.0
 */
public class DictionaryUtil {

    /*
     * @param code:参数表
     * @param runtime:运行时
     * @return Object
     * @author kagg886
     * @description 用于函数，将参数变量提取成字符串
     * @date 2023/01/19 18:51
     */
    public static List<Object> variableToObject(String code, AbsRuntime<?> runtime) {
        Object[] args = code.split(" ");
        ArrayList<Object> k = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            String arg = (String) args[i];
            if (arg.startsWith("%") && arg.endsWith("%")) {//是变量
                k.add(runtime.getRuntimeObject().getOrDefault(arg.substring(1, arg.length() - 1), "null"));
                continue;
            }
            k.add(cleanVariableCode(arg, runtime));
        }
        return k;
    }

    /*
     * @param :
     * @return String
     * @author kagg886
     * @description 变量转换成字符串
     * @date 2023/01/13 09:44
     */
    public static String cleanVariableCode(String code, AbsRuntime<?> runtime) {
        //变量替换成常量
        String clone = code.replace("\\n", "\n");
        for (String s : runtime.getRuntimeObject().keySet()) { //s一定是String
            String var = "%" + s + "%";
            if (clone.contains(var)) {
                Object q = runtime.getRuntimeObject().get(s);
                if (q == null) {
                    q = "null";
                }
                clone = clone.replace(var, q.toString());
            }
        }

        try {
            //计算表达式，若出错则不计算
            int xLeft = 0;
            while ((xLeft = clone.indexOf("[", xLeft)) != -1) {
                int xRight = clone.indexOf("]", xLeft);
                String expression = clone.substring(xLeft + 1, xRight);
                String result = String.valueOf(DictionaryUtil.mathExpressionCalc(expression));
                clone = clone.replace("[" + expression + "]", result);
                xLeft = xRight;
            }
        } catch (Exception ignored) {
        }

        return clone;
    }


    /*
     * @param str: 传入的布尔表达式
     * @return boolean
     * @author kagg886
     * @description 计算布尔表达式
     * @date 2023/01/28 21:33
     */
    public static boolean compareAsString(String str) {
        if (str == null || str.equals("")) {
            throw new NullPointerException("表达式为空");
        }
        str = str.replace(" ", "");

        if (str.equals("true")) {
            return true;
        }

        if (str.equals("false")) {
            return false;
        }

        if (str.contains(")")) { //
            int lIndex = str.lastIndexOf("(");
            int rIndex = str.indexOf(")", lIndex);
            boolean p = compareAsString(str.substring(lIndex + 1, rIndex));
            return compareAsString(str.replace("(" + str.substring(lIndex + 1, rIndex) + ")", Boolean.toString(p)));
        }

        if (str.contains("||")) {
            int idx = str.indexOf("||");
            return compareAsString(str.substring(0, idx)) || compareAsString(str.substring(idx + 2));
        }

        if (str.contains("&&")) {
            int idx = str.indexOf("&&");
            return compareAsString(str.substring(0, idx)) && compareAsString(str.substring(idx + 2));
        }

        if (str.contains("==")) {
            int idx = str.indexOf("==");
            return Objects.equals(mathExpressionCalc(str.substring(0, idx)), mathExpressionCalc(str.substring(idx + 2)));
        }
        if (str.contains(">=")) {
            int idx = str.indexOf(">=");
            return mathExpressionCalc(str.substring(0, idx)) >= mathExpressionCalc(str.substring(idx + 2));
        }
        if (str.contains("<=")) {
            int idx = str.indexOf("<=");
            return mathExpressionCalc(str.substring(0, idx)) <= mathExpressionCalc(str.substring(idx + 2));

        }
        if (str.contains(">")) {
            int idx = str.indexOf(">");
            return mathExpressionCalc(str.substring(0, idx)) > mathExpressionCalc(str.substring(idx + 1));
        }
        if (str.contains("<")) {
            int idx = str.indexOf("<");
            return mathExpressionCalc(str.substring(0, idx)) < mathExpressionCalc(str.substring(idx + 1));

        }
        throw new RuntimeException("计算表达式出错!" + str);
    }


    public static Double mathExpressionCalc(String str) {
        if (str.equals("")) {
            throw new DictionaryOnRunningException("中括号内不能为空");
        }
        Double a = null;
        try {
            a = Double.parseDouble(str);
        } catch (NumberFormatException ignored) {
        }

        if (str.isEmpty() || a != null) {
            return str.isEmpty() ? 0 : a;
        }

        if (str.contains(")")) {
            // 最后一个左括号
            int lIndex = str.lastIndexOf("(");
            // 对于的右括号
            int rIndex = str.indexOf(")", lIndex);
            return mathExpressionCalc(str.substring(0, lIndex) + mathExpressionCalc(str.substring(lIndex + 1, rIndex)) + str.substring(rIndex + 1));
        }
        if (str.contains("+")) {
            int index = str.lastIndexOf("+");
            return mathExpressionCalc(str.substring(0, index)) + mathExpressionCalc(str.substring(index + 1));
        }
        if (str.contains("-")) {
            int index = str.lastIndexOf("-");
            return mathExpressionCalc(str.substring(0, index)) - mathExpressionCalc(str.substring(index + 1));
        }
        if (str.contains("*")) {
            int index = str.lastIndexOf("*");
            return mathExpressionCalc(str.substring(0, index)) * mathExpressionCalc(str.substring(index + 1));
        }
        if (str.contains("/")) {
            int index = str.lastIndexOf("/");
            return mathExpressionCalc(str.substring(0, index)) / mathExpressionCalc(str.substring(index + 1));
        }

        if (str.contains("^")) {
            int index = str.lastIndexOf("^");
            return Math.pow(mathExpressionCalc(str.substring(0, index)), mathExpressionCalc(str.substring(index + 1)));
        }

        if (str.contains("%")) {
            int index = str.lastIndexOf("%");
            return mathExpressionCalc(str.substring(0, index)) % mathExpressionCalc(str.substring(index + 1));
        }
        // 出错
        throw new RuntimeException("无法解析的表达式:" + str);
    }
}
