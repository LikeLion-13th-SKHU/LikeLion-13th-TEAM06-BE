package com.dongnering.comment.newsComment.application;


import com.dongnering.comment.newsComment.api.dto.requsest.NewsCommentRequestDto;
import com.dongnering.comment.newsComment.api.dto.response.NewsCommentResponseDto;
import com.dongnering.comment.newsComment.domain.NewsComment;
import com.dongnering.comment.newsComment.domain.repository.NewsCommentRepository;
import com.dongnering.mypage.domain.Member;
import com.dongnering.mypage.domain.repository.MemberRepository;
import com.dongnering.news.domain.News;
import com.dongnering.news.domain.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsCommentService {

    private final MemberRepository memberRepository;
    private final NewsRepository newsRepository;
    private final NewsCommentRepository newsCommentRepository;


    //뉴스 댓글 생성
    @Transactional
    public void createNewsComment(NewsCommentRequestDto newsCommentRequestDto, Principal principal){

        Long memberId = Long.parseLong(principal.getName());
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new IllegalStateException("해당 멤버가 없습니다. id=" + memberId));;


        News news = newsRepository.findById(newsCommentRequestDto.newsId()).orElseThrow(IllegalArgumentException::new);


        if (!newsCommentRepository.existsByMemberAndNews(member, news)){
            NewsComment newsComment = NewsComment.builder()
                    .member(member)
                    .news(news)
                    .content(newsCommentRequestDto.content())
                    .build();


            newsCommentRepository.save(newsComment);
        }
        else {
            throw new IllegalStateException("이미 댓글이 존재합니다.");
        }

    }

    //뉴스 댓글 삭제
    @Transactional
    public void deleteNewsComment(Long newsId, Principal principal){
        Long memberId = Long.parseLong(principal.getName());
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new IllegalStateException("해당 멤버가 없습니다. id=" + memberId));;

        News news = newsRepository.findById(newsId).orElseThrow(IllegalArgumentException::new);

        if(newsCommentRepository.existsByMemberAndNews(member, news)){
            NewsComment newsComment = newsCommentRepository.findByMemberAndNews(member, news);
            newsCommentRepository.delete(newsComment);
        }
        else {
            throw new IllegalStateException("작성한 댓글이 없습니다.");
        }


    }

    //뉴스 댓글 반환
    public List<NewsCommentResponseDto> findNewsComment(Long newsId){

        News news = newsRepository.findById(newsId).orElseThrow(IllegalArgumentException::new);
        List<NewsComment> newsCommentList = newsCommentRepository.findAllByNews(news);

        return newsCommentList.stream().map(NewsCommentResponseDto::from).toList();

    }

    //뉴스 댓글 개수 빈환
    public Long findCommentNumber(News news){
        return newsCommentRepository.countByNews(news);
    }





}
