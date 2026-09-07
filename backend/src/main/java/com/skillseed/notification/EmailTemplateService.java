package com.skillseed.notification;

import com.skillseed.user.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Centralised HTML + plain-text email templates. Keeps copy and layout
 * in a single place so the AuthService stays focused on the flow logic.
 *
 * <p>All templates share the same {@code publicBaseUrl} so every CTA
 * link can be rendered as an absolute URL.
 */
@Service
public class EmailTemplateService {

    private static final String SKILL_SEED_BRAND = "SkillSeed";

    private final EmailSender emailSender;
    private final String publicBaseUrl;

    public EmailTemplateService(
            EmailSender emailSender,
            @Value("${app.public-base-url:http://localhost:3000}") String publicBaseUrl) {
        this.emailSender = emailSender;
        this.publicBaseUrl = trimTrailingSlash(publicBaseUrl);
    }

    /**
     * Welcome email sent after a user verifies their email address.
     * Encourages them to finish onboarding to claim the 30 free seeds.
     */
    public void sendWelcomeEmail(User user) {
        String firstName = firstNameOf(user.getFullName());
        String subject = "Welcome to " + SKILL_SEED_BRAND + " — let's set up your profile";
        String onboardingUrl = publicBaseUrl + "/onboarding";
        String html = """
                <div style="font-family:system-ui,Segoe UI,Roboto,sans-serif;line-height:1.6;color:#0f172a">
                  <h1 style="margin:0 0 8px;font-size:22px">Welcome aboard, %s!</h1>
                  <p>Your email is verified. You now have a SkillSeed account.</p>
                  <p>Finish the 7-step onboarding to claim your <strong>30 free Starter Seeds</strong> \
                and start trading skills.</p>
                  <p style="margin:24px 0">
                    <a href="%s" style="background:#0f172a;color:#fff;padding:10px 18px;border-radius:8px;text-decoration:none">
                      Start onboarding
                    </a>
                  </p>
                  <p style="color:#64748b;font-size:13px">
                    If the button doesn't work, paste this link:<br>%s
                  </p>
                </div>
                """.formatted(escapeHtml(firstName), onboardingUrl, onboardingUrl);
        String text = "Welcome aboard, " + firstName + "!\n\n"
                + "Your email is verified. Finish the 7-step onboarding to claim your "
                + "30 free Starter Seeds:\n" + onboardingUrl + "\n";
        safeSend(user.getEmail(), subject, html, text);
    }

    /**
     * Email verification link sent at registration. Token TTL is 24h
     * (see {@code TokenGenerator.defaultTtl()}).
     */
    public void sendVerificationEmail(User user, String token) {
        String link = publicBaseUrl + "/verify-email/" + token;
        String subject = "Verify your " + SKILL_SEED_BRAND + " email";
        String html = """
                <div style="font-family:system-ui,Segoe UI,Roboto,sans-serif;line-height:1.6;color:#0f172a">
                  <h1 style="margin:0 0 8px;font-size:22px">Confirm your email</h1>
                  <p>Welcome to %s! Click the button below to confirm your email address. \
                The link expires in %d hours.</p>
                  <p style="margin:24px 0">
                    <a href="%s" style="background:#0f172a;color:#fff;padding:10px 18px;border-radius:8px;text-decoration:none">
                      Verify email
                    </a>
                  </p>
                  <p style="color:#64748b;font-size:13px">
                    If the button doesn't work, paste this link:<br>%s
                  </p>
                  <p style="color:#64748b;font-size:13px">
                    If you didn't create this account, you can safely ignore this email.
                  </p>
                </div>
                """.formatted(SKILL_SEED_BRAND, Duration.ofHours(24).toHours(), link, link);
        String text = "Welcome to " + SKILL_SEED_BRAND + "!\n\n"
                + "Confirm your email by opening:\n" + link + "\n\n"
                + "This link expires in 24 hours.\n";
        safeSend(user.getEmail(), subject, html, text);
    }

    /**
     * Password reset link. Token TTL is 1 hour (enforced by
     * {@code AuthService.forgotPassword}).
     */
    public void sendPasswordResetEmail(User user, String token) {
        String link = publicBaseUrl + "/reset-password?token=" + token;
        String subject = "Reset your " + SKILL_SEED_BRAND + " password";
        String html = """
                <div style="font-family:system-ui,Segoe UI,Roboto,sans-serif;line-height:1.6;color:#0f172a">
                  <h1 style="margin:0 0 8px;font-size:22px">Reset your password</h1>
                  <p>We received a request to reset the password for your account. \
                Click the button below to choose a new password (expires in 1 hour).</p>
                  <p style="margin:24px 0">
                    <a href="%s" style="background:#0f172a;color:#fff;padding:10px 18px;border-radius:8px;text-decoration:none">
                      Reset password
                    </a>
                  </p>
                  <p style="color:#64748b;font-size:13px">
                    If the button doesn't work, paste this link:<br>%s
                  </p>
                  <p style="color:#64748b;font-size:13px">
                    If you did not request this, you can safely ignore this email.
                  </p>
                </div>
                """.formatted(link, link);
        String text = "We received a request to reset your " + SKILL_SEED_BRAND + " password.\n\n"
                + "Open the link below to choose a new password (expires in 1 hour):\n"
                + link + "\n\n"
                + "If you did not request this, you can safely ignore this email.\n";
        safeSend(user.getEmail(), subject, html, text);
    }

    private void safeSend(String to, String subject, String html, String text) {
        try {
            emailSender.send(to, subject, html, text);
        } catch (Exception ex) {
            // Never fail the caller because the email service hiccuped; the
            // auth flow already returns success before the email is dispatched.
            org.slf4j.LoggerFactory.getLogger(EmailTemplateService.class)
                    .warn("email send failed to={} subject={} err={}", to, subject, ex.getMessage());
        }
    }

    private static String firstNameOf(String fullName) {
        if (fullName == null || fullName.isBlank()) return "there";
        int sp = fullName.indexOf(' ');
        return sp < 0 ? fullName : fullName.substring(0, sp);
    }

    private static String trimTrailingSlash(String url) {
        if (url == null) return "";
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private static String escapeHtml(String raw) {
        if (raw == null) return "";
        return raw.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
