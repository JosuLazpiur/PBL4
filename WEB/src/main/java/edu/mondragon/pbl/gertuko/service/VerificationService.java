package edu.mondragon.pbl.gertuko.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class VerificationService {

    @Autowired
    private JavaMailSender mailSender;

    private Map<String, String> verificationCodes = new HashMap<>();

    public void sendVerificationCode(String email) {
        String code = generateVerificationCode();
        verificationCodes.put(email, code);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Código de Verificación");
        message.setText("Tu código de verificación es: " + code);
        mailSender.send(message);
    }

    public boolean verifyCode(String email, String code) {
        return code.equals(verificationCodes.get(email));
    }

    public void removeVerificationCode(String email) {
        verificationCodes.remove(email);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
