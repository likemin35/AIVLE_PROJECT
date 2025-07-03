package millie.infra;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import millie.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/books")
@Transactional
@CrossOrigin(origins = "*") // CORS 설정 추가
public class BookController {

    @Autowired
    BookRepository bookRepository;

    // 더미 데이터 생성 (임시)
    private List<BookDetailResponse> createDummyBooks() {
        return Arrays.asList(
            createBookDetail(1L, "보라공주와 일곱 난쟁이", "곽보라", 1100, 13240, "로맨스", 
                "/assets/sample1.png", "평범한 소녀가 마법의 세계에서 진정한 사랑을 찾아가는 이야기입니다."),
            createBookDetail(2L, "해리포터", "조하민", 1100, 15420, "스릴러",
                "/assets/sample1.png", "평범한 소년 해리가 마법의 세계에 발을 들이면서 시작되는 모험 이야기입니다."),
            createBookDetail(3L, "미움받을용기", "이원준", 1100, 8830, "SF",
                "/assets/sample1.png", "아들러 심리학을 바탕으로 한 인생 철학서입니다."),
            createBookDetail(4L, "하하엄마처럼 하하하", "장우진", 1100, 12650, "에세이",
                "/assets/sample1.png", "일상 속 작은 행복을 찾아가는 따뜻한 에세이입니다."),
            createBookDetail(5L, "정준하장가가기40일대작전", "김영진", 1100, 6420, "자기계발",
                "/assets/sample1.png", "40일 만에 인생을 바꾸는 자기계발서입니다."),
            createBookDetail(6L, "이기적유전자", "조은형", 1100, 21100, "로맨스",
                "/assets/sample1.png", "진화론의 관점에서 본 인간의 본성에 대한 이야기입니다."),
            createBookDetail(7L, "20대 투자에 미쳐라", "정병찬", 1100, 18750, "스릴러",
                "/assets/sample1.png", "젊은 세대를 위한 투자 가이드북입니다."),
            createBookDetail(8L, "개미", "이승환", 1100, 9340, "SF",
                "/assets/sample1.png", "베르나르 베르베르의 대표작입니다."),
            createBookDetail(9L, "수박은 장마철 이전이 맛있다", "윤성열", 1100, 7890, "에세이",
                "/assets/sample1.png", "계절의 변화와 함께하는 일상의 단상들입니다."),
            createBookDetail(10L, "명탐정코난", "한기영", 1100, 24680, "자기계발",
                "/assets/sample1.png", "추리와 모험이 가득한 탐정 이야기입니다.")
        );
    }

    private BookDetailResponse createBookDetail(Long id, String title, String author, 
            Integer price, Integer views, String category, String image, String summary) {
        BookDetailResponse book = new BookDetailResponse();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setPrice(price);
        book.setViews(views);
        book.setCategory(category);
        book.setImage(image);
        book.setSummary(summary);
        return book;
    }

    @GetMapping
    public ResponseEntity<List<BookDetailResponse>> getBooks(@RequestParam(required = false) String category) {
        List<BookDetailResponse> books = createDummyBooks();
        
        if (category != null && !category.isEmpty()) {
            books = books.stream()
                .filter(book -> category.equals(book.getCategory()))
                .collect(java.util.stream.Collectors.toList());
        }
        
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookDetailResponse> getBook(@PathVariable Long bookId) {
        List<BookDetailResponse> books = createDummyBooks();
        BookDetailResponse book = books.stream()
            .filter(b -> b.getId().equals(bookId))
            .findFirst()
            .orElse(null);
            
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(book);
    }

    // 응답 DTO 클래스 - 프론트엔드 기대 형식에 맞춤
    public static class BookDetailResponse {
        private Long id;
        private String title;
        private String author;
        private Integer price;
        private Integer views;
        private String category;
        private String image;
        private String summary;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public Integer getPrice() { return price; }
        public void setPrice(Integer price) { this.price = price; }
        public Integer getViews() { return views; }
        public void setViews(Integer views) { this.views = views; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
    }
}