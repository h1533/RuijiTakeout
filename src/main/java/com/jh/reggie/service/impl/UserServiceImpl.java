package com.jh.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jh.reggie.model.entity.User;
import com.jh.reggie.service.UserService;
import com.jh.reggie.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
* @author JH
* @description 针对表【user(用户信息)】的数据库操作Service实现
* @createDate 2022-12-19 17:01:46
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Autowired
    private static JavaMailSender jms;

    /**
     * 发送邮箱
     *
     * @param sender  发送者
     * @param toUser  收信者
     * @param subject 主题
     * @param text    内容
     */
    @Override
    public void sendSimpleMail(String sender, String toUser, String subject, String text) {
        try {
            SimpleMailMessage smm = new SimpleMailMessage();
            smm.setFrom(sender);       // 发送者
            smm.setTo(toUser);         // 接收者
            smm.setSubject(subject);   // 邮件主题
            smm.setText(text);         // 邮件正文
            jms.send(smm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}




