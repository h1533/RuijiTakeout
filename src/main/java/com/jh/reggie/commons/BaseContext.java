package com.jh.reggie.commons;

/**
 * @author JH
 * @description 创建ThreadLocal线程,设置用户id
 * @date 2022-12-07 18:10:43
 */
public class BaseContext {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
