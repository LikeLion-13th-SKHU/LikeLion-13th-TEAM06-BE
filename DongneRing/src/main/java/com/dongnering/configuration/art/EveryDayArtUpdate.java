package com.dongnering.configuration.art;


import com.dongnering.art.api.dto.converter.ArtOpenApiToServer;
import com.dongnering.art.domain.Art;
import com.dongnering.art.domain.repository.ArtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

//매일 9:00 최신 예술 100개 조회 -> 이전에 등록된 정보는 저장 x

@Component
@RequiredArgsConstructor
public class EveryDayArtUpdate {

    private final ArtRepository artRepository;

    @Value("${serviceKey.openApi.art-secret}")
    private String OPENAPI_ART_SECRET;

    @Scheduled(cron = "0 0 8 * * *") // 매일 오전 8시 실행
    public void runScheduledJob() {

        //xml utf-8인코딩 설정
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .removeIf(c -> c instanceof org.springframework.http.converter.StringHttpMessageConverter);
        restTemplate.getMessageConverters()
                .add(1, new org.springframework.http.converter.StringHttpMessageConverter(java.nio.charset.StandardCharsets.UTF_8));



        String searchContentNumber = "50";

        try {

            String url = "https://apis.data.go.kr/B553457/cultureinfo/period2"
                    + "?serviceKey=" + OPENAPI_ART_SECRET
                    + "&PageNo=1"
                    + "&numOfrows=" + searchContentNumber;


            String xml = restTemplate.getForObject(url, String.class);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));

            XPath xpath = XPathFactory.newInstance().newXPath();

            NodeList newsItems = (NodeList) xpath.evaluate("/response/body/items/item", doc, XPathConstants.NODESET);

            for (int i = newsItems.getLength() - 1; i >= 0; i--){
                Node item = newsItems.item(i);

                //식별값 Long전환
                String identifyStr = xpath.evaluate("seq", item).trim();
                Long identifyId = Long.valueOf(identifyStr);

                String title = xpath.evaluate("title", item).trim();
                String startDate = xpath.evaluate("startDate", item).trim();
                String endDate = xpath.evaluate("endDate", item).trim();
                String location = xpath.evaluate("place", item).trim();
                String area = xpath.evaluate("area", item).trim();
                String imgUrl = xpath.evaluate("thumbnail", item).trim();

                ArtOpenApiToServer artOpenApiToServer =  new ArtOpenApiToServer(identifyId, title, startDate, endDate, location, area, imgUrl);

                if (!artRepository.existsByIdentifyId(artOpenApiToServer.identifyId())){
                    Art art = Art.builder()

                            .identifyId(artOpenApiToServer.identifyId())
                            .title(artOpenApiToServer.title())
                            .startDate(artOpenApiToServer.startDate())
                            .endDate(artOpenApiToServer.endDate())
                            .location(artOpenApiToServer.place())
                            .area(artOpenApiToServer.area())
                            .imageUrl(artOpenApiToServer.imageUrl())
                            .build();


                    artRepository.save(art);

                }


            } } catch (Exception e) {
            System.err.println("art 8시 업데이트 오류 발생 : " + e.getMessage());
        }




    }


}
