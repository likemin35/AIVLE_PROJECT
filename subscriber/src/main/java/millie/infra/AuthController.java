package millie.infra;

import millie.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    UserRepository userRepository;

    // 로그인 API
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        try {
            // 이메일로 사용자 찾기
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

            if (userOpt.isEmpty()) {
                return new LoginResponse(false, "사용자를 찾을 수 없습니다.", null);
            }

            User user = userOpt.get();

            // 비밀번호 검증 (실제로는 해싱해서 비교해야 함)
            if (!user.getPassword().equals(request.getPassword())) {
                return new LoginResponse(false, "비밀번호가 올바르지 않습니다.", null);
            }

            // 로그인 성공
            UserInfo userInfo = new UserInfo(
                    user.getId(),
                    user.getEmail(),
                    user.getUserName(),
                    user.getIsKt(),
                    user.getIsPurchase());

            return new LoginResponse(true, "로그인 성공", userInfo);

        } catch (Exception e) {
            return new LoginResponse(false, "로그인 중 오류가 발생했습니다: " + e.getMessage(), null);
        }
    }

    // 로그인 요청 DTO
    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    // 로그인 응답 DTO
    public static class LoginResponse {
        private boolean success;
        private String message;
        private UserInfo user;

        public LoginResponse(boolean success, String message, UserInfo user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public UserInfo getUser() {
            return user;
        }
    }

    // 사용자 정보 DTO
    public static class UserInfo {
        private Long id;
        private String email;
        private String userName;
        private Boolean isKt;
        private Boolean isPurchase;

        public UserInfo(Long id, String email, String userName, Boolean isKt, Boolean isPurchase) {
            this.id = id;
            this.email = email;
            this.userName = userName;
            this.isKt = isKt;
            this.isPurchase = isPurchase;
        }

        public Long getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }

        public String getUserName() {
            return userName;
        }

        public Boolean getIsKt() {
            return isKt;
        }

        public Boolean getIsPurchase() {
            return isPurchase;
        }
    }
}