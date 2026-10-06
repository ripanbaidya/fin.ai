package ai.fin.service.impl;

import ai.fin.service.EmailService;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendHtmlEmail(String toEmail, String subject, String templateName, Map<String, Object> templateModel) {
        try {
            Context context = new Context();
            if (templateModel != null && !templateModel.isEmpty()) {
                context.setVariables(templateModel);
            }

            String body = templateEngine.process(templateName, context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);
            log.info("HTML email dispatched successfully to: '{}', template: '{}', subject: '{}'", toEmail, templateName, subject);
        } catch (MessagingException | MailException e) {
            log.error("Failed to send HTML email to: '{}' with template: '{}'", toEmail, templateName, e);
            throw new EmailException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    @Override
    public void sendSimpleEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Simple email dispatched successfully to: '{}', subject: '{}'", toEmail, subject);
        } catch (MailException e) {
            log.error("Failed to send simple email to: '{}'", toEmail, e);
            throw new EmailException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }
}
