package com.doctorq.appointmentservice.appointment.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Async
    public void sendMail(
            String destinationEmail,
            String templateName,
            Map<String, Object> variables,
            String mailTitle
    ) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(
                mimeMessage, MimeMessageHelper.MULTIPART_MODE_RELATED, UTF_8.name()
        );

        messageHelper.setFrom(fromMail);

        Context context = new Context();
        context.setVariables(variables);
        messageHelper.setSubject(mailTitle);

        try {
            String htmlTemplate = templateEngine.process(templateName, context);
            messageHelper.setText(htmlTemplate, true);
            messageHelper.setTo(destinationEmail);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new MessagingException("Error sending mail");
        }
    }
}
