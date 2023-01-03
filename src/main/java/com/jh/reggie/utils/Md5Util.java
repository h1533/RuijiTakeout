package com.jh.reggie.utils;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.DigestUtils;

/**
 * @author JH
 * @description 密码加密工具类
 * @date 2022-12-05 21:24:46
 */
public class Md5Util {

    private static final String SALT = "552392576";

    /**
     * 密码加密
     *
     * @param source 原密码
     * @return
     */
    public static String digest(String source) {
        String left = StringUtils.left(SALT, 4);
        String right = StringUtils.right(SALT, 5);
        String newPasswd = left + source + right;
        return DigestUtils.md5DigestAsHex(newPasswd.getBytes());
    }
}
