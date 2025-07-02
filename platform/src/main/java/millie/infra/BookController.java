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
@CrossOrigin(origins = "*")
public class BookController {

    @Autowired
    BookRepository bookRepository;

    // 더미 데이터 생성 (임시)
    private List<Book> createDummyBooks() {
        return Arrays.asList(
            createBook(1L, "보라공주와 일곱 난쟁이", "곽보라", 1100, 13240, "로맨스"),
            createBook(2L, "해리포터", "조하민", 1100, 15420, "스릴러"),
            createBook(3L, "미움받을용기", "이원준", 1100, 8830, "SF"),
            createBook(4L, "하하엄마처럼 하하하", "장우진", 1100, 12650, "에세이"),
            createBook(5L, "정준하장가가기40일대작전", "김영진", 1100, 6420, "자기계발"),
            createBook(6L, "이기적유전자", "조은형", 1100, 21100, "로맨스"),
            createBook(7L, "20대 투자에 미쳐라", "정병찬", 1100, 18750, "스릴러"),
            createBook(8L, "개미", "이승환", 1100, 9340, "SF"),
            createBook(9L, "수박은 장마철 이전이 맛있다", "윤성열", 1100, 7890, "에세이"),
            createBook(10L, "명탐정코난", "한기영", 1100, 24680, "자기계발")
        );
    }

    private Book createBook(Long id, String title, String author, Integer price, Integer views, String category) {
        Book book = new Book();
        book.setId(id);
        book.setBookName(title);
        book.setAuthorName(author);
        book.setPoint(price);
        book.setCost(price);
        book.setViewCount(views);
        book.setCategory(category);
        book.setIsBestSeller(views > 15000);
        return book;
    }

    @GetMapping
    public ResponseEntity<List<Book>> getBooks(@RequestParam(required = false) String category) {
        List<Book> books = createDummyBooks();
        
        if (category != null && !category.isEmpty()) {
            books = books.stream()
                .filter(book -> category.equals(book.getCategory()))
                .collect(java.util.stream.Collectors.toList());
        }
        
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookDetailResponse> getBook(@PathVariable Long bookId) {
        List<Book> books = createDummyBooks();
        Book book = books.stream()
            .filter(b -> b.getId().equals(bookId))
            .findFirst()
            .orElse(null);
            
        if (book == null) {
            return ResponseEntity.notFound().build();
        }

        BookDetailResponse response = new BookDetailResponse();
        response.setId(book.getId());
        response.setTitle(book.getBookName());
        response.setAuthor(book.getAuthorName());
        response.setPrice(book.getPoint());
        response.setViews(book.getViewCount());
        response.setCategory(book.getCategory());
        response.setImage("/assets/sample1.png");
        response.setSummary("평범한 소년 해리가 마법의 세계에 발을 들이면서 시작되는 모험 이야기입니다. 호그와트 마법학교에서 친구들과 함께 성장하며, 어둠의 마법사와 맞서 싸우는 용기와 우정의 서사시입니다.");
        
        return ResponseEntity.ok(response);
    }

    // 응답 DTO 클래스
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