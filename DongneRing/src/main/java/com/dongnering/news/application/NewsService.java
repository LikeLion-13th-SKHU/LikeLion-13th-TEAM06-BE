package com.dongnering.news.application;


import com.dongnering.comment.newsComment.api.dto.response.NewsCommentResponseDto;
import com.dongnering.comment.newsComment.application.NewsCommentService;
import com.dongnering.comment.newsComment.domain.NewsComment;
import com.dongnering.interest.domain.Interest;
import com.dongnering.interest.domain.InterestType;
import com.dongnering.interest.domain.repository.InterestRepository;
import com.dongnering.memberInterest.domain.MemberInterest;
import com.dongnering.memberNewsLike.MemberNewsLike;
import com.dongnering.memberNewsLike.MemberNewsLikeRepository;
import com.dongnering.member.domain.Member;
import com.dongnering.member.domain.repository.MemberRepository;
import com.dongnering.news.api.dto.request.NewsUpdateDtoDDDDDD;
import com.dongnering.news.api.dto.response.NewsListDto;
import com.dongnering.news.api.dto.response.NewsSingleByIdDto;
import com.dongnering.news.domain.News;
import com.dongnering.news.domain.repository.NewsRepository;
import com.dongnering.newsInterest.domain.NewsInterest;
import com.dongnering.newsInterest.domain.repository.NewsInterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsService {

    private final NewsRepository newsRepository;
    private final MemberRepository memberRepository;
    private final MemberNewsLikeRepository memberNewsLikeRepository;
    private final NewsCommentService newsCommentService;

    //------ai 연결 후 뉴스 테이블 수정할떄사용 ->언젠가 지워
    private final NewsInterestRepository newsInterestRepository;
    private final InterestRepository interestRepository;



    //뉴스 단건 조회
    public NewsSingleByIdDto newsFindById(Principal principal, Long newsId) {

        Member member = findMemberByMemberId(principal);
        News news = newsRepository.findById(newsId).orElseThrow(() -> new IllegalStateException("해당 뉴스를 찾을 수 없습니다 : " + newsId));

        //기사 좋아요 당겨오기 - 멤버뵬
        List<Long> likedNewsIds = memberNewsLikeRepository.findNewsByMember(member);

        //기사별 댓글 가져오기 - 기사 id별
        List<NewsCommentResponseDto> newsComment = newsCommentService.findNewsComment(news.getNewsId());

        boolean liked = likedNewsIds.contains(news.getNewsId());
        return NewsSingleByIdDto.from(news, liked, newsComment);
    }


    //전체조회(최신 등록순) - newsId  DESC
    public Page<NewsListDto> newsFindAll(Principal principal, @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Pageable sortedPageable = pageConverter(pageable);
        Member member = findMemberByMemberId(principal);
        Page<News> newsPage = newsRepository.findAll(sortedPageable);

        //기사 좋아요 당겨오기 - 멤버뵬
        List<Long> likedNewsIds = memberNewsLikeRepository.findNewsByMember(member);

        return pageLastLikeCommentConverter(newsPage, likedNewsIds);
    };


    //지역별 뉴스 조회
    public Page<NewsListDto> newsFindByLocation(Principal principal, @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Pageable sortedPageable = pageConverter(pageable);
        Member member = findMemberByMemberId(principal);
        String location = member.getLocation();

        Page<News> newsPage = newsRepository.findByLocation(location, sortedPageable);
        List<Long> likedNewsIds = memberNewsLikeRepository.findNewsByMember(member);

        return pageLastLikeCommentConverter(newsPage, likedNewsIds);
    }


    //개인화 뉴스(태그 기반)
    public Page<NewsListDto> newsFindByPersonal(Principal principal, @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Pageable sortedPageable = pageConverter(pageable);
        Member member = findMemberByMemberId(principal);

        //관심사 추출  ->
        List<Interest> interests = member.getMemberInterests().stream()
                .map(MemberInterest::getInterest)
                .toList();


        Page<News> newsList = newsRepository.findByInterests(interests, sortedPageable);
        List<Long> likedNewsIds = memberNewsLikeRepository.findNewsByMember(member);

        return pageLastLikeCommentConverter(newsList, likedNewsIds);
    }


    //지역별 + 개인화 태그 뉴스 -> 이걸 최종적으로 사용
    public Page<NewsListDto> newsFindByLocationPlusPersonal(Principal principal, @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Pageable sortedPageable = pageConverter(pageable);
        Member member = findMemberByMemberId(principal);
        String location = member.getLocation();

        List<Interest> interests = member.getMemberInterests().stream()
                .map(MemberInterest::getInterest)
                .toList();


        List<Long> likedNewsIds = memberNewsLikeRepository.findNewsByMember(member);
        Page<News> newsPage = newsRepository.findByLocationAndInterests(location, interests, sortedPageable);

        return pageLastLikeCommentConverter(newsPage, likedNewsIds);
    }

     //뉴스 좋아요순 - 핫이슢 뉴스
    public Page<NewsListDto> newsLikeList(Principal principal, Pageable pageable){
        Pageable sortedPageable = pageConverter(pageable);
        Long memberId = Long.valueOf(principal.getName());
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new IllegalStateException("해당 멤버가 없습니다. id=" + memberId));;;
        List<Long> likedNewsIds = memberNewsLikeRepository.findNewsByMember(member);
        Page<News> newsList = newsRepository.findAllByOrderByLikeCountDesc(sortedPageable);
        return pageLastLikeCommentConverter(newsList, likedNewsIds);
    }


    //댓글단 기사들만 보여주기
    public Page<NewsListDto> newsFindByComment(Principal principal, Pageable pageable){
        Pageable sortedPageable = pageConverter(pageable);
        Member member = findMemberByMemberId(principal);
        List<NewsComment> newsCommentList = member.getNewsCommentList();
        List<Long> likedNewsIds = memberNewsLikeRepository.findNewsByMember(member);
        List<Long> list = newsCommentList.stream().map(newsComment -> newsComment.getNews().getNewsId()).toList();


        Page<News> newsList = newsRepository.findNewsByCommentId(list, sortedPageable);
        return pageLastLikeCommentConverter(newsList, likedNewsIds);


    }


    //좋아요한 기사들만 보여주기
    public Page<NewsListDto> newsFindByLiked(Principal principal, Pageable pageable){
        Pageable sortedPageable = pageConverter(pageable);
        Member member = findMemberByMemberId(principal);
        List<Long> likedNewsIds = memberNewsLikeRepository.findNewsByMember(member);

        Page<News> newsPage = newsRepository.findNewsByCommentId(likedNewsIds, sortedPageable);


        return pageLastLikeCommentConverter(newsPage, likedNewsIds);

    }


    //뉴스 좋아요
    @Transactional
    public void newsLike(Principal principal, Long newsId) {
        Long memberId = Long.parseLong(principal.getName());
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new IllegalStateException("해당 멤버가 없습니다. id=" + memberId));;
        News news = newsRepository.findById(newsId).orElseThrow(()-> new IllegalStateException("해당 뉴스가 없습니다. id=" + newsId));

        //한 뉴스에 한명의 사람만 좋아요 가능하게 확인하는 부분
        boolean exists = memberNewsLikeRepository.existsByMemberAndNews(member, news);
        if (exists) {
            throw new IllegalStateException("이미 좋아요를 누른 뉴스입니다.");
        }

        MemberNewsLike memberNewsLike = new MemberNewsLike(member, news);
        memberNewsLikeRepository.save(memberNewsLike);
        member.getNewsLikeList().add(memberNewsLike);
        news.getMemberNewsLike().add(memberNewsLike);

        news.setLikeCount(news.getLikeCount() + 1);
    }


    //뉴스 좋아요 취소
    @Transactional
    public void unlikeNews(Principal principal, Long newsId) {
        Long memberId = Long.parseLong(principal.getName());
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new IllegalStateException("해당 멤버가 없습니다. id=" + memberId));;
        News news = newsRepository.findById(newsId).orElseThrow(()-> new IllegalStateException("해당 뉴스가 없습니다. id=" + newsId));

        boolean exists = memberNewsLikeRepository.existsByMemberAndNews(member, news);
        if (!exists) {
            throw new IllegalStateException("좋아요 누른적 없음.");
        }

        news.setLikeCount(news.getLikeCount() - 1);
        memberNewsLikeRepository.deleteByMemberAndNews(member, news);
    }

    //뉴스 단건 삭제
    @Transactional
    public void newsDeleteById(Long newsId){
        News news = newsRepository.findById(newsId).orElseThrow(()-> new IllegalStateException("해당 뉴스가 없습니다. id=" + newsId));
        newsRepository.delete(news);
    }

    //전체 뉴스 삭제
    @Transactional
    public void newsDeleteAll(){
        newsRepository.deleteAll();
    }


    //공공api -> 서버 (identifyId, title, contents, imgUrl) 저장 후 -> ai로 전달
    @Transactional
    public void newFirstSave(Long newsIdentifyId, String title, String imageUrl, String content, String newsDate){

        News news = News.builder()
                .newsIdentifyId(newsIdentifyId)
                .title(title)
                .imgUrl(imageUrl)
                .content(content)
                .newsdate(newsDate)
                .build();

        newsRepository.save(news);
    }





    //-----------공통 메소드들-------------

    //멤버찾기
    public Member findMemberByMemberId(Principal principal){
        Long memberId = Long.parseLong(principal.getName());
        return memberRepository.findById(memberId).orElseThrow(()-> new IllegalStateException("해당 멤버가 없습니다. id=" + memberId));
    }

    //최종 클라이언트에 전달되는 객체 - 뉴스마다 좋아요 + 댓글 개수 추가해서 전송
    private Page<NewsListDto> pageLastLikeCommentConverter (Page<News> newsPage, List<Long> likedNewsIds ){
        return newsPage.map(news -> {
            boolean liked = likedNewsIds.contains(news.getNewsId());
            Long num = newsCommentService.findCommentNumber(news);
            return NewsListDto.from(news, liked, num);
        });
    }

    //클라이언트가 준 pageable -> 뉴스 최신순으로 보여주기 위해서 작성함 (페이지네이션에 sort가 포함되어 있어야해서 작성)
    private Pageable pageConverter(Pageable pageable){
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "newsId"));
        return sortedPageable;
    }




    //------ai 연결 후 뉴스 테이블 수정할떄사용
    @Transactional
    public void newsUpdate(NewsUpdateDtoDDDDDD newsUpdateDtoDDDDDD){

        News news = newsRepository.findById(newsUpdateDtoDDDDDD.newsId()).orElseThrow(()->new IllegalStateException("해당 뉴스없음"));


        for (String s : newsUpdateDtoDDDDDD.category()) {

            InterestType interestType = InterestType.valueOf(s);
            Interest interest = interestRepository.findByInterestType(interestType);

            NewsInterest newsInterest = NewsInterest.builder()
                    .news(news)
                    .interest(interest)
                    .build();

            newsInterestRepository.save(newsInterest);
            news.getNewsInterest().add(newsInterest);
        }


       news.getNewsSummary().addAll(newsUpdateDtoDDDDDD.summary());

       news.getNewsTags().addAll(newsUpdateDtoDDDDDD.tag());

       news.setLocation(newsUpdateDtoDDDDDD.location());


    }





//    //page<NewsByIdDto> 최종 출력할때 사용 - 뉴스마다 좋아요 + 댓글 정보 추가 위해 필요
//    private Page<NewsByIdDto> pageLastLikeCommentConverter (Page<News> newsPage, List<Long> likedNewsIds ){
//        return newsPage.map(news -> {
//            boolean liked = likedNewsIds.contains(news.getNewsId());
//            List<NewsCommentResponseDto> newsComment = newsCommentService.findNewsComment(news.getNewsId());
//            return NewsByIdDto.from(news, liked, newsComment);
//        });
//    }


}


















