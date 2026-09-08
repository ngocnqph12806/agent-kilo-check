package com.skillseed.notification;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Email sender wiring. When {@code notification.resend.api-key} is set the
 * real Resend SDK is used; otherwise a logging fallback is registered so
 * the rest of the app boots without external dependencies.
 *
 * <p>Set {@code notification.email.fail-on-missing-key=true} in
 * production profiles to crash boot instead of silently logging emails —
 * the silent fallback would mask signup / verification / forgot-password
 * flows in staging.
 */
@Configuration
public class EmailSenderConfig {

    @Bean
    @ConditionalOnProperty(prefix = "notification.resend", name = "api-key")
    public EmailSender resendEmailSender(
            @Value("${notification.resend.api-key}") String apiKey,
            @Value("${notification.resend.from:no-reply@skillseed.app}") String from) {
        if (apiKey == null || apiKey.isBlank()) {
            // Defensive: an explicitly empty value would still satisfy
            // @ConditionalOnProperty because Spring binds "" as a present
            // value. Reject blank keys here so the Resend bean creation
            // never silently mis-configures the SDK.
            throw new IllegalStateException(
                    "notification.resend.api-key is set but blank — refusing to send email");
        }
        Resend resend = new Resend(apiKey);
        return new ResendEmailSender(resend, from);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean(EmailSender.class)
    public EmailSender loggingEmailSender(
            @Value("${notification.email.fail-on-missing-key:false}") boolean failOnMissingKey) {
        if (failOnMissingKey) {
            throw new IllegalStateException(
                    "notification.resend.api-key is required but missing; refusing to fall back "
                            + "to a logging stub. Set notification.email.fail-on-missing-key=false "
                            + "explicitly if you really want the dev fallback.");
        }
        return new LoggingEmailSender();
    }

    /** Resend SDK implementation. */
    public static final class ResendEmailSender implements EmailSender {

        private static final Logger log = LoggerFactory.getLogger(ResendEmailSender.class);

        private final Resend resend;
        private final String from;

        public ResendEmailSender(Resend resend, String from) {
            this.resend = resend;
            this.from = from;
        }

        @Override
        public void send(String to, String subject, String htmlBody, String textBody) {
            try {
                CreateEmailOptions options = CreateEmailOptions.builder()
                        .from(from)
                        .to(to)
                        .subject(subject)
                        .html(htmlBody)
                        .text(textBody)
                        .build();
                CreateEmailResponse response = resend.emails().send(options);
                log.info("Resend email queued id={} to={}", response.getId(), to);
            } catch (Exception ex) {
                log.error("Failed to send email via Resend to={} subject={}", to, subject, ex);
            }
        }
    }

    /** Dev/test fallback: just logs the message body. */
    public static final class LoggingEmailSender implements EmailSender {

        private static final Logger log = LoggerFactory.getLogger(LoggingEmailSender.class);

        @Override
        public void send(String to, String subject, String htmlBody, String textBody) {
            log.info("[email:dev] to={} subject={}\n{}", to, subject, textBody);
        }
    }
}