package com.activity.smallgame.controller;

import com.activity.smallgame.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Mr.dong
 * @create: 2019-09-19 11:22
 **/
@RestController
public class MailController {

    @Autowired
    private SendMailService sendMailService;

    @GetMapping("/api/v1/send")
    public void sendTestMail() {
        sendMailService.sendSimpleMail("394167306@qq.com", "test mail", "send test mail", "15913375679", false);
    }
}
