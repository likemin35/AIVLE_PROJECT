package millie.infra;

import millie.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class GetSubscriptionService {

    @Autowired
    private GetSubscriptionRepository getSubscriptionRepository;

    // ✅ 사용자별 구독 목록 조회 메서드 추가
    public List<GetSubscription> getUserSubscriptions(Long userId) {
        return getSubscriptionRepository.findByUserId(userId);
    }

    // ✅ 사용자의 대여 중인 책들만 조회
    public List<GetSubscription> getUserActiveSubscriptions(Long userId) {
        return getSubscriptionRepository.findByUserIdAndIsSubscription(userId, true);
    }

    // ✅ 전체 구독 목록 조회
    public List<GetSubscription> getAllSubscriptions() {
        return (List<GetSubscription>) getSubscriptionRepository.findAll();
    }

    // ✅ 특정 구독 정보 조회
    public GetSubscription getSubscription(Long userId, Long bookId) {
        return getSubscriptionRepository.findByUserIdAndBookId(userId, bookId);
    }

    // 📋 구독 성공 시 GetSubscription ReadModel 업데이트
    public void updateOnSubscriptionApplied(SubscriptionApplied subscriptionApplied) {
        GetSubscription getSubscription = new GetSubscription();

        getSubscription.setUserId(subscriptionApplied.getUserId());
        getSubscription.setBookId(subscriptionApplied.getBookId());
        getSubscription.setIsSubscription(subscriptionApplied.getIsSubscription());
        getSubscription.setRentalstart(subscriptionApplied.getStartSubscription());
        getSubscription.setRentalend(subscriptionApplied.getEndSubscription());
        getSubscription.setWebURL(subscriptionApplied.getPdfPath());

        getSubscriptionRepository.save(getSubscription);

        System.out.println(">>> [GetSubscription ReadModel] 구독 정보 업데이트: userId=" +
                subscriptionApplied.getUserId() + ", bookId=" + subscriptionApplied.getBookId());
    }

    // 📋 구독 실패 시 GetSubscription ReadModel 업데이트
    public void updateOnSubscriptionFailed(SubscriptionFailed subscriptionFailed) {
        GetSubscription getSubscription = new GetSubscription();

        getSubscription.setUserId(subscriptionFailed.getUserId());
        getSubscription.setBookId(subscriptionFailed.getBookId());
        getSubscription.setIsSubscription(false);

        getSubscriptionRepository.save(getSubscription);

        System.out.println(">>> [GetSubscription ReadModel] 구독 실패 기록: userId=" +
                subscriptionFailed.getUserId() + ", bookId=" + subscriptionFailed.getBookId());
    }
}