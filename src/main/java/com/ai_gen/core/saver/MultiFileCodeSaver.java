package com.ai_gen.core.saver;

import cn.hutool.core.io.FileUtil;
import com.ai_gen.enums.CodeGenTypeEnum;
import com.ai_gen.model.MultiFileCodeResult;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class MultiFileCodeSaver extends CodeFileSaverTemplate<MultiFileCodeResult> {
    @Override
    protected CodeGenTypeEnum getCodeType() {

        return CodeGenTypeEnum.MULTI_FILE;
    }

    @Override
    protected void saveFile(MultiFileCodeResult codeResult, String filePath) {

        writeFile(codeResult.getHtmlCode(), "index.html", filePath);
        writeFile(codeResult.getCssCode(), "style.css", filePath);
        writeFile(codeResult.getJsCode(), "script.js", filePath);

    }

    // 文件写入方法
    private static void writeFile(String code, String fileName, String dirPath) {
        String filePath = dirPath + File.separator + fileName;
        FileUtil.writeString(code, filePath, StandardCharsets.UTF_8);
    }
}
