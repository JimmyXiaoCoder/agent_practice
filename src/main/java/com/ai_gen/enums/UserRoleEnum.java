package com.ai_gen.enums;

import cn.hutool.core.util.StrUtil;

public enum UserRoleEnum {
    ADMIN("管理员","admin"),
    USER("用户","user");

    private final String text;

    private final String value;

    UserRoleEnum(String text,String value){
        this.text = text;
        this.value = value;
    }

    public static String getValueByText(String text) {
        if (StrUtil.isBlank(text)) {
            return null;
        }

        for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {

            if(text.equals(userRoleEnum.text)){
                return userRoleEnum.value;
            }

        }

        return null;
    }

    public static UserRoleEnum getUserRoleByValue(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }

        for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {

            if(value.equals(userRoleEnum.value)){
                return userRoleEnum;
            }

        }

        return null;
    }

}
