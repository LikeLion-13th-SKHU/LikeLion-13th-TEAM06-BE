package com.dongnering.mail.application;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final MailProperties mailProperties;

    private static final String SENDER_NAME  = "동네링";

    public void sendDailyNews(String to, String title, String summary, String url) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Thymeleaf
        Context context = new Context();
        context.setVariable("title", title);
        context.setVariable("summary", summary);
        context.setVariable("url", url);
        context.setVariable("senderEmail", mailProperties.getUsername());

        String html = templateEngine.process("mail/news", context);

        helper.setTo(to);
        helper.setFrom(mailProperties.getUsername(), SENDER_NAME);
        helper.setSubject("🔥 오늘의 핫 뉴스 - 동네링");
        helper.setText(html, true);

        javaMailSender.send(message);
    }
}
