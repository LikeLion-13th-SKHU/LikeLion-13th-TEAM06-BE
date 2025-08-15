package com.dongnering.comment.artComment.application;



import com.dongnering.art.domain.Art;
import com.dongnering.art.domain.repository.ArtRepository;
import com.dongnering.comment.artComment.api.dto.request.ArtCommentRequestDto;
import com.dongnering.comment.artComment.api.dto.response.ArtCommentResponseDto;
import com.dongnering.comment.artComment.domain.ArtComment;
import com.dongnering.comment.artComment.domain.repository.ArtCommentRepository;
import com.dongnering.mypage.domain.Member;
import com.dongnering.mypage.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtCommentService {

    private final ArtCommentRepository artCommentRepository;
    private final MemberRepository memberRepository;
    private final ArtRepository artRepository;

    //아트 댓글 생성
    @Transactional
    public void createArtComment(ArtCommentRequestDto artCommentRequestDto, Principal principal){

        Long memberId = Long.parseLong(principal.getName());
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new IllegalStateException("해당 멤버가 없습니다. id=" + memberId));;

        Art art = artRepository.findById(artCommentRequestDto.artId()).orElseThrow(() -> new IllegalStateException("해당 아트가 없습니다. id=" + artCommentRequestDto.artId()));

        if (!artCommentRepository.existsByArtAndMember(art, member)){
            ArtComment artComment = ArtComment.builder()
                    .art(art)
                    .member(member)
                    .content(artCommentRequestDto.content())
                    .build();

            artCommentRepository.save(artComment);

        }else {
            throw new IllegalStateException("이미 댓글이 존재합니다.");
        }

    }

    //아트 댓글 삭제
    @Transactional
    public void deleteArtComment(Long artId, Principal principal){

        Long memberId = Long.parseLong(principal.getName());
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new IllegalStateException("해당 멤버가 없습니다. id=" + memberId));;

        Art art = artRepository.findById(artId).orElseThrow(() -> new IllegalStateException("해당 아트가 없습니다. id=" + artId));

        if (artCommentRepository.existsByArtAndMember(art, member)){
            ArtComment artComment = artCommentRepository.findByArtAndMember(art, member);
            artCommentRepository.delete(artComment);
        }
        else {
        throw new IllegalStateException("작성한 댓글이 없습니다.");
        }

    }

    //아트 댓글 반환
    public List<ArtCommentResponseDto> findArtComment(Long artId){

        Art art = artRepository.findById(artId).orElseThrow(() -> new IllegalStateException("해당 아트가 없습니다. id=" + artId));
        List<ArtComment> artCommentList = artCommentRepository.findAllByArt(art);

        return artCommentList.stream().map(ArtCommentResponseDto::from).toList();
    }

    //아트 댓글 개수 반환
    public Long findArtCommentCount(Art art){
        return artCommentRepository.countByArt(art);
    }









}
