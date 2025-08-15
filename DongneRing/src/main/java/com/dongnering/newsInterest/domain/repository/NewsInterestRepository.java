package com.dongnering.newsInterest.domain.repository;


import com.dongnering.newsInterest.domain.NewsInterest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsInterestRepository extends JpaRepository<NewsInterest, Long> {
}
