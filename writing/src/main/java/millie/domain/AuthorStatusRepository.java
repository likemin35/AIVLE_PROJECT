package millie.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorStatusRepository extends JpaRepository<AuthorStatus, Long> {
}