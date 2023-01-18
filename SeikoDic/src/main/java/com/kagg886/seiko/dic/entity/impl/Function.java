package com.kagg886.seiko.dic.entity.impl;

import com.kagg886.seiko.dic.DictionaryUtil;
import com.kagg886.seiko.dic.entity.DictionaryCode;
import com.kagg886.seiko.dic.session.AbsRuntime;

import java.util.ArrayList;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.impl
 * @className: Function
 * @author: kagg886
 * @description: 代表一个函数，最好不要直接继承它。
 * @date: 2023/1/16 17:25
 * @version: 1.0
 */
public abstract class Function extends DictionaryCode {
    private static final String[][] interruptedFunctionNames = { //阻断方法列表，后面的是Class名
            {"延时", "Delay"}
    };
    private static final String[][] uninterruptedFunctionNames = { //阻断方法列表，后面的是Class名
            {"图片", "addImage"}
    };
    protected ArrayList<String> args;
    private final String[] sourceArgs;

    public Function(int line, String code) {
        super(line, code);
        int sIndex = code.indexOf(" ");
        code = code.substring(sIndex + 1, code.length() - 1);
        sourceArgs = code.split(" ");
    }

    public static Function parseFunction(String dicLine, int line) throws Throwable { //一定是$xxxx a b c$
        for (String[] p : interruptedFunctionNames) {
            int spaceIndex = dicLine.indexOf(" ");
            if (spaceIndex == -1) {
                spaceIndex = dicLine.length();
            }
            String command = dicLine.substring(1, spaceIndex);
            if (command.equals(p[0])) {
                Class<?> func = Class.forName("com.kagg886.seiko.dic.entity.func.interrupted." + p[1]);
                return (Function) func.getConstructor(int.class, String.class).newInstance(line, dicLine);
            }
        }

        for (String[] p : uninterruptedFunctionNames) {
            int spaceIndex = dicLine.indexOf(" ");
            if (spaceIndex == -1) {
                spaceIndex = dicLine.length();
            }
            String command = dicLine.substring(1, spaceIndex);
            if (command.equals(p[0])) {
                Class<?> func = Class.forName("com.kagg886.seiko.dic.entity.func.uninterrupted." + p[1]);
                return (Function) func.getConstructor(int.class, String.class).newInstance(line, dicLine);
            }
        }
        throw new NoSuchFieldException("未找到词库方法");
    }

    public void invoke(AbsRuntime runtime) {
        args = new ArrayList<>();
        for (String s : sourceArgs) {
            args.add(DictionaryUtil.cleanVariableCode(s, runtime));
        }
        run(runtime);
    }

    protected abstract void run(AbsRuntime runtime);

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.impl
     * @className: InterruptedFunction
     * @author: kagg886
     * @description: 代表一个缓冲区阻断函数。在调用此函数前，需要清空并发送缓冲区内内容。
     * @date: 2023/1/18 11:43
     * @version: 1.0
     */
    public static abstract class InterruptedFunction extends Function {

        public InterruptedFunction(int line, String code) {
            super(line, code);
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.impl
     * @className: InterruptedFunction
     * @author: kagg886
     * @description: 代表一个缓冲区非阻断函数。在调用此函数前，不需要清空并发送缓冲区内内容。
     * @date: 2023/1/18 11:45
     * @version: 1.0
     */
    public static abstract class UnInterruptedFunction extends Function {

        public UnInterruptedFunction(int line, String code) {
            super(line, code);
        }
    }
}