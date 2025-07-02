package millie.domain;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AuthorId implements Serializable {

    private String id;

    protected AuthorId() {
        this.id = null;
    }

    public AuthorId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthorId)) return false;
        AuthorId authorId = (AuthorId) o;
        return Objects.equals(id, authorId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
