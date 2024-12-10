package kr.just.tj.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final Map<String, String> verificationCodes = new HashMap<>();  // 이메일-인증번호 저장

   
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // 인증번호 생성 메서드
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);  // 6자리 랜덤 숫자 생성
        return String.valueOf(code);
    }

    // 인증번호 전송 및 저장
    public void sendVerificationCode(String email) {
        String code = generateVerificationCode();  // 인증번호 생성
        verificationCodes.put(email, code);  // 이메일-인증번호 매핑 저장

        // 이메일 발송 설정
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("이메일 인증 코드");
        message.setText("인증번호: " + code);

        mailSender.send(message);  // 이메일 전송
    }

    // 인증번호 검증
    public boolean verifyCode(String email, String code) {
        String storedCode = verificationCodes.get(email);  // 저장된 인증번호 조회
        return storedCode != null && storedCode.equals(code);  // 인증번호 일치 여부 확인
    }
}