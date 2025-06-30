package millie.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AiClient {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.api.url}")
    private String openaiApiUrl;

    @Value("${openai.api.image-url}")
    private String openaiImageUrl;

    // 1) 요약
    public String summarizeContent(
            String title,
            String authorId,
            String publishId,
            String manuscriptId,
            String cost,
            String category,
            String webUrl,
            String content
    ) throws Exception {

        String prompt = 
    "다음은 책의 본문과 메타 정보를 포함하는 요청입니다:\n" +
    "- 책 제목: " + title + "\n" +
    "- 저자 ID: " + authorId + "\n" +
    "- 출판 요청 ID: " + publishId + "\n" +
    "- 원고 ID: " + manuscriptId + "\n" +
    "- 발행 비용: " + cost + "\n" +
    "- 카테고리: " + category + "\n" +
    "- 웹 URL: " + webUrl + "\n\n" +
    "본문 내용:\n" + content + "\n\n" +
    "위 정보를 바탕으로 아래 두 가지를 수행해주세요:\n" +
    "1. 전체 줄거리를 핵심 포인트 위주로 500자 내외로 요약해주세요.\n" +
    "2. 주요 변수(책 제목, 저자 ID, 원고 ID, 카테고리, 비용 등)를 메타데이터처럼 자연스럽게 포함한 요약을 작성해주세요.";


        RequestBody body = RequestBody.create(
                mapper.writeValueAsString(Map.of(
                        "model", "gpt-4o",
                        "messages", List.of(Map.of("role", "user", "content", prompt))
                )),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(openaiApiUrl)
                .addHeader("Authorization", "Bearer " + openaiApiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            JsonNode root = mapper.readTree(response.body().string());
            return root.get("choices").get(0).get("message").get("content").asText();
        }
    }

    // 2) 표지 생성
    public String generateCover(
            String title,
            String authorId,
            String manuscriptId,
            String publishId,
            String category,
            String cost,
            String webUrl,
            String summary
    ) throws Exception {

        String imagePrompt = 
    "당신은 예술적인 책 표지 디자이너입니다.\n" +
    "다음은 책에 대한 메타정보입니다:\n" +
    "- 제목: " + title + "\n" +
    "- 저자 ID: " + authorId + "\n" +
    "- 원고 ID: " + manuscriptId + "\n" +
    "- 발행 요청 ID: " + publishId + "\n" +
    "- 카테고리: " + category + "\n" +
    "- 예상 발행 비용: " + cost + "\n" +
    "- 웹 URL: " + webUrl + "\n\n" +
    "줄거리 요약:\n" + summary + "\n\n" +
    "위 내용을 바탕으로 다음 스타일로 표지를 만들어주세요:\n" +
    "- 스타일: 동양화, 수묵 느낌\n" +
    "- 분위기: 몽환적, 잔잔함\n" +
    "- 컬러톤: 파스텔 톤 + 은은한 검정 계열\n" +
    "- 요소: 연꽃, 물결, 구름\n" +
    "- 제목과 저자ID를 텍스트로 포함";


        RequestBody body = RequestBody.create(
                mapper.writeValueAsString(Map.of(
                        "model", "dall-e-3",
                        "prompt", imagePrompt,
                        "n", 1,
                        "size", "512x768"
                )),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(openaiImageUrl)
                .addHeader("Authorization", "Bearer " + openaiApiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            JsonNode root = mapper.readTree(response.body().string());
            return root.get("data").get(0).get("url").asText();
        }
    }

    // 3) 가격 측정
    public int predictBookPrice(
            String title,
            String summary,
            String category,
            String content
    ) throws Exception {

        String prompt = String.format(
    "당신은 전문 책 평가 AI입니다.\n\n"
    + "다음 책에 대해 다음 요소를 종합적으로 고려해 가격을 책정하세요:\n"
    + "- 전문성/깊이\n"
    + "- 문장 전달력\n"
    + "- 감성/창의성\n"
    + "- 대중성 또는 틈새성\n"
    + "- 구성과 완성도\n"
    + "- 시장에서 팔릴 기대감\n\n"
    + "[책 정보]\n"
    + "제목: %s\n"
    + "카테고리: %s\n"
    + "요약: %s\n"
    + "본문 일부: %s\n\n"
    + "규칙:\n"
    + "1. 단 하나의 숫자만 출력\n"
    + "2. 1,000원 단위 정수\n"
    + "3. 최소 1,000 ~ 최대 100,000\n"
    + "4. '원' 단위 말고 숫자만 응답",
    title, category, summary, content.substring(0, Math.min(1000, content.length()))
);


        RequestBody body = RequestBody.create(
                mapper.writeValueAsString(Map.of(
                        "model", "gpt-4o",
                        "messages", List.of(Map.of("role", "user", "content", prompt))
                )),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(openaiApiUrl)
                .addHeader("Authorization", "Bearer " + openaiApiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            JsonNode root = mapper.readTree(response.body().string());
            String result = root.get("choices").get(0).get("message").get("content").asText().trim();
            int rawPrice = Integer.parseInt(result.replaceAll("[^\\d]", ""));
            int rounded = (rawPrice / 1000) * 1000;
            return Math.max(1000, Math.min(100000, rounded));
        }
    }
}
