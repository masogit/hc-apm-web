package com.ge.apm.service.utils;

public class BooleanUtils {

    public static boolean getFalseIfNull(Boolean input) {
        if (null == input) {
            return false;
        }
        return input.booleanValue();
    }
}
