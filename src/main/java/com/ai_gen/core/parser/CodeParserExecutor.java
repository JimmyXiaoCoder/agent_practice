package com.ai_gen.core.parser;

import com.ai_gen.enums.CodeGenTypeEnum;
import com.ai_gen.enums.ErrorCode;
import com.ai_gen.exception.BusinessException;

public class CodeParserExecutor {

    private final static HTMLCodeParser htmlCodeParser = new HTMLCodeParser();
    private final static MultiFileCodeParser multiFileCodeParser = new MultiFileCodeParser();

    public static Object codeParse(String codeContent, CodeGenTypeEnum codeGenTypeEnum) {

        return switch (codeGenTypeEnum) {
            case HTML -> htmlCodeParser.parse(codeContent);
            case CodeGenTypeEnum.MULTI_FILE -> multiFileCodeParser.parse(codeContent);
            default -> throw new BusinessException(ErrorCode.PARAMS_ERROR,"不支持的代码生成类型");
        };

    }


}
