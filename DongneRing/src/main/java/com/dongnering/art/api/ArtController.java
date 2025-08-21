package com.dongnering.art.api;


import com.dongnering.art.api.dto.ArtLikeDisLikeDto;
import com.dongnering.art.api.dto.response.ArtListDto;
import com.dongnering.art.api.dto.response.ArtSingleByIdDto;
import com.dongnering.art.application.ArtService;

import com.dongnering.common.error.SuccessCode;
import com.dongnering.common.template.ApiResTemplate;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/art")
public class ArtController {

    private final ArtService artService;

    //art 지역별 조회 - 멤버가 설정한 지역 art만 보여줍니다.
    @GetMapping
    @Operation(summary = "art 지역별 조회", description = "멤버가 설정한 지역 art만 보여줍니다.")
    public ApiResTemplate<Page<ArtListDto>> artFindByLocation(Principal principal, @ParameterObject Pageable pageable){
        Page<ArtListDto> artList = artService.artFindByLocation(principal,pageable);

        System.out.println(artList.stream().toList());

        return ApiResTemplate.successResponse(SuccessCode.ARTS_GET_SUCCESS, artList);

    }

    //단일 art 조회
    @GetMapping("/{artId}")
    @Operation(summary = "단일 art 조회", description = "단일 art 조회")
    public ApiResTemplate<ArtSingleByIdDto> artFindById(Principal principal, @PathVariable Long artId){
        ArtSingleByIdDto artByIdDto = artService.artFindById(principal, artId);
        return ApiResTemplate.successResponse(SuccessCode.ARTS_GET_SUCCESS, artByIdDto);
    }

    //아트 전체 조회
    @GetMapping("/all")
    @Operation(summary = "art 전체 조회", description = "art 전체 조회")
    public ApiResTemplate<Page<ArtListDto>> artFindAll(Principal principal, @ParameterObject Pageable pageable){
        Page<ArtListDto> artList = artService.artFindAll(principal, pageable);
        return ApiResTemplate.successResponse(SuccessCode.ARTS_GET_SUCCESS, artList);
    }

    //아트 좋아요 순 - 실시간 핫이슈 아트
    @GetMapping("/artlike")
    @Operation(summary = "실시간 핫이슈 아트", description = "아트 좋아요 순")
    public ApiResTemplate<Page<ArtListDto>> newsLikeList(Principal principal, @ParameterObject Pageable pageable){
        Page<ArtListDto> newsList = artService.artLikeList(principal, pageable);
        return ApiResTemplate.successResponse(SuccessCode.ARTS_GET_SUCCESS, newsList);
    }

    //art 좋아요 통합
    @PostMapping("/like")
    @Operation(summary = "art 좋아요 통합(토글식)", description = "art 좋아요 통합(토글식)")
    public ApiResTemplate<Boolean> artLikeUnlike(Principal principal, @RequestBody ArtLikeDisLikeDto artLikeDisLikeDto){
        boolean artLikeStatus = artService.likeUnlikeArt(principal, artLikeDisLikeDto);
        if (artLikeStatus){
            return ApiResTemplate.successResponse(SuccessCode.LIKE_SUCCESS, artLikeStatus);
        }
        return ApiResTemplate.successResponse(SuccessCode.UNLIKE_SUCCESS, artLikeStatus);
    }


    //멤버가 좋아요한 아트만 보여주기
    @GetMapping("/personalLike")
    @Operation(summary = "멤버가 좋아요한 아트만 보여주기", description = "멤버가 좋아요한 아트만 보여주기")
    public ApiResTemplate<Page<ArtListDto>> artFinByPersonalLike(Principal principal, @ParameterObject Pageable pageable){
        Page<ArtListDto> artList = artService.artFindByLiked(principal, pageable);
        return ApiResTemplate.successResponse(SuccessCode.ARTS_GET_SUCCESS, artList);
    }

    @DeleteMapping("/artDeleteAll")
    @Operation(summary = "모든 아트 지우기", description = "모든 아트 지우기")
    public ApiResTemplate<String> artDeleteAll(Principal principal){
        artService.artAllDelete();
        return ApiResTemplate.successWithNoContent(SuccessCode.ART_DELETE_SUCCESS);
    }



}
