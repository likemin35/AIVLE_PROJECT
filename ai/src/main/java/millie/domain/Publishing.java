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

    @Column
    private String image;             // AI 생성 표지 이미지 URL
    @Column
    private String summaryContent;    // 요약 내용
    private String bookName;          // 제목
    private String pdfPath;           // PDF 경로
    private String authorId;          // 저자 ID
    private String webUrl;            // 웹 주소
    private String category;          // 장르 (소설, 에세이 등)
    @Column
    private Integer cost;             // 가격 (연속형)
    private String title;
    private String content;

    @Embedded
    private ManuscriptId manuscriptId;

}
