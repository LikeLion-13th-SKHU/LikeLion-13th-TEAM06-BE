package com.dongnering.art.application;


import com.dongnering.art.api.dto.response.ArtListDto;
import com.dongnering.art.api.dto.response.ArtSingleByIdDto;
import com.dongnering.art.domain.Art;
import com.dongnering.art.domain.repository.ArtRepository;
import com.dongnering.memberArtLike.MemberArtLike;
import com.dongnering.memberArtLike.MemberArtLikeRepository;

import com.dongnering.member.domain.Member;
import com.dongnering.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.security.PublicKey;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtService {

    private final MemberRepository memberRepository;
    private final ArtRepository artRepository;
    private final MemberArtLikeRepository memberArtLikeRepository;


    //아트 Id별 단건 조회
    public ArtSingleByIdDto artFindById(Principal principal, Long artId) {
        Member member = findMemberByMemberId(principal);
        Art art = artRepository.findById(artId).orElseThrow(()-> new IllegalStateException("해당 아트가 없습니다. id=" + artId));
        List<Long> memberLikedArtIds = memberArtLikeRepository.findArtByMember(member);


        boolean liked = memberLikedArtIds.contains(art.getArtId());
        return ArtSingleByIdDto.from(art, liked);
    }

    //아트 지역별 조회
    public Page<ArtListDto> artFindByLocation(Principal principal, @PageableDefault(page = 0, size = 10) Pageable pageable){
        Pageable sortPageable = pageConverter(pageable);
        Member member = findMemberByMemberId(principal);
        String location = member.getLocation();
        Page<Art> artPage = artRepository.findAllByLocation(location, sortPageable);

        List<Long> likedArtIds = memberArtLikeRepository.findArtByMember(member);

        return pageLastLikeCommentConverter(artPage, likedArtIds);

    }

    //아트 전체 (전국) 조회
    public Page<ArtListDto> artFindAll(Principal principal, Pageable pageable){
        Pageable sortPageable = pageConverter(pageable);
        Member member = findMemberByMemberId(principal);

        System.out.println("AAAAAA :" + member.getMemberId());

        List<Long> likedArtIds = memberArtLikeRepository.findArtByMember(member);

        System.out.println("AAAAAA :" + likedArtIds.stream().toList());

        Page<Art> artPage = artRepository.findAll(sortPageable);
        return pageLastLikeCommentConverter(artPage, likedArtIds);
    }

    //아트 좋아요
    @Transactional
    public void likeArt (Principal principal, Long artId){

        Member member = findMemberByMemberId(principal);
        Art art = artRepository.findById(artId).orElseThrow(()-> new IllegalStateException("해당 아트가 없습니다. id=" + artId));

        boolean exists = memberArtLikeRepository.existsByMemberAndArt(member, art);
        if (exists) {
            throw new IllegalStateException("이미 좋아요를 누른 뉴스입니다.");
        }

        MemberArtLike memberArtLike = new MemberArtLike(member, art);
        memberArtLikeRepository.save(memberArtLike);

        member.getMemberArtLikes().add(memberArtLike);
        art.getMemberArtLikes().add(memberArtLike);

        art.setLikeCount(art.getLikeCount()+1);

    }

    //아트 좋아요순 리스트 - 핫이슈 순
    public Page<ArtListDto> artLikeList(Principal principal, Pageable pageable){
        Member member = findMemberByMemberId(principal);
        List<Long> likedArtIds = memberArtLikeRepository.findArtByMember(member);
        Page<Art> artList = artRepository.findAllByOrderByLikeCountDesc(pageable);
        return pageLastLikeCommentConverter(artList, likedArtIds);
    }

    //아트 싫어요
    @Transactional
    public void unLikeArt (Principal principal, Long artId){

        Member member = findMemberByMemberId(principal);
        Art art = artRepository.findById(artId).orElseThrow(()-> new IllegalStateException("해당 아트가 없습니다. id=" + artId));
        boolean exists = memberArtLikeRepository.existsByMemberAndArt(member, art);
        if (!exists) {
            throw new IllegalStateException("좋아요 누른적 없음.");
        }

        art.setLikeCount(art.getLikeCount() - 1);
        memberArtLikeRepository.deleteByMemberAndArt(member, art);
    }



    //좋아요한 아트들만 보여주기
    public Page<ArtListDto> artFindByLiked(Principal principal, Pageable pageable){
        Pageable sortPageable = pageConverter(pageable);
        Member member = findMemberByMemberId(principal);
        List<Long> likedArtIds = memberArtLikeRepository.findArtByMember(member);

        Page<Art> artPage = artRepository.findArtByCommentId(likedArtIds, sortPageable);

        return pageLastLikeCommentConverter(artPage, likedArtIds);

    }

    //아트 전체 삭제
    @Transactional
    public void artAllDelete(){
        artRepository.deleteAll();
    }





    //-----------공통 메소드들-------------

    //멤버찾기
    public Member findMemberByMemberId(Principal principal){
        Long memberId = Long.parseLong(principal.getName());
        return memberRepository.findById(memberId).orElseThrow(()-> new IllegalStateException("해당 멤버가 없습니다. id=" + memberId));
    }


    //page<artByIdDto> 최종 출력할때 사용 - 뉴스마다 좋아요 확인을 위해 필요
    private Page<ArtListDto> pageLastLikeCommentConverter (Page<Art> artsPage, List<Long> likedArtIds){
        return artsPage.map(art -> {
            boolean liked = likedArtIds.contains(art.getArtId());
            return ArtListDto.from(art, liked);
        });
    }



    //클라이언트가 준 pageable -> 뉴스 최신순으로 보여주기 위해서 작성함 (페이지네이션에 sort가 포함되어 있어야해서 작성)
    private Pageable pageConverter(Pageable pageable){
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "artId"));
        return sortedPageable;
    }






//    //page<artByIdDto> 최종 출력할때 사용 - 뉴스마다 좋아요 + 댓글
//    private Page<ArtByIdDto> pageLastLikeCommentConverter (Page<Art> artsPage, List<Long> likedArtIds){
//        return artsPage.map(art -> {
//            boolean liked = likedArtIds.contains(art.getArtId());
//            List<ArtCommentResponseDto> commentList = artCommentService.findArtComment(art.getArtId());
//            return ArtByIdDto.from(art, liked, commentList);
//        });
//    }


}
