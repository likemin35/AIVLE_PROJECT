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
    public String summarizeContent(String content, String category) throws Exception {
        String rules = 
        "다음 규칙을 먼저 인지해줘:\n\n"
        + "동화책: 어린이가 읽을 수 있도록 아주 쉬운 단어와 문장으로 설명해줘. 감정 표현과 교훈적인 메시지를 잘 살려줘.\n"
        + "소설: 10~40대가 주 독자층이며, 이해하기 쉽고 몰입할 수 있는 언어로 요약해줘. 등장인물과 사건의 흐름을 중심으로 다뤄줘.\n"
        + "에세이: 20~50대가 공감할 수 있도록, 편안하고 자연스러운 어조로 요약해줘. 필자의 생각이나 주제를 핵심적으로 전달해줘.\n"
        + "학습서: 학생과 일반인이 쉽게 이해할 수 있는 언어로, 핵심 개념과 주요 포인트 위주로 요약해줘.\n"
        + "자기계발서: 20~40대 직장인과 일반인에게 동기부여가 될 수 있도록, 실천 가능한 조언과 핵심 메시지를 중심으로 요약해줘.\n"
        + "만화: 어린이부터 청소년까지 재미있게 이해할 수 있도록, 줄거리의 핵심과 재미있는 요소를 잘 살려서 요약해줘.\n\n"
        + "---\n\n";

        String prompt = rules 
        + "책 카테고리: " + category + "\n"
        + "다음 책 내용을 300~900자 이내로 요약해줘. "
        + "너무 짧게 생략하지 말고, 중요한 흐름과 키워드, 감정, 메시지를 충분히 담아줘.\n\n"
        + content;
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
     * 1-1) 키워드 생성
     */
    public String extractKeywords(String summary) throws Exception {
        String prompt = 
                "다음 글에서 중요한 인물 이름, 장소, 사건, 상징적인 키워드만 콤마(,)로 구분해서 알려줘. " +
                "불필요한 문장은 빼고 키워드만 뽑아줘.\n\n" + summary;

        RequestBody body = RequestBody.create(
                mapper.writeValueAsString(Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a helpful keyword extractor."),
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
                System.out.println("OpenAI 키워드 응답: " + raw);

                JsonNode root = mapper.readTree(raw);
                JsonNode choices = root.get("choices");
                if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode contentNode = choices.get(0).get("message").get("content");
                if (contentNode != null) {
                        return contentNode.asText();
                }
                }
                throw new RuntimeException("extractKeywords() OpenAI 응답구조가 예상과 다릅니다: " + raw);
                }
        }
    /**
     * 2) 표지 생성
     */
    public String generateCover(String title, String category, String keywords) throws Exception {
        String imagePrompt =
                "다음 규칙을 먼저 인지해줘:\n\n"
                + "동화책: 어린이가 좋아할 수 있도록 따뜻하고 친근한 색감과 단순한 일러스트 중심의 표지.\n"
                + "소설: 10~40대가 몰입할 수 있도록 감성적이고 상징적인 요소를 강조한 표지.\n"
                + "에세이: 20~50대가 공감할 수 있는 편안하고 미니멀한 디자인의 표지.\n"
                + "학습서: 신뢰감과 전문성을 느낄 수 있는 깔끔하고 명료한 레이아웃의 표지.\n"
                + "자기계발서: 긍정적이고 동기부여를 주는 색상과 상징적인 이미지의 표지.\n"
                + "만화: 화려하고 재미있게 표현된 캐릭터 중심의 표지.\n\n"
                + "---\n\n"
                + "위 규칙을 참고해서, 아래 책의 카테고리에 맞는 표지를 만들어줘. "
                + "교보문고, 알라딘, 리디북스 등 실제 도서 사이트에서 볼 수 있는 상업적 표지 스타일로, "
                + "시장에서 통용되는 수준의 품질을 목표로 해줘. "
                + "책의 요약을 잘 반영하되, 표지는 구체적 장면보다는 상징적이고 직관적인 디자인으로 해줘.\n\n"
                + "책 카테고리: " + category + "\n"
                + "제목: " + title + "\n"
                + "주요 키워드: " + keywords + "\n" +
                "교보문고, 알라딘, 리디북스 등 상업적 표지처럼 시각적으로 완성도 높게 디자인해줘. " +
                "너무 구체적인 장면보다 상징적이고 직관적인 표지를 원해.";

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
        String summary = summarizeContent(publishing.getContent(), publishing.getCategory());
        publishing.setSummaryContent(summary);

        // 2. 이미지
        String imageUrl = generateCover(
                publishing.getTitle(),
                publishing.getCategory(),
                publishing.getContent()
        );
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
