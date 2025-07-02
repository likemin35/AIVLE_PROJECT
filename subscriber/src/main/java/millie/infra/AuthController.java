package millie.infra;

import millie.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import millie.SubscriberApplication;
import millie.config.kafka.KafkaProcessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.MimeTypeUtils;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    UserRepository userRepository;

    // 회원가입 API 추가
    @PostMapping("/signup")
    public SignupResponse signup(@RequestBody SignupRequest request) {
        try {
            // 이메일 중복 검사
            Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser.isPresent()) {
                return new SignupResponse(false, "이미 존재하는 이메일입니다.", null);
            }

            // 사용자 생성
            User user = new User();
            user.setEmail(request.getEmail());
            user.setUserName(request.getUserName());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setPassword(request.getPassword());
            user.setIsPurchase(false);
            user.setIsKt(request.getIsKt());

            User savedUser = userRepository.save(user);
            savedUser.setUserId(new UserId(savedUser.getId()));
            userRepository.save(savedUser);

            // UserRegistered 이벤트 발행
            UserRegistered userRegistered = new UserRegistered(savedUser);
            userRegistered.publishAfterCommit();

            // RegisterPointGained 이벤트도 발행
            int basePoint = 1000;
            if (Boolean.TRUE.equals(savedUser.getIsKt())) {
                basePoint += 5000;
            }

            Map<String, Object> pointEvent = Map.of(
                    "eventType", "RegisterPointGained",
                    "userId", savedUser.getId(),
                    "point", String.valueOf(basePoint),
                    "userName", savedUser.getUserName(),
                    "email", savedUser.getEmail(),
                    "phoneNumber", savedUser.getPhoneNumber(),
                    "password", savedUser.getPassword(),
                    "isKt", savedUser.getIsKt());

            ObjectMapper outMapper = new ObjectMapper();
            String payload = outMapper.writeValueAsString(pointEvent);

            KafkaProcessor processor = SubscriberApplication.applicationContext.getBean(KafkaProcessor.class);
            processor.outboundTopic().send(
                    MessageBuilder.withPayload(payload)
                            .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                            .build());

            UserInfo userInfo = new UserInfo(
                    savedUser.getId(),
                    savedUser.getEmail(),
                    savedUser.getUserName(),
                    savedUser.getIsKt(),
                    savedUser.getIsPurchase());

            return new SignupResponse(true, "회원가입이 완료되었습니다.", userInfo);

        } catch (Exception e) {
            return new SignupResponse(false, "회원가입 중 오류가 발생했습니다: " + e.getMessage(), null);
        }
    }

    // 로그인 API
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

            if (userOpt.isEmpty()) {
                return new LoginResponse(false, "사용자를 찾을 수 없습니다.", null);
            }

            User user = userOpt.get();

            if (!user.getPassword().equals(request.getPassword())) {
                return new LoginResponse(false, "비밀번호가 올바르지 않습니다.", null);
            }

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

    // 회원가입 요청 DTO
    public static class SignupRequest {
        private String email;
        private String userName;
        private String phoneNumber;
        private String password;
        private Boolean isKt;

        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public Boolean getIsKt() { return isKt; }
        public void setIsKt(Boolean isKt) { this.isKt = isKt; }
    }

    // 회원가입 응답 DTO
    public static class SignupResponse {
        private boolean success;
        private String message;
        private UserInfo user;

        public SignupResponse(boolean success, String message, UserInfo user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public UserInfo getUser() { return user; }
    }

    // 로그인 요청 DTO
    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
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

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public UserInfo getUser() { return user; }
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

        public Long getId() { return id; }
        public String getEmail() { return email; }
        public String getUserName() { return userName; }
        public Boolean getIsKt() { return isKt; }
        public Boolean getIsPurchase() { return isPurchase; }
    }
}