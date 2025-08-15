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

    // 가입한 모든 사용자 기준 메일 전송
    public void sendDailyNews(String to, String newsTitle, String newsContent, String artTitle, String artImageUrl, String newsUrl, String artUrl)
            throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Thymeleaf
        Context context = new Context();
        context.setVariable("newsTitle", newsTitle);
        context.setVariable("newsContent", newsContent);
        context.setVariable("artTitle", artTitle);
        context.setVariable("artImageUrl", artImageUrl);
        context.setVariable("newsUrl", newsUrl);
        context.setVariable("artUrl", artUrl);
        context.setVariable("senderEmail", mailProperties.getUsername());

        String html = templateEngine.process("mail/news", context);

        helper.setTo(to);
        helper.setFrom(mailProperties.getUsername(), SENDER_NAME);
        helper.setSubject("🔥 오늘의 핫 뉴스 - 동네링");
        helper.setText(html, true);

        javaMailSender.send(message);
    }

    // Test: 로그인한 사용자 이메일 기준 메일 전송
    public void sendDailyNewsWithMember(String email, String newsTitle, String newsContent, String artTitle, String artImageUrl, String newsUrl, String artUrl)
            throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Thymeleaf
        Context context = new Context();
        context.setVariable("newsTitle", newsTitle);
        context.setVariable("newsContent", newsContent);
        context.setVariable("artTitle", artTitle);
        context.setVariable("artImageUrl", artImageUrl);
        context.setVariable("newsUrl", newsUrl);
        context.setVariable("artUrl", artUrl);
        context.setVariable("senderEmail", mailProperties.getUsername());

        String html = templateEngine.process("mail/news", context);

        helper.setTo(email);
        helper.setFrom(mailProperties.getUsername(), SENDER_NAME);
        helper.setSubject("🔥 오늘의 추천 뉴스&예술 - 동네링");
        helper.setText(html, true);

        javaMailSender.send(message);
    }
}
