package com.dongnering.configuration.News;


import com.dongnering.interest.domain.InterestType;
import com.dongnering.news.api.dto.converter.NewsAiToServerDto;
import com.dongnering.news.api.dto.converter.NewsOpenApiToServerDto;
import com.dongnering.news.application.NewsService;
import com.dongnering.news.domain.repository.NewsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
public class EveryDayNewsUpdate {

    private final NewsRepository newsRepository;
    private final NewsService newsService;

    @Value("${serviceKey.openApi.news-secret}")
    private String OPENAPI_NEWS_SECRET;

    @Scheduled(cron = "0 0 7-19/3 * * *") //오전 7 - 오후7시까지 3시간마다
    public void runScheduledJob() {
        System.out.println("뉴스 스케줄러 실행 시작");

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .removeIf(c -> c instanceof org.springframework.http.converter.StringHttpMessageConverter);
        restTemplate.getMessageConverters()
                .add(1, new org.springframework.http.converter.StringHttpMessageConverter(java.nio.charset.StandardCharsets.UTF_8));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String today = LocalDate.now().format(formatter);

        //오늘 뉴스 가져오기
        List<NewsOpenApiToServerDto> newsList = fetchNewsFromApi(today, today, restTemplate);

        newsList.forEach(news -> {
            if (!newsRepository.existsByNewsIdentifyId(Long.valueOf(news.newsIdentifyId()))) {
                newsService.newFirstSave(
                        Long.valueOf(news.newsIdentifyId()),
                        news.title(),
                        news.pictureUrl(),
                        news.contents(),
                        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                );
            }
        });

        postNewsList(newsList);
        System.out.println("뉴스 스케줄러 실행 완료!");
    }

    private List<NewsOpenApiToServerDto> fetchNewsFromApi(String start, String end, RestTemplate restTemplate) {
        List<NewsOpenApiToServerDto> newsList = new ArrayList<>();
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
                String content = cdataConverter(xpath.evaluate("DataContents", item).trim());
                String imageUrl = xpath.evaluate("OriginalimgUrl", item).trim();

                newsList.add(new NewsOpenApiToServerDto(newsId, title, content, imageUrl));
            }
        } catch (Exception e) {
            System.err.println("뉴스 가져오기 오류 [" + start + " ~ " + end + "]: " + e.getMessage());
        }
        return newsList;
    }

    private void postNewsList(List<NewsOpenApiToServerDto> list) {
        String postUrl = "http://youhayeong.shop/run/json";

        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();

            // 실제 POST 전송 시
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<List<NewsOpenApiToServerDto>> requestEntity = new HttpEntity<>(list, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(postUrl, requestEntity, String.class);

            // 응답 본문(JSON 문자열) 파싱
            String responseBody = response.getBody();
            if (responseBody != null) {
                JsonNode root = objectMapper.readTree(responseBody);

                for (JsonNode item : root) {
                    //뉴스 식별 아이디
                    String id = item.get("NewsItemId").asText();
                    Long newsIdentifyId = Long.valueOf(id);

                    //뉴스 관심사 태그
                    JsonNode categoryNode = item.get("category");
                    List<InterestType> categories = new ArrayList<>();

                    if (categoryNode != null) {
                        if (categoryNode.isArray()) {
                            // 배열일 때: 빈 문자열 제외하고 추가
                            for (JsonNode c : categoryNode) {
                                String value = c.asText();
                                if (!value.isBlank()) {
                                    addCategory(categories, value);
                                }
                            }
                        } else if (!categoryNode.isNull()) {
                            // 단일 문자열일 때: 빈 문자열 제외
                            String value = categoryNode.asText();
                            if (!value.isBlank()) {
                                addCategory(categories, value);
                            }
                        }
                    }


                    //뉴스 요약
                    JsonNode newsSummaryNode = item.get("summary_lines");
                    List<String> newsSummarys = new ArrayList<>();

                    if (newsSummaryNode != null && newsSummaryNode.isArray()) {
                        for (JsonNode c : newsSummaryNode) {
                            String value = c.asText();
                            if (!value.isBlank()) { // 빈 문자열 필터링
                                newsSummarys.add(value);
                            }
                        }
                    } else if (newsSummaryNode != null && !newsSummaryNode.isNull()) {
                        String value = newsSummaryNode.asText();
                        if (!value.isBlank()) {
                            newsSummarys.add(value);
                        }
                    }

                    //뉴스 태그
                    JsonNode newsTagNode = item.get("subcategories");
                    List<String> newsTags = new ArrayList<>();

                    if (newsTagNode != null) {
                        if (newsTagNode.isArray()) {
                            for (JsonNode tag : newsTagNode) {
                                String value = tag.asText();
                                if (!value.isBlank()) { // 빈 문자열 필터링
                                    newsTags.add(value);
                                }
                            }
                        } else if (!newsTagNode.isNull()) {
                            String value = newsTagNode.asText();
                            if (!value.isBlank()) {
                                newsTags.add(value);
                            }
                        }
                    }


                    //뉴스 지역
                    String location = item.get("region").asText();

                    //뉴스 추가 정보 저장
                    NewsAiToServerDto build = NewsAiToServerDto.builder()
                            .newsIdentifyId(newsIdentifyId)
                            .category(categories)
                            .summary(newsSummarys)
                            .tag(newsTags)
                            .location(location)
                            .build();


                    newsService.newsUpdateAiConnectAfter(build);

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("뉴스 데일리 스케줄러 실행 완료!");
    }


    // 카테고리 매핑 함수
    private void addCategory(List<InterestType> categories, String i) {
        switch (i) {
            case "정책_정부" -> categories.add(InterestType.POLICY_GOVERNMENT);
            case "산업_기업" -> categories.add(InterestType.INDUSTRY_COMPANY);
            case "연구_기술" -> categories.add(InterestType.RESEARCH_TECHNOLOGY);
            case "규제_제도" -> categories.add(InterestType.REGULATION_SYSTEM);
            case "수출_글로벌" -> categories.add(InterestType.EXPORT_GLOBAL);
            case "투자_금융" -> categories.add(InterestType.INVESTMENT_FINANCE);
            case "인사_조직" -> categories.add(InterestType.HR_ORGANIZATION);
            case "사회"     -> categories.add(InterestType.SOCIETY);
            case "기타"     -> categories.add(InterestType.OTHERS);
        }
    }



    private String cdataConverter(String content) {
        String htmlContent = content.replace("<![CDATA[", "").replace("]]>", "");
        htmlContent = htmlContent.replaceAll("(?is)<style.*?>.*?</style>", "");
        htmlContent = htmlContent.replaceAll("(?i)\\sstyle\\s*=\\s*\"[^\"]*\"", "");
        return Jsoup.clean(htmlContent, Safelist.basicWithImages());
    }
}