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
        "Please follow these summarization rules based on the book category:\n\n"
        + "Children's books: Summarize using very simple words and sentences so children can understand easily. Highlight emotions and moral lessons.\n"
        + "Novels: Summarize in easy-to-understand and immersive language for readers in their teens to 40s. Focus on the main characters and the storyline.\n"
        + "Essays: Summarize in a natural, comfortable tone so readers in their 20s to 50s can relate. Deliver the author's core message and thoughts clearly.\n"
        + "Study books: Summarize using clear language that students and general readers can easily understand. Emphasize key concepts and important points.\n"
        + "Self-help: Summarize with a motivational and actionable tone for readers in their 20s to 40s, focusing on practical advice and inspiring messages.\n"
        + "Comics: Summarize in a fun and lively style that appeals to children and teenagers, highlighting the main storyline and entertaining elements.\n\n"
        + "---\n\n";

        String prompt = rules
        + "Book category: " + category + "\n"
        + "Please summarize the following book content in 300 to 900 characters. "
        + "Do not leave out important details, and make sure to include the main flow, key characters, emotions, and messages clearly.\n\n"
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
 * 표지 이미지 생성 (요약 포함)
 */
public String generateCover(String title, String content) throws Exception {
    // 1) content 요약 (GPT에게)
    String summarized = summarizeContent_1(content);

    // 2) 프롬프트 구성
    String imagePrompt = String.format(
        "%s. Theme: \"%s\". No text inside. Focus on illustration, mood, details, symbols.",
        summarized,
        title
    );

    // 3) 이미지 요청
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
 * 500자 이내 요약, 영어 2문장
 */
public String summarizeContent_1(String content) throws Exception {
    RequestBody body = RequestBody.create(
        mapper.writeValueAsString(Map.of(
            "model", "gpt-4o-mini",
            "messages", List.of(
                Map.of(
                    "role", "system",
                    "content",
                    "You are a helper summarizing prompts for DALL·E. "
                    + "Please summarize the user's book description in no more than 500 characters, "
                    + "and respond ONLY as a comma-separated list of concise English keywords, "
                    + "including these elements: "
                    + "1) Theme, 2) Style, 3) Colors, 4) Composition, 5) Mood/Emotion, "
                    + "6) Details like props, background, or lighting. "
                ),
                Map.of("role", "user", "content", content)
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
        System.out.println("OpenAI 요약 응답: " + raw);

        JsonNode root = mapper.readTree(raw);
        JsonNode choices = root.get("choices");
        if (choices != null && choices.isArray() && choices.size() > 0) {
            JsonNode contentNode = choices.get(0).get("message").get("content");
            if (contentNode != null) {
                return contentNode.asText();
            }
        }
        throw new RuntimeException("summarizeContent_1() OpenAI 응답구조가 예상과 다릅니다: " + raw);
    }
}


    /**
     * 3) 가격 측정
     */
    public int predictBookPrice(String title, String category, boolean isBestSeller, int viewCount, String content) throws Exception {
        String prompt = String.format(
                "You are a professional book pricing evaluation AI.\n\n"
                        + "Title: %s\n"
                        + "Category: %s\n"
                        + "Bestseller: %s\n"
                        + "View count: %d\n"
                        + "Content excerpt: %s\n\n"
                        + "Based on these factors, please recommend a reasonable sales price (numbers only, in units of 1,000 KRW, between 1,000 and 100,000).",
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
