package millie.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import millie.AiApplication;
import millie.domain.PublicationFailed;
import millie.domain.Published;

import javax.persistence.*;

@Entity
@Table(name = "Publishing_table")
@Data
public class Publishing {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String image;             // AI 생성 표지 이미지 URL
    private String summaryContent;    // 요약 내용
    private String bookName;          // 제목
    private String pdfPath;           // PDF 경로
    private String authorId;          // 저자 ID
    private String webUrl;            // 웹 주소
    private String category;          // 장르 (소설, 에세이 등)
    private Integer cost;             // 가격 (연속형)

    @Embedded
    private ManuscriptId manuscriptId;

}
