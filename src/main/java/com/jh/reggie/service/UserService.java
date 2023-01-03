package com.jh.reggie.service;

import com.jh.reggie.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author JH
* @description 针对表【user(用户信息)】的数据库操作Service
* @createDate 2022-12-19 17:01:46
*/
public interface UserService extends IService<User> {

    /**
     * 发送邮箱
     *
     * @param sender  发送者
     * @param toUser  收信者
     * @param subject 主题
     * @param text    内容
     */
    void sendSimpleMail(String sender, String toUser, String subject, String text);
}
