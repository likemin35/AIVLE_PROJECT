package millie.domain;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import millie.domain.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository
        extends PagingAndSortingRepository<User, Long> {

    // ✅ 이메일로 사용자 찾기 (로그인용)
    Optional<User> findByEmail(String email);
}