package com.ai_gen.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.ai_gen.constants.AppConstant;
import com.ai_gen.enums.CodeGenTypeEnum;
import com.ai_gen.enums.ErrorCode;
import com.ai_gen.exception.BusinessException;

import java.io.File;

public abstract class CodeFileSaverTemplate<T> {

    // 文件保存路径
    private static final String ROOT_PATH = AppConstant.CODE_OUTPUT_ROOT_DIR;

    public final File saveCodeFile(T codeResult, Long appId) {

        // 1. 参数校验
        validateParam(codeResult);

        // 2. 生成文件保存目录
        CodeGenTypeEnum codeType = getCodeType();
        String dirPath = dirPathGen(codeType.getValue(),appId);

        // 3. 保存文件（子类对象提供）
        saveFile(codeResult, dirPath);

        // 4. 返回文件
        return new File(dirPath);

    }

    // 文件存放目录创建方法（雪花ID）
    private static String dirPathGen(String bizType, Long appId) {
        if (ObjUtil.isEmpty(appId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"appId不能为空");
        }

        String dirName = StrUtil.format("{}_{}", bizType, appId);
        String dirPath = ROOT_PATH + File.separator + dirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    public void validateParam(Object codeResult) {
        if (ObjUtil.isEmpty(codeResult)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"生成代码为空");
        }
    }

    protected abstract CodeGenTypeEnum getCodeType();

    // 文件保存
    protected abstract void saveFile(T codeResult, String filePath);

}
