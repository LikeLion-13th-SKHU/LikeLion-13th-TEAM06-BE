package com.dongnering.configuration.News;


import com.dongnering.news.api.dto.converter.NewsOpenApiToServerDto;
import com.dongnering.news.application.NewsService;
import com.dongnering.news.domain.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class NewsInitializerTestAiOut {

    private final NewsRepository newsRepository;
    private final NewsService newsService;

    @Value("${serviceKey.openApi.news-secret}")
    private String OPENAPI_NEWS_SECRET;

    @Bean
    public CommandLineRunner initNews() {
        return args -> {

            List<NewsOpenApiToServerDto> newsList = new ArrayList<>();

            //xml utf-8인코딩 설정
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters()
                    .removeIf(c -> c instanceof org.springframework.http.converter.StringHttpMessageConverter);
            restTemplate.getMessageConverters()
                    .add(1, new org.springframework.http.converter.StringHttpMessageConverter(java.nio.charset.StandardCharsets.UTF_8));


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

            LocalDate endDate = LocalDate.now(); // 오늘 날짜
//            LocalDate startLimitDate = endDate.minusMonths(1); // 한 달 전  -1주일전 세팅
            LocalDate startLimitDate = endDate.minusWeeks(1);

            LocalDate currentStartDate = startLimitDate;

            while (!currentStartDate.isAfter(endDate)) {
                LocalDate currentEndDate = currentStartDate.plusDays(2); // 3일 단위
                if (currentEndDate.isAfter(endDate)) {
                    currentEndDate = endDate; // 오늘을 넘어가지 않도록
                }

                String start = currentStartDate.format(formatter);
                String end = currentEndDate.format(formatter);

                try {
                    String url = "http://apis.data.go.kr/1371000/policyNewsService/policyNewsList"
                            + "?serviceKey=" + OPENAPI_NEWS_SECRET
                            + "&startDate=" + start
                            + "&endDate=" + end;

                    String xml = restTemplate.getForObject(url, String.class);

                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(new InputSource(new StringReader(xml)));

                    XPath xpath = XPathFactory.newInstance().newXPath();
                    NodeList newsItems = (NodeList) xpath.evaluate("/response/body/NewsItem", doc, XPathConstants.NODESET);

                    for (int i = newsItems.getLength()-1; i >=0; i--) {
                        Node item = newsItems.item(i);

                        String newsIdentifyIdtTmp = xpath.evaluate("NewsItemId", item).trim();
                        Long newsIdentifyId = Long.valueOf(newsIdentifyIdtTmp);

                        if (newsRepository.existsByNewsIdentifyId(Long.valueOf(newsIdentifyId))){
                            continue;
                        }

                        String title = xpath.evaluate("Title", item).trim();
                        String imageUrl = xpath.evaluate("OriginalimgUrl", item).trim();

                        String lawContents = xpath.evaluate("DataContents", item).trim();
                        String content = cdataConverter(lawContents);

                        // XPath로 값 읽기
                        String newDate = xpath.evaluate("ApproveDate", item).trim();

                         // 기존 형식의 포맷터
                        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
                        // 변환할 형식의 포맷터
                        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

                        // 문자열 → LocalDateTime → 다시 문자열
                        String formattedDate = LocalDateTime.parse(newDate, inputFormatter).format(outputFormatter);

                        newsService.newFirstSave(newsIdentifyId, title, imageUrl, content, formattedDate);




                        newsList.add(new NewsOpenApiToServerDto(newsIdentifyId.toString(), title, content, imageUrl));
                    }

                } catch (Exception e) {
                    System.err.println("news 초기 업데이트 오류 발생 [" + start + " ~ " + end + "]: " + e.getMessage());
                }

                currentStartDate = currentEndDate.plusDays(1);


            }






//            // 3. JSON으로 ai서버에 POST 전송
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            HttpEntity<List<NewsOpenApiToServerDto>> requestEntity = new HttpEntity<>(newsList, headers);
//
//            String postUrl = "http://localhost:8080/yourPostApi";
//            ResponseEntity<String> response = restTemplate.postForEntity(postUrl, requestEntity, String.class);
//
//            System.out.println("응답: " + response.getBody());



        };
    }


    private String cdataConverter(String content){

        String rawCdata = content;
        // 1. CDATA 제거
        String htmlContent = rawCdata
                .replace("<![CDATA[", "").replace("]]>", "");

        // 2. <style> 태그 제거
        htmlContent = htmlContent.replaceAll("(?is)<style.*?>.*?</style>", "");

        // 3. style 속성 제거
        htmlContent = htmlContent.replaceAll("(?i)\\sstyle\\s*=\\s*\"[^\"]*\"", "");

        // 2. HTML sanitize (XSS 방지) - 보안용
        String safeHtml = Jsoup.clean(htmlContent, Safelist.basicWithImages());

       return safeHtml;

    }


    //이미지, 하이퍼 링크 뺴고 주는거
//    private String cdataConverter(String content){
//
//        String rawCdata = content;
//        // 1. CDATA 제거
//        String htmlContent = rawCdata
//                .replace("<![CDATA[", "")
//                .replace("]]>", "");
//
//
//        // 1. <img> 태그 전체 제거
//        htmlContent = htmlContent.replaceAll("<img[^>]*>", "");
//
//        // 2. <a> 태그 전체 제거 (링크 텍스트만 남기고 태그는 제거)
//        htmlContent = htmlContent.replaceAll("<a[^>]*>", "");  // 시작 태그 제거
//        htmlContent = htmlContent.replaceAll("</a>", "");      // 종료 태그 제거
//
//        // 3. HTML 안에 있는 https://... URL 제거
//        htmlContent = htmlContent.replaceAll("https://[^\\s\"'>]+", "");
//
//        // 2. HTML sanitize (XSS 방지) - 보안용
//        String safeHtml = Jsoup.clean(htmlContent, Safelist.basicWithImages());
//
//        return safeHtml;
//
//    }






}



