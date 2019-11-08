package com.activity.smallgame.service;

import com.activity.smallgame.constant.MiningConstant;
import com.activity.smallgame.enums.ResultEnum;
import com.activity.smallgame.exception.SmallGameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author: Mr.dong
 * @create: 2019-08-21 17:10
 **/
@Service
public class SendMailService {

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StringRedisTemplate redis;

    public void sendSimpleMail(String to, String subject, String content, String mobile, boolean sendToUser) {
        String cacheKey = null;
        if (!sendToUser) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String date = format.format(new Date());
            cacheKey = String.format(MiningConstant.CACHE_SEND_MAIL, mobile, date);
            String cacheValue = redis.opsForValue().get(cacheKey);
            if (!StringUtils.isEmpty(cacheValue)) {
                throw new SmallGameException(ResultEnum.SEND_MAIL_ALREADY_TODAY);
            }
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);//收信人
        message.setSubject(subject);//主题
        message.setText(content);//内容
        message.setFrom(from);//发信人
        mailSender.send(message);

        if (!sendToUser) {
            redis.opsForValue().set(cacheKey, mobile, 1, TimeUnit.DAYS);
        }
    }
}
