package com.ai_gen.core.saver;

import com.ai_gen.enums.CodeGenTypeEnum;
import com.ai_gen.enums.ErrorCode;
import com.ai_gen.exception.BusinessException;
import com.ai_gen.model.HtmlCodeResult;
import com.ai_gen.model.MultiFileCodeResult;

import java.io.File;

public class CodeFileSaverExecutor {

    private static final HTMLCodeFileSaver htmlCodeFileSaver = new HTMLCodeFileSaver();
    private static final MultiFileCodeSaver multiFileCodeSaver = new MultiFileCodeSaver();

    public static File codeFileSave(Object codeResult, CodeGenTypeEnum codeGenTypeEnum, Long appId) {

        switch (codeGenTypeEnum) {
            case HTML -> {
                return htmlCodeFileSaver.saveCodeFile((HtmlCodeResult) codeResult, appId);

            }
            case MULTI_FILE -> {
                return multiFileCodeSaver.saveCodeFile((MultiFileCodeResult) codeResult, appId);

            }
            default -> throw new BusinessException(ErrorCode.PARAMS_ERROR,"不支持的代码生成类型");
        }

    }

}
