package com.skillseed.notification;

/**
 * Sender for transactional email. Backed by Resend in production; the
 * logging fallback is used when {@code notification.resend.api-key} is
 * absent (dev/test).
 */
public interface EmailSender {

    void send(String to, String subject, String htmlBody, String textBody);
}