package com.skillseed.waitlist.service;

import com.skillseed.notification.EmailSender;
import com.skillseed.notification.EmailTemplateService;
import com.skillseed.shared.security.InMemoryRateLimiter;
import com.skillseed.waitlist.domain.WaitlistEntry;
import com.skillseed.waitlist.dto.JoinWaitlistRequest;
import com.skillseed.waitlist.dto.WaitlistResponse;
import com.skillseed.waitlist.repository.WaitlistRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Locale;

/**
 * Marketing waitlist operations: signup + rate limit + confirmation
 * email. Idempotent — re-submitting the same email returns the same
 * "thanks for joining" copy without creating a duplicate row.
 */
@Service
public class WaitlistService {

    private static final Logger log = LoggerFactory.getLogger(WaitlistService.class);

    /** Rate limit: 5 signups per IP per 10 minutes. */
    static final Duration WINDOW = Duration.ofMinutes(10);
    static final int MAX_ATTEMPTS = 5;

    private final WaitlistRepository waitlistRepository;
    private final InMemoryRateLimiter rateLimiter;
    private final EmailSender emailSender;
    private final String publicBaseUrl;
    private final String fromAddress;
    private final boolean emailEnabled;

    public WaitlistService(
            WaitlistRepository waitlistRepository,
            InMemoryRateLimiter rateLimiter,
            EmailSender emailSender,
            EmailTemplateService emailTemplateService,
            @Value("${app.public-base-url:http://localhost:3000}") String publicBaseUrl,
            @Value("${notification.resend.api-key:}") String resendApiKey,
            @Value("${notification.resend.from:no-reply@skillseed.app}") String fromAddress) {
        this.waitlistRepository = waitlistRepository;
        this.rateLimiter = rateLimiter;
        this.emailSender = emailSender;
        this.publicBaseUrl = trimTrailingSlash(publicBaseUrl);
        this.fromAddress = fromAddress;
        this.emailEnabled = resendApiKey != null && !resendApiKey.isBlank();
        // Reference kept to make DI graph obvious even when email is disabled.
        if (emailTemplateService == null) {
            throw new IllegalStateException("EmailTemplateService is required");
        }
    }

    @Transactional
    public WaitlistResponse join(JoinWaitlistRequest req, HttpServletRequest httpRequest) {
        String clientKey = clientKey(httpRequest);
        rateLimiter.acquireOrThrow(
                "waitlist:" + clientKey,
                MAX_ATTEMPTS,
                WINDOW,
                "RATE_LIMIT_WAITLIST");

        String email = req.email().trim();
        String emailLower = email.toLowerCase(Locale.ROOT);
        WaitlistEntry entry = waitlistRepository.findByEmailLower(emailLower)
            .orElseGet(() -> createEntry(req, email, emailLower, httpRequest));

        // Always send a confirmation email so the user knows they're in —
        // even on a duplicate signup (covers the "did my first submission
        // go through?" case).
        sendConfirmationEmail(entry);

        long total = waitlistRepository.count();
        int position = (int) Math.max(1, total);
        log.info("Waitlist signup email={} source={} position={}",
                emailLower, entry.getSource(), position);
        return new WaitlistResponse(
                "You're on the list. We'll email you when SkillSeed launches.",
                position);
    }

    private WaitlistEntry createEntry(JoinWaitlistRequest req, String email,
                                      String emailLower, HttpServletRequest httpRequest) {
        String referrer = req.referrer() != null && !req.referrer().isBlank()
                ? req.referrer()
                : httpRequest.getHeader("Referer");
        String userAgent = httpRequest.getHeader("User-Agent");
        String ip = clientKey(httpRequest);
        WaitlistEntry entry = new WaitlistEntry(
                email,
                req.source(),
                referrer,
                userAgent,
                ip);
        return waitlistRepository.save(entry);
    }

    private void sendConfirmationEmail(WaitlistEntry entry) {
        if (!emailEnabled) {
            log.info("Skipping waitlist confirmation email — Resend API key not set");
            return;
        }
        String subject = "You're on the SkillSeed waitlist 🌱";
        String html = """
                <div style="font-family:Inter,system-ui,sans-serif;max-width:560px;margin:0 auto;padding:24px">
                  <h1 style="color:#10B981;font-size:28px;margin:0 0 16px">Welcome to the waitlist 🌱</h1>
                  <p style="font-size:16px;line-height:24px;color:#374151">
                    Thanks for joining! We'll email you the moment SkillSeed opens
                    the doors — usually 2 weeks before launch.
                  </p>
                  <p style="font-size:14px;line-height:20px;color:#6B7280;margin-top:24px">
                    In the meantime, tell a friend who teaches something you want to learn.
                  </p>
                  <p style="font-size:12px;color:#9CA3AF;margin-top:32px">
                    — The SkillSeed team
                  </p>
                </div>
                """;
        String text = "Thanks for joining the SkillSeed waitlist! "
                + "We'll email you the moment we launch.";
        emailSender.send(entry.getEmail(), subject, html, text);
    }

    private static String clientKey(HttpServletRequest req) {
        String forwarded = req.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            int comma = forwarded.indexOf(',');
            return comma > 0 ? forwarded.substring(0, comma).trim() : forwarded.trim();
        }
        return req.getRemoteAddr();
    }

    private static String trimTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
