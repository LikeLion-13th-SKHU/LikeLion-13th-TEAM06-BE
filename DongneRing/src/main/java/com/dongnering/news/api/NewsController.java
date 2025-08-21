package com.dongnering.news.api;


import com.dongnering.common.error.SuccessCode;
import com.dongnering.common.template.ApiResTemplate;
import com.dongnering.news.api.dto.request.NewsLikeDisLikeDto;
import com.dongnering.news.api.dto.request.NewsUpdateDtoDDDDDD;
import com.dongnering.news.api.dto.response.NewsListDto;
import com.dongnering.news.api.dto.response.NewsSingleByIdDto;
import com.dongnering.news.application.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news")
@Tag(name = "뉴스(정책) API", description = "뉴스(정책) 관련 기능 제공")
public class NewsController {

    private final NewsService newsService;

    //전체 뉴스 조회(최신순)
    @GetMapping("/all")
    @Operation(summary = "전체 뉴스 조회", description = "전체 뉴스 조회(최신순)(개인화 x)")
    public ApiResTemplate<Page<NewsListDto>> newsFindAll(Principal principal, @ParameterObject Pageable pageable) {
        Page<NewsListDto> newsList = newsService.newsFindAll(principal, pageable);
        return ApiResTemplate.successResponse(SuccessCode.NEWS_GET_SUCCESS, newsList);
    }


    //뉴스 단건 조회(뉴스 id값 조회)
    @GetMapping("/{newsId}")
    @Operation(summary = "뉴스 단건 조회", description = "뉴스 단건 조회(뉴스 id값 조회)")
    public ApiResTemplate<NewsSingleByIdDto> newsFindById(Principal principal, @PathVariable Long newsId){
        NewsSingleByIdDto newsSingleByIdDto = newsService.newsFindById(principal, newsId);
        return ApiResTemplate.successResponse(SuccessCode.NEWS_GET_SUCCESS, newsSingleByIdDto);
    }


    //뉴스 좋아요 순 - 실시간 핫이슈 뉴스
    @GetMapping("/newslike")
    @Operation(summary = "뉴스 좋아요 순", description = "뉴스 좋아요 순 - 실시간 핫이슈 뉴스")
    public ApiResTemplate<Page<NewsListDto>> newsLikeList(Principal principal, @ParameterObject Pageable pageable){
        Page<NewsListDto> newsList = newsService.newsLikeList(principal, pageable);
        return ApiResTemplate.successResponse(SuccessCode.NEWS_GET_SUCCESS, newsList);
    }


    //뉴스 좋아요 통합
    @PostMapping("/like")
    @Operation(summary = "뉴스 좋아요 통합(토글식)", description = "뉴스 좋아요 통합(토글식)")
    public ApiResTemplate<Boolean> newsLikeUnLike(@RequestBody NewsLikeDisLikeDto newsLikeDisLikeDto, Principal principal){
        boolean newsLikeStatus = newsService.likeUnlikeNews(principal, newsLikeDisLikeDto);
        if (newsLikeStatus){
            return ApiResTemplate.successResponse(SuccessCode.LIKE_SUCCESS, newsLikeStatus);
        }
        return ApiResTemplate.successResponse(SuccessCode.UNLIKE_SUCCESS, newsLikeStatus);
    }


    //지역별 멤버가 등록한 지역에서 검색
    @GetMapping("/location")
    @Operation(summary = "지역별 검색", description = "멤버가 등록한 지역기사 검색(지정 지역 + 전국)")
    public ApiResTemplate<Page<NewsListDto>> newsFindByLocation(Principal principal, @ParameterObject  Pageable pageable){
        Page<NewsListDto> newsList = newsService.newsFindByLocation(principal, pageable);
        return ApiResTemplate.successResponse(SuccessCode.NEWS_GET_SUCCESS, newsList);
    }

    //관심사별 조회
    @GetMapping("/personal")
    @Operation(summary = "관심사별 조회", description = "관심사별 조회")
    public ApiResTemplate<Page<NewsListDto>> newsFinByPersonal(Principal principal, @ParameterObject Pageable pageable){
        Page<NewsListDto> newsList = newsService.newsFindByPersonal(principal, pageable);
        return ApiResTemplate.successResponse(SuccessCode.NEWS_GET_SUCCESS, newsList);
    }

    //지역별 + 관심사별 조회
    @GetMapping("/personalLocation")
    @Operation(summary = "지역별 + 관심사별 조회", description = "지역별 + 관심사별 조회")
    public ApiResTemplate<Page<NewsListDto>> newsFinByLocationPlusPersonal(Principal principal, @ParameterObject Pageable pageable){
        Page<NewsListDto> newsList = newsService.newsFindByLocationPlusPersonal(principal, pageable);
        return ApiResTemplate.successResponse(SuccessCode.NEWS_GET_SUCCESS, newsList);
    }

    //멤버가 댓글단 뉴스만 보여주기
    @GetMapping("/personalComment")
    @Operation(summary = "멤버가 댓글단 뉴스만 조회", description = "멤버가 댓글단 뉴스만 조회")
    public ApiResTemplate<Page<NewsListDto>> newsFinByPersonalComment(Principal principal, @ParameterObject Pageable pageable){
        Page<NewsListDto> newsList = newsService.newsFindByComment(principal, pageable);
        return ApiResTemplate.successResponse(SuccessCode.NEWS_GET_SUCCESS, newsList);
    }

    //멤버가 좋아요한 뉴스만 보여주기
    @GetMapping("/personalLike")
    @Operation(summary = "멤버가 좋아요한 뉴스만 보여주기", description = "멤버가 좋아요한 뉴스만 보여주기")
    public ApiResTemplate<Page<NewsListDto>> newsFinByPersonalLike(Principal principal, @ParameterObject Pageable pageable){
        Page<NewsListDto> newsList = newsService.newsFindByLiked(principal, pageable);
        return ApiResTemplate.successResponse(SuccessCode.NEWS_GET_SUCCESS, newsList);
    }


    //뉴스 삭제
    @DeleteMapping("/{newsId}")
    @Operation(summary = "뉴스 단건 삭제", description = "뉴스 Id로 뉴스 삭제")
    public ApiResTemplate<String> newsDeleteById(@PathVariable Long newsId){
        newsService.newsDeleteById(newsId);
        return ApiResTemplate.successWithNoContent(SuccessCode.NEWS_DELETE_SUCCESS);
    }


    @DeleteMapping("/deleteAll")
    @Operation(summary = "뉴스 전체 삭제", description = "뉴스 전체 삭제")
    public ApiResTemplate<String> newsDeleteAll(){
        newsService.newsDeleteAll();
        return ApiResTemplate.successWithNoContent(SuccessCode.NEWS_DELETE_SUCCESS);
    }


    //ai연결전 테스트
    @PostMapping("/newsUpdate")
    @Operation(summary = "뉴스 업데이트", description = "뉴스 업데이트")
    public ApiResTemplate<String> newsUpdate(@RequestBody NewsUpdateDtoDDDDDD newsUpdateDtoDDDDDD){
        newsService.newsUpdate(newsUpdateDtoDDDDDD);
        return ApiResTemplate.successWithNoContent(SuccessCode.GET_SUCCESS);
    }



}
