package com.dongnering.art.api.dto.converter;

public record ArtOpenApiToServer(
        
        
    Long identifyId,   
    String title,
    String startDate,
    String endDate,
    String area,
    String place,
    String imageUrl


) {
}
