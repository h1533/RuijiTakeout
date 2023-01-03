package com.jh.reggie.utils;

import com.alibaba.fastjson2.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/**
 * 短信发送工具类
 *
 * @author JH
 */
@Slf4j
public class SMSUtil {

    /**
     * 发送短信
     *
     * @param signName        签名
     * @param templateCode    模板
     * @param phoneNumbers    手机号
     * @param param           参数
     * @param accessKeyId     访问id
     * @param accessKeySecret 密钥
     */
    public static void sendMessage(String signName, String templateCode, String phoneNumbers, String param,
                                   String accessKeyId, String accessKeySecret) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        SendSmsRequest request = new SendSmsRequest();
        request.setSysRegionId("cn-hangzhou");
        request.setPhoneNumbers(phoneNumbers);
        request.setSignName(signName);
        request.setTemplateCode(templateCode);
        request.setTemplateParam("{\"code\":\"" + param + "\"}");
        try {
            SendSmsResponse response = client.getAcsResponse(request);
            System.out.println("短信发送成功:" + JSON.toJSON(response));
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

}
