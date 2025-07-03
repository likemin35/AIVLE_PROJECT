// 제거될 가능성 매우 높음 Id 기준 정렬이 필요할거 같아서 만듬 필요 없을거 같으면 제거
// Publishing 엔티티에서  
// @Embedded
// private ManuscriptId manuscriptId;
// 같이 제거
package millie.domain;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ManuscriptId implements Serializable {

    private String id;

    public ManuscriptId() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}
