package com.carl.judge.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * App 应用枚举
 */
public enum AppTypeEnum {

    SCORE("得分类", 0),
    TEST("测评类", 1);

    private final String text;
    private final int value;

    AppTypeEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }


    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static AppTypeEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)){
            return null;
        }
        for (AppTypeEnum appTypeEnum : AppTypeEnum.values()) {
            if (appTypeEnum.getValue() == value) {
                return appTypeEnum;
            }
        }
        return null;
    }

    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }


}
