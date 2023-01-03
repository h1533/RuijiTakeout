package com.jh.reggie.commons;

/**
 * @author JH
 * @description 错误枚举类
 * @date 2022-12-07 21:18:25
 */
public enum ErrorCode {

    ASSOCIATED_DISH("当前分类已关联菜品，请删除菜品后删除分类"),
    ASSOCIATED_SETMEAL("当前分类已关联套餐，请删除套餐后删除分类"),
    LOGIN_ERROR("登录失败"),
    PASSWORD_ERROR("密码错误"),
    ACCOUNT_ERROR("账号已禁用"),
    FILE_NOT_FOUND_ERROR("上传文件不能为空"),
    DIR_ERROR("文件夹创建失败"),
    SETMEAL_STATUS_ERROR("当前套餐售卖中，无法删除"),
    DISH_STATUS_ERROR("当前菜品售卖中，无法删除"),
    SETMEAL_NOT_FOUND("套餐不存在"),
    SHOPPING_CART_NOT_FOUND("购物车为空,不能下单"),
    ADDRESS_BOOK_ERROR("地址簿为空，请添加地址后下单");

    private String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
