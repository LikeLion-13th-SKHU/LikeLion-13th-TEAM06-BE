package com.dongnering.configuration.art;


import com.dongnering.art.api.dto.converter.ArtOpenApiToServer;
import com.dongnering.art.domain.Art;
import com.dongnering.art.domain.repository.ArtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ArtInitializer {

    private final ArtRepository artRepository;

    @Value("${serviceKey.openApi.art-secret}")
    private String OPENAPI_ART_SECRET;

    @Bean
    @Order(3)
    public CommandLineRunner initArt(){


        return args -> {

            List<ArtOpenApiToServer> artList = new ArrayList<>();

            //xml utf-8인코딩 설정
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters()
                    .removeIf(c -> c instanceof org.springframework.http.converter.StringHttpMessageConverter);
            restTemplate.getMessageConverters()
                    .add(1, new org.springframework.http.converter.StringHttpMessageConverter(java.nio.charset.StandardCharsets.UTF_8));



            //예술 조회 개수 -> 1000개
            String searchContentNumber = "1000";


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

                    String identifyStr = xpath.evaluate("seq", item).trim();
                    Long identifyId = Long.valueOf(identifyStr);

                    String title = xpath.evaluate("title", item).trim();

                    String startDateBefore = xpath.evaluate("startDate", item).trim();


                    String startDate = dateChange(startDateBefore);

                    String endDateBefore = xpath.evaluate("endDate", item).trim();

                    String endDate = dateChange(endDateBefore);




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
                                .likeCount(0L)
                                .build();


                        artRepository.save(art);

                    }



                } } catch (Exception e) {
                System.err.println("art 초기 업데이트 오류 발생 : " + e.getMessage());
            }

            System.out.println("초기 아트 데이터 세팅 완료");
        };


    }

    private String dateChange(String startDate) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        LocalDate date = LocalDate.parse(startDate, inputFormatter);
        String formatted = date.format(outputFormatter);

        System.out.println("test : " + formatted);

        return formatted;
    }

}
