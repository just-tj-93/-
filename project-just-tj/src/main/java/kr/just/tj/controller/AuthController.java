package kr.just.tj.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.just.tj.service.EmailService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final EmailService emailService;

    @Autowired
    public AuthController(EmailService emailService) {
        this.emailService = emailService;
    }

    // 이메일 인증번호 전송 API
    @PostMapping("/send-verification-code")
    public ResponseEntity<String> sendVerificationCode(@RequestBody Map<String, String> request) {
    	//이메일 주소 받기
        String email = request.get("email");	

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("이메일을 입력하세요.");
        }
        // 인증번호 전송
        emailService.sendVerificationCode(email);  
        return ResponseEntity.ok("인증번호가 이메일로 발송되었습니다.");
    }

    // 인증번호 검증 API
    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        if (emailService.verifyCode(email, code)) {
            return ResponseEntity.ok("이메일 인증이 완료되었습니다!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증번호가 일치하지 않습니다.");
        }
    }
}