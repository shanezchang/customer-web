package com.shane.customer_web.service.impl;

import com.shane.customer_web.service.IMailService;
import com.shane.customer_web.util.BusinessException;
import com.shane.customer_web.util.RspCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService implements IMailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
        log.info("简单邮件已发送至 {}", to);
    }

    @Override
    public void sendHtmlMail(String to, String subject, String htmlContent) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("HTML邮件已发送至 {}", to);
        } catch (MessagingException e) {
            log.error("发送HTML邮件失败", e);
            throw new MailSendException("邮件发送失败");
        }
    }

    @Override
    public void sendAttachmentMail(String to, String subject, String content,
                                   Map<String, String> attachments) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content);

            // 添加附件
            attachments.forEach((fileName, filePath) -> {
                try {
                    FileSystemResource file = new FileSystemResource(new File(filePath));
                    helper.addAttachment(fileName, file);
                } catch (MessagingException e) {
                    throw new MailPreparationException("附件添加失败: " + fileName);
                }
            });

            mailSender.send(message);
            log.info("带附件邮件已发送至 {}", to);
        } catch (MessagingException e) {
            log.error("发送带附件邮件失败", e);
            throw new MailSendException("邮件发送失败");
        }
    }

    @Override
    public void sendEmail(String toEmail, String subject, String content) {
        if (!this.isValidEmail(toEmail)) {
            throw new BusinessException(RspCode.EMAIL_ADDRESS_ERROR);
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    private boolean isValidEmail(String email) {
        try {
            InternetAddress address = new InternetAddress(email);
            address.validate();
            return true;
        } catch (AddressException ex) {
            return false;
        }
    }
}
