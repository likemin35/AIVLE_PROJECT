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
import java.time.Duration;

import java.util.List;
import java.util.Map;

@Component
public class AiClient {

    OkHttpClient client = new OkHttpClient.Builder()
        .callTimeout(Duration.ofMinutes(60))         // 전체 호출 제한 60분
        .connectTimeout(Duration.ofSeconds(30))      // 연결 자체는 30초
        .readTimeout(Duration.ofMinutes(60))         // 응답 수신 60분
        .writeTimeout(Duration.ofMinutes(60))        // 요청 전송 60분
        .build();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.api.url}")
    private String openaiApiUrl;

    @Value("${openai.api.image-url}")
    private String openaiImageUrl;

    /**
     * 1) 요약
     */
    public String summarizeContent(String content) throws Exception {
        String prompt = "다음 동화책 내용을 300자 이내로 어린이도 이해하기 쉽게 요약해줘:\n\n" + content;

        RequestBody body = RequestBody.create(
                mapper.writeValueAsString(Map.of(
                        "model", "gpt-4o-mini",
                        "messages", List.of(
                                Map.of("role", "system", "content", "You are a helpful summarizer."),
                                Map.of("role", "user", "content", prompt)
                        )
                )),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(openaiApiUrl)
                .addHeader("Authorization", "Bearer " + openaiApiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String raw = response.body().string();
            System.out.println("OpenAI 응답: " + raw);

            JsonNode root = mapper.readTree(raw);
            JsonNode choices = root.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode contentNode = choices.get(0).get("message").get("content");
                if (contentNode != null) {
                    return contentNode.asText();
                }
            }
            throw new RuntimeException("summarizeContent() OpenAI 응답구조가 예상과 다릅니다: " + raw);
        }
    }

    /**
     * 2) 표지 생성
     */
    public String generateCover(String title, String authorId) throws Exception {
        String imagePrompt = "한국 전래동화 스타일의 표지를 그려줘.\n"
                + "제목: " + title + "\n"
                + "저자: " + authorId + "\n"
                + "동양화 느낌, 파스텔톤, 따뜻하고 몽환적인 분위기.";

        RequestBody body = RequestBody.create(
                mapper.writeValueAsString(Map.of(
                        "prompt", imagePrompt,
                        "n", 1,
                        "size", "1024x1024"
                )),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(openaiImageUrl)
                .addHeader("Authorization", "Bearer " + openaiApiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String raw = response.body().string();
            System.out.println("OpenAI 이미지 응답: " + raw);

            JsonNode root = mapper.readTree(raw);
            JsonNode data = root.get("data");
            if (data != null && data.isArray() && data.size() > 0) {
                JsonNode urlNode = data.get(0).get("url");
                if (urlNode != null) {
                    return urlNode.asText();
                }
            }
            throw new RuntimeException("generateCover() OpenAI 응답구조가 예상과 다릅니다: " + raw);
        }
    }

    /**
     * 3) 가격 측정
     */
    public int predictBookPrice(String title, String category, boolean isBestSeller, int viewCount, String content) throws Exception {
        String prompt = String.format(
                "당신은 전문 서적 가격 평가 AI입니다.\n\n"
                        + "책 제목: %s\n"
                        + "카테고리: %s\n"
                        + "베스트셀러 여부: %s\n"
                        + "조회수: %d\n"
                        + "본문 일부: %s\n\n"
                        + "위 요소를 참고해 합리적인 판매 가격(숫자만, 1000원 단위, 1000~100000)을 추천해줘.",
                title, category, isBestSeller, viewCount,
                content.substring(0, Math.min(1000, content.length()))
        );

        RequestBody body = RequestBody.create(
                mapper.writeValueAsString(Map.of(
                        "model", "gpt-4o-mini",
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
            String raw = response.body().string();
            System.out.println("OpenAI 가격 응답: " + raw);

            JsonNode root = mapper.readTree(raw);
            JsonNode choices = root.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode contentNode = choices.get(0).get("message").get("content");
                if (contentNode != null) {
                    String result = contentNode.asText().trim();
                    int rawPrice = Integer.parseInt(result.replaceAll("[^\\d]", ""));
                    int rounded = (rawPrice / 1000) * 1000;
                    return Math.max(1000, Math.min(100000, rounded));
                }
            }
            throw new RuntimeException("predictBookPrice() OpenAI 응답구조가 예상과 다릅니다: " + raw);
        }
    }

    /**
     * 4) 한 번에 enrich
     */
    public Publishing enrichPublishing(Publishing publishing) throws Exception {
        // 1. 요약
        String summary = summarizeContent(publishing.getContent());
        publishing.setSummaryContent(summary);

        // 2. 이미지
        String imageUrl = generateCover(publishing.getTitle(), publishing.getAuthorId());
        publishing.setImage(imageUrl);

        // 3. 가격
        int cost = predictBookPrice(
                publishing.getTitle(),
                publishing.getCategory(),
                false, // isBestSeller
                0,     // viewCount
                publishing.getContent()
        );
        publishing.setCost(cost);

        // 상태
        publishing.setAiStatus("COMPLETED");

        return publishing;
    }
}
