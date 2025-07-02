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
    private String title;
    @Column
    private String authorId;
    @Lob
    @Column
    private String content;
    @Column
    private String category;
    @Lob
    @Column
    private String image;
    @Lob
    @Column
    private String summaryContent;
    @Column
    private Integer cost;
    @Column
    private String aiStatus;
    @Column
    private String bookName;
    @Column
    private String pdfPath;
    @Column
    private String webUrl;



    @Embedded
    private ManuscriptId manuscriptId;

}
