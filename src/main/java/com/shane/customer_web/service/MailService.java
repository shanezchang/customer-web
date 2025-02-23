package com.shane.customer_web.service;

import java.util.Map;

public interface MailService {
    /**
     * 发送简单文本邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 内容
     */
    void sendSimpleMail(String to, String subject, String content);

    /**
     * 发送HTML格式邮件
     *
     * @param to          收件人
     * @param subject     主题
     * @param htmlContent HTML内容
     */
    void sendHtmlMail(String to, String subject, String htmlContent);

    /**
     * 发送带附件的邮件
     *
     * @param to          收件人
     * @param subject     主题
     * @param content     内容
     * @param attachments 附件列表（文件名 → 文件路径）
     */
    void sendAttachmentMail(String to, String subject, String content,
                            Map<String, String> attachments);
}
