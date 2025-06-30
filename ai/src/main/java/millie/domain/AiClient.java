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

        String prompt = """
다음은 책의 본문과 메타 정보를 포함한 요청입니다:
- 책 제목: %s
- 저자 ID: %s
- 출판 요청 ID: %s
- 원고 ID: %s
- 발행 비용: %s
- 카테고리: %s
- 웹 URL: %s

본문:
%s

위 정보를 바탕으로 500자 내외의 요약과 메타데이터를 포함한 설명을 작성해주세요.
""".formatted(
                title,
                authorId,
                publishId,
                manuscriptId != null ? manuscriptId : "",
                cost != null ? cost : "",
                category,
                webUrl != null ? webUrl : "",
                content
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

        String imagePrompt = """
책 표지를 생성해 주세요. 다음 메타정보를 참고하세요:
- 제목: %s
- 저자 ID: %s
- 원고 ID: %s
- 출판 요청 ID: %s
- 카테고리: %s
- 발행 비용: %s
- 웹 URL: %s

줄거리 요약:
%s

스타일 가이드:
- 동양화, 수묵 느낌
- 몽환적, 잔잔함
- 파스텔 톤
- 연꽃, 물결, 구름
- 제목/저자 ID 포함
""".formatted(
                title,
                authorId,
                manuscriptId != null ? manuscriptId : "",
                publishId,
                category,
                cost != null ? cost : "",
                webUrl != null ? webUrl : "",
                summary
        );

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

        String prompt = """
다음 책의 가격을 1,000원 단위 정수로 산정해주세요.
- 제목: %s
- 카테고리: %s
- 요약: %s
- 본문 일부: %s

규칙:
1. 숫자만 출력
2. 최소 1,000 ~ 최대 100,000
""".formatted(
                title,
                category,
                summary,
                content.substring(0, Math.min(1000, content.length()))
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
