package com.skillseed.session.chat;

import java.time.Instant;
import java.util.UUID;

/**
 * Single chat message payload broadcast over STOMP. Kept
 * intentionally tiny — the front-end can format timestamps.
 */
public class SessionChatMessage {

    private UUID bookingId;
    private UUID senderId;
    private String senderName;
    private String body;
    private Instant sentAt;

    public SessionChatMessage() {
    }

    public SessionChatMessage(UUID bookingId, UUID senderId, String senderName,
                              String body, Instant sentAt) {
        this.bookingId = bookingId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.body = body;
        this.sentAt = sentAt;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }
}
