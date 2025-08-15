package com.dongnering.configuration.News;


import com.dongnering.news.api.dto.converter.NewsOpenApiToServerDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
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


//@Component
public class EveryDayNewsUpdate {

    @Value("${serviceKey.openApi.news-secret}")
    private String OPENAPI_NEWS_SECRET;

    @Scheduled(cron = "0 0 8 * * *") // 매일 오전 98 실행
    public void runScheduledJob() {



            List<NewsOpenApiToServerDto> newsList = new ArrayList<>();
            RestTemplate restTemplate = new RestTemplate();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

            LocalDate endDate = LocalDate.now(); // 오늘 날짜로 설정
            LocalDate startDate = LocalDate.now();


                String start = startDate.format(formatter);
                String end = endDate.format(formatter);

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

                        newsList.add(new NewsOpenApiToServerDto(newsId, title, contents, imageUrl));
                    }

                } catch (Exception e) {
                    System.err.println("news 9시 업데이트 오류 발생 [" + start + " ~ " + end + "]: " + e.getMessage());
                }



            // 3. JSON으로 POST 전송
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<List<NewsOpenApiToServerDto>> requestEntity = new HttpEntity<>(newsList, headers);

            String postUrl = "http://localhost:8080/yourPostApi";
            ResponseEntity<String> response = restTemplate.postForEntity(postUrl, requestEntity, String.class);

            System.out.println("응답: " + response.getBody());



            System.out.println("스케줄러가 실행되었습니다!");
    }

}
