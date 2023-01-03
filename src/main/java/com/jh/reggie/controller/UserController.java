package com.jh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jh.reggie.commons.R;
import com.jh.reggie.model.entity.User;
import com.jh.reggie.service.UserService;
import com.jh.reggie.utils.ValidateCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author JH
 * @description TODO
 * @date 2022-12-19 17:32:57
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${aliyun.sms.access-key-id}")
    private String accessKeyId;
    @Value("${aliyun.sms.access-key-secret}")
    private String accessKeySecret;
    @Value("${aliyun.sms.sign-name}")
    private String signName;
    @Value("${aliyun.sms.template-code}")
    private String templateCode;

    @Value("${spring.mail.sendername}")
    private String senderName;
    @Value("${spring.mail.username}")
    private String username;

    @PostMapping("/sendMsg")
    @Async
    public R<String> send(@RequestBody User user, HttpSession session) {
        // 获取手机号
        String phone = user.getPhone();
        // 判断手机号是为为空
        if (StringUtils.isNotEmpty(phone)) {
            // 生成4位验证码
            String code = ValidateCodeUtil.generateValidateCode(6).toString();
            log.info("Code:{}", code);
            // 将生成的验证码保存到Redis中，并设置5分钟有效期
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
            // 发送短信
            // SMSUtil.sendMessage(signName, templateCode, phone, code, accessKeyId, accessKeySecret);
            // userService.sendSimpleMail(username, "15336761@qq.com", senderName, "验证码为:" + code);
            return R.success("手机验证码发送成功");
        }
        return R.error("验证码发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        // 获取手机号
        String phone = map.get("phone").toString();
        // 获取验证码
        String code = map.get("code").toString();
        // 从Redis中获取验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);
        // 将验证码进行比对（验证码一致，登录成功或自动注册账号）
        if (codeInSession != null && codeInSession.equals(code)) {
            // 判断账号是否存在
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                // 将用户手机号保存到数据库
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            session.setMaxInactiveInterval(86400);
            // 如果登录成功，删除Redis中的验证码
            redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登录失败");
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpSession session) {
        session.removeAttribute("user");
        return R.success("退出登录成功");
    }
}
