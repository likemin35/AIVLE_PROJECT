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
            JsonNode root = mapper.readTree(response.body().string());
            return root.get("choices").get(0).get("message").get("content").asText();
        }
    }

    // 2) 표지 생성
    public String generateCover(String title, String authorId) throws Exception {
        String imagePrompt = "한국 전래동화 스타일의 표지를 그려줘.\n"
                + "제목: " + title + "\n"
                + "저자: " + authorId + "\n"
                + "동양화 느낌, 파스텔톤, 따뜻하고 몽환적인 분위기.";

        RequestBody body = RequestBody.create(
                mapper.writeValueAsString(Map.of(
                        "model", "gpt-4o-mini",
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

    // 3) 가격 측정 (GPT 연속형)
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
            JsonNode root = mapper.readTree(response.body().string());
            String result = root.get("choices").get(0).get("message").get("content").asText().trim();
            int rawPrice = Integer.parseInt(result.replaceAll("[^\\d]", ""));
            int rounded = (rawPrice / 1000) * 1000;
            return Math.max(1000, Math.min(100000, rounded));
        }
    }
}
