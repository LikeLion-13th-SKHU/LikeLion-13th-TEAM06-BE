package com.dongnering.mail.scheduler;

import com.dongnering.art.domain.Art;
import com.dongnering.art.domain.repository.ArtRepository;
import com.dongnering.common.error.ErrorCode;
import com.dongnering.common.exception.BusinessException;
import com.dongnering.mail.application.MailService;
import com.dongnering.member.domain.repository.MemberRepository;
import com.dongnering.news.domain.News;
import com.dongnering.news.domain.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class NewsScheduler {

    private final MailService mailService;
    private final MemberRepository memberRepository;
    private final NewsRepository newsRepository;
    private final ArtRepository artRepository;

    private final Random random = new Random();

    // 매일 ?시에 메일 전송 실행 - 가입한 모든 사용자
    // @Scheduled(cron = "0 35 2 * * *")
    public void sendDailyNews() {
        memberRepository.findAll().forEach(member -> {
            try {
                // 상위 20개 조회
                Page<News> topNews = newsRepository.findAllByOrderByLikeCountDesc(PageRequest.of(0, 20));
                Page<Art> topArts = artRepository.findAllByOrderByLikeCountDesc(PageRequest.of(0, 20));

                // 랜덤 1개 선택
                News selectedNews = topNews.getContent().get(random.nextInt(topNews.getContent().size()));
                Art selectedArt = topArts.getContent().get(random.nextInt(topArts.getContent().size()));

                // 보러가기 url 설정
                String frontBaseUrl = "https://dongnering.vercel.app";
                String newsUrl = frontBaseUrl + "/news/" + selectedNews.getNewsId();
                String artUrl  = frontBaseUrl + "/art/" + selectedArt.getArtId();

                // html 형식 변환
                String newsContent = selectedNews.getContent()
                        .replaceAll("<img[^>]*>", "")
                        .replaceAll("<[^>]+>", "")
                        .trim();
                newsContent = HtmlUtils.htmlUnescape(newsContent);
                newsContent = truncate(newsContent, 300);

                String artTitle = HtmlUtils.htmlUnescape(selectedArt.getTitle());

                // 이메일 전송
                mailService.sendDailyNews(
                        member.getEmail(),
                        selectedNews.getTitle(),
                        newsContent,
                        artTitle,
                        selectedArt.getImageUrl(),
                        newsUrl,
                        artUrl
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // test: 로그인 한 사용자 이메일로만 전송
    public void sendDailyNewsWithMember(Principal principal) {
        Long memberId = Long.parseLong(principal.getName()); // 주체 id -> email 가져오기 필요
        String email = memberRepository.findById(memberId)
                .map(member -> member.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND
                        , ErrorCode.MEMBER_NOT_FOUND.getMessage()));

        // 상위 20개 조회
        Page<News> topNews = newsRepository.findAllByOrderByLikeCountDesc(PageRequest.of(0, 20));
        Page<Art> topArts = artRepository.findAllByOrderByLikeCountDesc(PageRequest.of(0, 20));

        // 랜덤 1개 선택
        News selectedNews = topNews.getContent().get(random.nextInt(topNews.getContent().size()));
        Art selectedArt = topArts.getContent().get(random.nextInt(topArts.getContent().size()));

        // 보러가기 url 설정
        String frontBaseUrl = "https://dongnering.vercel.app";
        String newsUrl = frontBaseUrl + "/news/" + selectedNews.getNewsId();
        String artUrl  = frontBaseUrl + "/events/" + selectedArt.getArtId();

        // html 형식 변환
        String newsContent = selectedNews.getContent()
                .replaceAll("<img[^>]*>", "")
                .replaceAll("<[^>]+>", "")
                .trim();
        newsContent = HtmlUtils.htmlUnescape(newsContent);
        newsContent = truncate(newsContent, 300);

        String artTitle = HtmlUtils.htmlUnescape(selectedArt.getTitle());

        // 이메일 전송
        try {
            mailService.sendDailyNewsWithMember(
                    email,
                    selectedNews.getTitle(),
                    newsContent,
                    artTitle,
                    selectedArt.getImageUrl(),
                    newsUrl,
                    artUrl
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // content 형식 변경 - 300자 max, 길면 ... 처리
    String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
    }
}
