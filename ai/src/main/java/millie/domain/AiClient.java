package millie.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.stereotype.Component;

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

    public String summarizeContent(String content) throws Exception {
        String prompt = String.format("""
    다음은 책의 본문과 메타 정보를 포함하는 요청입니다:
    - 책 제목: %s
    - 저자 ID: %s
    - 출판 요청 ID: %s
    - 원고 ID: %s
    - 발행 비용: %s
    - 카테고리: %s
    - 기타 변수: %s

    본문 내용:
    %s

    위 정보를 바탕으로 아래 두 가지를 수행해주세요:
    1. 전체 줄거리를 핵심 포인트 위주로 500자 내외로 요약해주세요.
    2. 주요 변수(책 제목, 저자 ID, 원고 ID, 카테고리, 비용 등)를 바탕으로 메타 정보가 잘 표현된 요약도 포함해주세요.
    """,
         event.getTitle(),
         event.getAuthorId(),
         event.getId(),
         event.getManuscriptId(),
         event.getCost(),
         event.getCategory(),
         /* 기타 변수: 예시로 event.getWebUrl() 등 */ event.getWebUrl(),
         event.getContent()
);

        RequestBody body = RequestBody.create(mapper.writeValueAsString(Map.of(
            "model", "gpt-4",
            "messages", List.of(Map.of("role", "user", "content", prompt))
        )), MediaType.parse("application/json"));

        Request request = new Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
            .post(body)
            .build();

        try (Response response = client.newCall(request).execute()) {
            JsonNode root = mapper.readTree(response.body().string());
            return root.get("choices").get(0).get("message").get("content").asText();
        }
    }

    public String generateCover(String summary, String title, String genre) throws Exception {
        String imagePrompt = String.format("""
당신은 예술적인 책 표지 디자이너입니다.
다음은 책에 대한 메타정보입니다:
- 제목: %s
- 저자 ID: %s
- 원고 ID: %s
- 발행 요청 ID: %s
- 카테고리: %s
- 예상 발행 비용: %s
- 변수들: %s

줄거리 요약:
%s

위 내용을 바탕으로 다음 스타일로 표지를 만들어주세요:
- 스타일: 동양화, 수묵 느낌
- 분위기: 몽환적, 잔잔함
- 컬러톤: 파스텔 톤 + 은은한 검정 계열
- 요소: 연꽃, 물결, 구름 등 소설 분위기와 어울리는 심볼
- 텍스트 영역: 제목 위주로 강조, 저자 ID 및 원고 ID는 부제처럼 작게 디자인
""",
         event.getTitle(),
         event.getAuthorId(),
         event.getManuscriptId(),
         event.getId(),
         event.getCategory(),
         event.getCost(),
         event.getWebUrl() != null ? event.getWebUrl() : "",
         summary,
);


        RequestBody body = RequestBody.create(mapper.writeValueAsString(Map.of(
            "prompt", imagePrompt,
            "n", 1,
            "size", "512x768"
        )), MediaType.parse("application/json"));

        Request request = new Request.Builder()
            .url("https://api.openai.com/v1/images/generations")
            .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
            .post(body)
            .build();

        try (Response response = client.newCall(request).execute()) {
            JsonNode root = mapper.readTree(response.body().string());
            return root.get("data").get(0).get("url").asText();
        }
    }
}


public int predictBookPrice(String title, String summary, String category, String content) throws Exception {
    String prompt = String.format("""
당신은 전문 책 평가 AI입니다.

다음 책에 대해 다음과 같은 요소들을 종합적으로 평가해 가격을 책정하세요:

- 내용의 전문성/깊이
- 문장 구성과 전달력
- 감성적/창의적 요소
- 대중성 또는 틈새시장 가치
- 길이 및 구성의 완성도
- 독자에게 줄 수 있는 통찰력
- 시장에서 팔릴 수 있는 기대감

[책 정보]
제목: %s
카테고리: %s
요약: %s
본문 일부: %s

📌 규칙:
1. 당신은 단 하나의 숫자만 대답합니다.
2. 가격은 **1,000원 단위 정수**로 (예: 12000, 27000, 99000 등) 응답합니다.
3. **최소 1,000원 ~ 최대 100,000원** 범위 내에서 응답하세요.
4. '원' 등의 단위는 쓰지 말고 숫자만 출력하세요.

정답:
""", title, category, summary, content.substring(0, Math.min(1000, content.length())));

    RequestBody body = RequestBody.create(
        mapper.writeValueAsString(Map.of(
            "model", "gpt-4",
            "messages", List.of(Map.of("role", "user", "content", prompt))
        )),
        MediaType.parse("application/json")
    );

    Request request = new Request.Builder()
        .url("https://api.openai.com/v1/chat/completions")
        .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
        .post(body)
        .build();

    try (Response response = client.newCall(request).execute()) {
        JsonNode root = mapper.readTree(response.body().string());
        String result = root.get("choices").get(0).get("message").get("content").asText().trim();

        // 숫자만 추출 후 보정
        int rawPrice = Integer.parseInt(result.replaceAll("[^\\d]", ""));
        int rounded = (rawPrice / 1000) * 1000;

        // 1,000 ~ 100,000 제한 적용
        return Math.max(1000, Math.min(100000, rounded));
    }
}

