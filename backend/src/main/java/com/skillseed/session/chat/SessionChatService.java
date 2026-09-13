package com.skillseed.session.chat;

import com.skillseed.booking.domain.Booking;
import com.skillseed.booking.repository.BookingRepository;
import com.skillseed.shared.exception.DomainException;
import com.skillseed.user.domain.User;
import com.skillseed.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Business logic for in-session chat (T-M412).
 *
 * <p>Owns participant authorisation and sender-name resolution so that
 * {@link SessionChatController} stays a thin STOMP dispatcher with no
 * repository access.
 *
 * <p>Authorisation preserves the original "silent drop" behaviour: a
 * non-participant sender causes the broker to receive a {@code null}
 * message rather than an exception frame. We keep that semantics to
 * avoid leaking booking roster to would-be eavesdroppers.
 */
@Service
public class SessionChatService {

    private static final Logger log = LoggerFactory.getLogger(SessionChatService.class);

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public SessionChatService(BookingRepository bookingRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    /**
     * Resolves the chat context for an incoming STOMP frame.
     *
     * @return populated context when {@code senderId} is teacher or learner
     *         on {@code bookingId}; empty otherwise. Throws
     *         {@link DomainException#notFound} if the booking does not
     *         exist (caller surfaces this as an ERROR frame).
     */
    @Transactional(readOnly = true)
    public Optional<ChatContext> resolveChatContext(UUID bookingId, UUID senderId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> DomainException.notFound("BOOKING_NOT_FOUND",
                        "Booking " + bookingId + " not found"));

        UUID teacherId = booking.getTeacher().getId();
        UUID learnerId = booking.getLearner().getId();
        if (!senderId.equals(teacherId) && !senderId.equals(learnerId)) {
            log.warn("Non-participant {} attempted to chat on booking {}", senderId, bookingId);
            return Optional.empty();
        }

        String senderName = userRepository.findById(senderId)
                .map(User::getFullName)
                .orElse("Anonymous");

        return Optional.of(new ChatContext(senderName));
    }

    /** Result of resolving the chat context. */
    public record ChatContext(String senderName) {
    }
}
