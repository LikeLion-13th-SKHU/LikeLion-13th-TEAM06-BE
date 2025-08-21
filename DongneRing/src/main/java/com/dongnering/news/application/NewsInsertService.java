package com.dongnering.news.application;


import com.dongnering.interest.domain.Interest;
import com.dongnering.interest.domain.InterestType;
import com.dongnering.interest.domain.repository.InterestRepository;
import com.dongnering.news.api.dto.request.NewsSaveDto;
import com.dongnering.news.domain.News;
import com.dongnering.news.domain.repository.NewsRepository;
import com.dongnering.newsInterest.domain.NewsInterest;
import com.dongnering.newsInterest.domain.repository.NewsInterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsInsertService {

    private final NewsRepository newsRepository;
    private final InterestRepository interestRepository;
    private final NewsInterestRepository newsInterestRepository;

    @Transactional
    public void newsSave(NewsSaveDto newsSaveDto){

        News news = News.builder()
                .title(newsSaveDto.title())
                .content(newsSaveDto.contents())
                .imgUrl(newsSaveDto.imageUrl())
                .build();

        newsRepository.save(news);

        List<InterestType> interestTypes = newsSaveDto.interestTypes();

        for (InterestType interestType : interestTypes) {

            if (interestRepository.existsByInterestType(interestType)){

                Interest interest = interestRepository.findByInterestType(interestType).orElseThrow(() -> new IllegalStateException("해당하는 관심사가 없습니다."));

                NewsInterest newsInterest = new NewsInterest(news, interest);
                newsInterestRepository.save(newsInterest);
                news.getNewsInterest().add(newsInterest);

            }




        }



    }




}
