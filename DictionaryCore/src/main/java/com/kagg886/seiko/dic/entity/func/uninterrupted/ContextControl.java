package com.kagg886.seiko.dic.entity.func.uninterrupted;

import com.kagg886.seiko.dic.DictionaryUtil;
import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.session.AbsRuntime;

import java.util.List;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
 * @className: ContextControl
 * @author: kagg886
 * @description: 上下文管理类
 * @date: 2023/2/1 11:39
 * @version: 1.0
 */
public abstract class ContextControl extends Function.UnInterruptedFunction {


    public ContextControl(int line, String code) {
        super(line, code);
    }


    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: NewVar
     * @author: kagg886
     * @description: $变量赋值 变量名 值$
     * @date: 2023/1/28 21:49
     * @version: 1.0
     */
    public static class SetVar extends ContextControl {

        public SetVar(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String name = args.get(0).toString();
            Object value = args.get(1);
            if (value instanceof String) {
                value = DictionaryUtil.cleanVariableCode((String) value, runtime);
            }
            runtime.getRuntimeObject().put(name, value);
        }
    }

    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: CheckExists
     * @author: kagg886
     * @description: $变量检验 要存入的变量 要检验的变量$
     * @date: 2023/1/29 9:03
     * @version: 1.0
     */
    public static class CheckExists extends ContextControl {

        public CheckExists(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String in = args.get(0).toString();
            String out = args.get(1).toString();
            runtime.getRuntimeObject().put(in, String.valueOf(runtime.getRuntimeObject().containsKey(out)));
        }
    }


    /**
     * @projectName: Seiko
     * @package: com.kagg886.seiko.dic.entity.func.uninterrupted
     * @className: DelVar
     * @author: kagg886
     * @description: $变量删除 变量名$
     * @date: 2023/1/28 21:54
     * @version: 1.0
     */
    public static class DelVar extends ContextControl {

        public DelVar(int line, String code) {
            super(line, code);
        }

        @Override
        protected void run(AbsRuntime<?> runtime, List<Object> args) {
            String name = args.get(0).toString();
            runtime.getRuntimeObject().remove(name);
        }
    }
}