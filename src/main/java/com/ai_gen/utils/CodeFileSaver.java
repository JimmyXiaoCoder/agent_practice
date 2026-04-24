package com.ai_gen.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ai_gen.enums.CodeGenTypeEnum;
import com.ai_gen.model.HtmlCodeResult;
import com.ai_gen.model.MultiFileCodeResult;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class CodeFileSaver {

    // 文件保存路径
    private static final String ROOT_PATH = System.getProperty("user.dir") + "/tmp/code_gen_result";

    // HTML 文件生成方法
    public static File HTMLCodeGen(HtmlCodeResult htmlCodeResult) {
        String dirPath = dirPathGen(CodeGenTypeEnum.HTML.getValue());
        writeFile(htmlCodeResult.getHtmlCode(), "index.html", dirPath);
        return new File(dirPath);
    }

    // HTML 文件生成方法
    public static File MultiFileCodeGen(MultiFileCodeResult multiFileCodeResult) {
        String dirPath = dirPathGen(CodeGenTypeEnum.MULTI_FILE.getValue());
        writeFile(multiFileCodeResult.getHtmlCode(), "index.html", dirPath);
        writeFile(multiFileCodeResult.getCssCode(), "style.css", dirPath);
        writeFile(multiFileCodeResult.getJsCode(), "script.js", dirPath);
        return new File(dirPath);
    }

    // 文件存放目录创建方法（雪花ID）
    private static String dirPathGen(String bizType) {
        String dirName = StrUtil.format("{}_{}", bizType, IdUtil.getSnowflakeNextIdStr());
        String dirPath = ROOT_PATH + File.separator + dirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }


    // 文件写入方法
    private static void writeFile(String code, String fileName, String dirPath) {
        String filePath = dirPath + File.separator + fileName;
        FileUtil.writeString(code, filePath, StandardCharsets.UTF_8);
    }


}
