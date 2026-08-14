package com.kien.auth.mail.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Async
    public void guiMail(String to,
                            String subject,
                            String html) {

        try {

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);

            helper.setSubject(subject);

            helper.setText(html, true);

            mailSender.send(message);

        } catch (Exception e) {

            throw new RuntimeException("Gửi mail thất bại", e);

        }

    }
    public void sendOtp(String email, String otp) {

        String subject = "Mã OTP đặt lại mật khẩu";

        String html = """
            <h2>Mã OTP của bạn là: %s</h2>
            <p>OTP có hiệu lực trong 5 phút.</p>
            """.formatted(otp);

        guiMail(email, subject, html);
    }

}
