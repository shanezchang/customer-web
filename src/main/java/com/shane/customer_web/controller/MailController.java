package com.shane.customer_web.controller;

import com.shane.customer_web.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;

    @GetMapping("/send-simple")
    public String sendSimple() {
        mailService.sendSimpleMail(
                "shane.z.chang@hotmail.com",
                "测试简单邮件",
                "这是一封测试邮件内容"
        );
        return "邮件已发送";
    }
}
