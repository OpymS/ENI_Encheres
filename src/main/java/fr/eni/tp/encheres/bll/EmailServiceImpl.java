package fr.eni.tp.encheres.bll;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;

@Service
public class EmailServiceImpl implements EmailService {
	
    @Value("${mail.smtp.host}")
    private String host;

    @Value("${mail.smtp.port}")
    private String port;

    @Value("${app.baseUrl}")
    private String baseUrl;

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        String resetLink = buildResetLink(baseUrl, token);
        String subject = buildPasswordResetSubject();
        String content = buildPasswordResetEmail(resetLink);

        sendEmail(to, subject, content);
    }

    private void sendEmail(String to, String subject, String content) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("no-reply@example.com")); // L'adresse email d'envoi
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);
            System.out.println("Email envoyé avec succès!");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildPasswordResetEmail(String resetLink) {
        return "Hello,\n\n" +
               "We received a request to reset your password. Please click the link below to reset your password:\n" +
               resetLink + "\n\n" +
               "If you did not request a password reset, please ignore this email.\n\n" +
               "Best regards,\n" +
               "YourApp Team";
    }

    private String buildPasswordResetSubject() {
        return "Password Reset Request";
    }

    private String buildResetLink(String baseUrl, String token) {
        return baseUrl + "/reset-password?token=" + token;
    }

}
