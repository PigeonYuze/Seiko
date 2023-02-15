package com.kagg886.seiko.dic.entity;

import com.kagg886.seiko.dic.entity.func.Function;
import com.kagg886.seiko.dic.entity.impl.Expression;
import com.kagg886.seiko.dic.entity.impl.PlainText;
import com.kagg886.seiko.dic.exception.DictionaryOnLoadException;
import com.kagg886.seiko.util.ArrayIterator;
import com.kagg886.seiko.util.IOUtil;
import com.kagg886.seiko.util.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.entity
 * @className: Dictionary
 * @author: kagg886
 * @description: 代表了一个伪代码实例
 * @date: 2023/1/9 19:34
 * @version: 1.0
 */
public class DictionaryFile {
    private static final boolean STRICT_MODE = false;
    private final File dicFile;
    private final HashMap<DictionaryCommandMatcher, ArrayList<DictionaryCode>> commands = new HashMap<DictionaryCommandMatcher, ArrayList<DictionaryCode>>() {
        @Override
        public ArrayList<DictionaryCode> put(DictionaryCommandMatcher key, ArrayList<DictionaryCode> value) {
            if (STRICT_MODE) {
                for (DictionaryCommandMatcher d : keySet()) {
                    if (key.equals(d)) {
                        throw new DictionaryOnLoadException(dicFile.getName() + "含有冲突指令:" + key.getLine() + "-" + d.getLine());
                    }
                }
            }
            return super.put(key, value);
        }
    };

    public DictionaryFile(File dicFile) throws Throwable {
        this.dicFile = dicFile;
        String dicCodes = IOUtil.loadStringFromFile(dicFile.getAbsolutePath()).replace("\r", "");
        if (dicCodes.length() == 0) {
            throw new DictionaryOnLoadException("[" + dicFile.getName() + "]为空!");
        }
        String[] lines = dicCodes.split("\n");
        int start = 0;
        for (int i = 0; i < lines.length; i++) {
            if (!TextUtils.isEmpty(lines[i])) {
                start = i;
                break;
            }
        }
        ArrayIterator<String> iterator = new ArrayIterator<>(lines);
        iterator.setLen(start);

        boolean behindLineIsEmpty = true;
        String commandRegex = null;
        ArrayList<DictionaryCode> dictionaryCodes = new ArrayList<>();
        int commandLine = 0; //指令所在的行号
        while (iterator.hasNext()) {
            String comm = iterator.next();
            if (comm.startsWith("//")) { //注释判空处理
                comm = "";
            }
            if (behindLineIsEmpty) {
                /*
                 判断此行的上一行是否为空。
                 若为空则判断此行是否为空，
                 不为空证明此行是指令开始解析指令。
                 */
                if (TextUtils.isEmpty(comm)) {
                    continue;
                }
                commandRegex = comm;
                behindLineIsEmpty = false;
                commandLine = iterator.getLen();
                continue;
            }
            if (TextUtils.isEmpty(comm)) {
                if (dictionaryCodes.size() == 0) {
                    //排除只有指令没有伪代码实现的情况
                    throw new DictionaryOnLoadException("指令无伪代码实现:" + commandRegex + "(" + dicFile.getName() + ":" + (iterator.getLen() - 1) + ")");
                }
                /*
                    证明这一行指令领导的伪代码已经解析完了，
                    下面的代码用于装载解析完毕的伪代码示例
                 */
                commands.put(new DictionaryCommandMatcher(commandRegex, commandLine, dicFile), dictionaryCodes);
                dictionaryCodes = new ArrayList<>();
                behindLineIsEmpty = true;
                continue;
            }
            /*
                对每一行伪代码进行解析。
                按照[函数->特殊控制字符->纯文本]解析
            */
            if (comm.startsWith("$")) {
                try {
                    dictionaryCodes.add(Function.parseFunction(comm, iterator.getLen()));
                } catch (Throwable e) {
                    throw new DictionaryOnLoadException("解析伪代码方法时出错!" + "(" + iterator.getLen() + ":" + comm + ")", e);
                }
            } else if (comm.startsWith("如果:")) {
                dictionaryCodes.add(new Expression.If(iterator.getLen(), comm));
            } else if (comm.equals("如果尾")) {
                dictionaryCodes.add(new Expression.Else(iterator.getLen(), comm));
            } else if (comm.equals("返回")) {
                dictionaryCodes.add(new Expression.Return(iterator.getLen(), comm));
            } else {
                dictionaryCodes.add(new PlainText(iterator.getLen(), comm));
            }
        }
        /*
            最后一行若不是空的话，需要强行装载一下
         */

        if (iterator.getLen() == lines.length) {
            commands.put(new DictionaryCommandMatcher(commandRegex, commandLine, dicFile), dictionaryCodes);
        }
    }

    public File getFile() {
        return dicFile;
    }

    public HashMap<DictionaryCommandMatcher, ArrayList<DictionaryCode>> getCommands() {
        return commands;
    }

    public String getName() {
        return getFile().getName();
    }
}