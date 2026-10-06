package ai.fin.service;

import java.util.Map;

public interface EmailService {

    /**
     * Sends an HTML email processed by a template engine with dynamic context variables.
     *
     * @param toEmail       recipient email address
     * @param subject       subject line of the email
     * @param templateName  name of the Thymeleaf template (e.g. "email-verification", "password-reset-otp")
     * @param templateModel key-value pairs to populate the template context
     */
    void sendHtmlEmail(String toEmail, String subject, String templateName, Map<String, Object> templateModel);

    /**
     * Sends a plain text email.
     *
     * @param toEmail recipient email address
     * @param subject subject line of the email
     * @param body    plain text content of the email
     */
    void sendSimpleEmail(String toEmail, String subject, String body);

}
