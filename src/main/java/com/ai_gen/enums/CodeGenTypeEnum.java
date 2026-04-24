package com.ai_gen.enums;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;

@Getter
public enum CodeGenTypeEnum {
    HTML("原生 HTML 文件模式","html"),
    MULTI_FILE("原生多文件模式","multi_file");

    private final String text;

    private final String value;

    CodeGenTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static CodeGenTypeEnum getTypeByValue(String value) {

        if (ObjUtil.isEmpty(value)) {
            return null;
        }

        for (CodeGenTypeEnum codeGenTypeEnum : CodeGenTypeEnum.values()) {

            if (StrUtil.equals(value,codeGenTypeEnum.value)) {
                return codeGenTypeEnum;
            }

        }
        return null;
    }

}
