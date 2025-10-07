package com.doctorq.userservice.mail;

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

import java.util.HashMap;
import java.util.Map;

import static com.doctorq.userservice.mail.EmailTemplate.RESET_PASSWORD_CODE;
import static com.doctorq.userservice.mail.EmailTemplate.VERIFICATION_CODE;
import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.from_mail}")
    private String fromMail;

    @Async
    public void sendVerificationCode(
            String destinationEmail,
            String verificationCode
    ) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(
                mimeMessage, MimeMessageHelper.MULTIPART_MODE_RELATED, UTF_8.name()
        );

        messageHelper.setFrom(fromMail);
        final String templateName = VERIFICATION_CODE.getTemplate();

        Map<String, Object> variables = new HashMap<>();

        variables.put("verificationCode", verificationCode);

        Context context = new Context();
        context.setVariables(variables);

        messageHelper.setSubject(VERIFICATION_CODE.getSubject());

        try {
            String htmlTemplate = templateEngine.process(templateName, context);
            messageHelper.setText(htmlTemplate, true);
            messageHelper.setTo(destinationEmail);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException exception) {
            throw new MessagingException("Error sending email");
        }

    }

    @Async
    public void sendPasswordResetCode(
            String destinationEmail,
            String resetPassCode
    ) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(
                mimeMessage, MimeMessageHelper.MULTIPART_MODE_RELATED, UTF_8.name()
        );

        messageHelper.setFrom(fromMail);
        final String templateName = RESET_PASSWORD_CODE.getTemplate();

        Map<String, Object> variables = new HashMap<>();

        variables.put("resetPassCode", resetPassCode);

        Context context = new Context();
        context.setVariables(variables);

        messageHelper.setSubject(RESET_PASSWORD_CODE.getSubject());

        try {
            String htmlTemplate = templateEngine.process(templateName, context);
            messageHelper.setText(htmlTemplate, true);
            messageHelper.setTo(destinationEmail);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException exception) {
            throw new MessagingException("Error sending email");
        }

    }
}
