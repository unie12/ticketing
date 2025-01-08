package com.example.ticketing.service.user;

import com.example.ticketing.model.user.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendVerificationEmail(User user, String token) {
        String subject = "이메일 인증을 완료해주세요";
        String verificationLink = "http://localhost:8080/api/auth/verify?token=" + token;

        String content = String.format(
                "안녕하세요 %s님,<br>"
                + "아래 링크를 클릭하여 이메일 인증을 완료해주세요:<br>"
                + "<a href='%s'>이메일 인증하기</a><br>"
                + "이 링크는 24시간 동안 유효합니다.",
                user.getUsername(),
                verificationLink
        );

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
}
