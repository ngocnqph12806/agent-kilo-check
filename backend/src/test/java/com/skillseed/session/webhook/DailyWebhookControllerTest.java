package com.skillseed.session.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillseed.session.client.DailyProperties;
import com.skillseed.session.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class DailyWebhookControllerTest {

    private SessionService sessionService;
    private DailyProperties dailyProperties;
    private ObjectMapper objectMapper;
    private DailyWebhookController controller;

    @BeforeEach
    void setUp() {
        sessionService = mock(SessionService.class);
        dailyProperties = new DailyProperties();
        // enabled = false, webhookSigningKey = "" by default.
        objectMapper = new ObjectMapper();
        controller = new DailyWebhookController(sessionService, dailyProperties, objectMapper);
    }

    @Test
    void rejectsWebhookWhenSigningKeyIsBlank() throws Exception {
        String body = """
                {"type":"meeting.ended","payload":{"room":{"name":"ss-test"}}}
                """;
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setContent(body.getBytes());

        var response = controller.handle(req);

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        verify(sessionService, never()).markMeetingEnded(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void rejectsWebhookWhenSignatureHeaderMissing() throws Exception {
        dailyProperties.setWebhookSigningKey("test-secret");
        String body = """
                {"type":"meeting.ended","payload":{"room":{"name":"ss-test"}}}
                """;
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setContent(body.getBytes());

        var response = controller.handle(req);

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        verify(sessionService, never()).markMeetingEnded(org.mockito.ArgumentMatchers.anyString());
    }
}
