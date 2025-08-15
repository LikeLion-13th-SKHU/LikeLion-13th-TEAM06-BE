package com.dongnering.configuration.News;


import com.dongnering.news.api.dto.converter.NewsOpenApiToServerDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@Configuration  -> 함부로 제거 금지
public class NewsInitializer {


    @Value("${serviceKey.openApi.news-secret}")
    private String OPENAPI_NEWS_SECRET;

//    @Bean -> 함부로 제거 금지
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
            LocalDate startLimitDate = endDate.minusMonths(1); // 한 달 전

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

                    for (int i = 0; i < newsItems.getLength(); i++) {
                        Node item = newsItems.item(i);

                        String newsId = xpath.evaluate("NewsItemId", item).trim();
                        String title = xpath.evaluate("Title", item).trim();
                        String contents = xpath.evaluate("DataContents", item).trim();
                        String imageUrl = xpath.evaluate("OriginalimgUrl", item).trim();


                        //날짜 변환
                        String newDate = xpath.evaluate("ApproveDate", item).trim();
                        // 기존 형식
                        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                        // 원하는 형식
                        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy.MM.dd");

                        Date date = inputFormat.parse(newDate); // 문자열 → Date
                        String formattedDate = outputFormat.format(date); // Date → 문자열


                        newsList.add(new NewsOpenApiToServerDto(newsId, title, contents, imageUrl));
                    }

                } catch (Exception e) {
                    System.err.println("news 초기 업데이트 오류 발생 [" + start + " ~ " + end + "]: " + e.getMessage());
                }

                // 다음 3일 구간으로 이동
                currentStartDate = currentEndDate.plusDays(1);
            }

            // 3. JSON으로 ai서버에 POST 전송
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<List<NewsOpenApiToServerDto>> requestEntity = new HttpEntity<>(newsList, headers);

            String postUrl = "http://localhost:8080/yourPostApi";
            ResponseEntity<String> response = restTemplate.postForEntity(postUrl, requestEntity, String.class);

            System.out.println("응답: " + response.getBody());



        };
    }
}



