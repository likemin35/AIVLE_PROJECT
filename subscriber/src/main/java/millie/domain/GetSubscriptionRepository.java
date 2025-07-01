package millie.domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

@RepositoryRestResource(
    collectionResourceRel = "getSubscriptions", 
    path = "getSubscriptions"
)
public interface GetSubscriptionRepository 
    extends PagingAndSortingRepository<GetSubscription, Long> {
    
    // 사용자별 구독 정보 조회
    List<GetSubscription> findByUserId(Long userId);
    
    // 사용자별 대여 중인 책 조회
    List<GetSubscription> findByUserIdAndIsSubscription(Long userId, Boolean isSubscription);
    
    // 특정 책의 구독 정보 조회
    List<GetSubscription> findByBookId(Long bookId);
    
    // 사용자의 특정 책 구독 정보 조회
    GetSubscription findByUserIdAndBookId(Long userId, Long bookId);
}