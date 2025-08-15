package com.dongnering.mail.scheduler;

import com.dongnering.mail.application.MailService;
import com.dongnering.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsScheduler {

    private final MailService mailService;
    private final MemberRepository memberRepository;

    // TODO: 뉴스 연결

    // 매일 ?시에 메일 전송 실행
    // @Scheduled(cron = "0 40 1 * * *")
    public void sendDailyNews() {
        memberRepository.findAll().forEach(member -> {
            try {
                mailService.sendDailyNews(member.getEmail(), "양천구"
                        , "Test"
                        , "https");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
