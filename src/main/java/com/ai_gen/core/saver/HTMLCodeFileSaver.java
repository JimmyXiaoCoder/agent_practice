package com.ai_gen.core.saver;

import cn.hutool.core.io.FileUtil;
import com.ai_gen.enums.CodeGenTypeEnum;
import com.ai_gen.model.HtmlCodeResult;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class HTMLCodeFileSaver extends CodeFileSaverTemplate<HtmlCodeResult> {
    @Override
    protected CodeGenTypeEnum getCodeType() {

        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveFile(HtmlCodeResult codeResult, String filePath) {
        writeFile(codeResult.getHtmlCode(),"index.html",filePath);
    }

    // 文件写入方法
    private static void writeFile(String code, String fileName, String dirPath) {
        String filePath = dirPath + File.separator + fileName;
        FileUtil.writeString(code, filePath, StandardCharsets.UTF_8);
    }


}
