package com.ai_gen.core.parser;

public interface CodeParser<T> {

    /**
     * 解析 ai 生成的代码
     * @param docContent
     * @return
     */
    T parse(String codeContent);

}
