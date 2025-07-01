package millie.domain;

import lombok.Data;

@Data
public class RegisterManuscriptCommand {
    private String title;
    private String content;
    private String authorId;
}
