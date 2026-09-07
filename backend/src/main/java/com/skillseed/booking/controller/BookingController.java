package com.skillseed.booking.controller;

import com.skillseed.booking.dto.BookingPageResponse;
import com.skillseed.booking.dto.BookingResponse;
import com.skillseed.booking.dto.CancelBookingRequest;
import com.skillseed.booking.dto.CreateBookingRequest;
import com.skillseed.booking.dto.DeclineBookingRequest;
import com.skillseed.booking.service.BookingService;
import com.skillseed.shared.domain.BookingStatus;
import com.skillseed.shared.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Booking lifecycle endpoints (T-M101..T-M105).
 *
 * <p>All paths under {@code /api/v1/bookings} require a JWT access token
 * (handled by the global SecurityConfig).
 */
@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "Bookings", description = "Booking lifecycle (request → confirm → start → complete)")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @Operation(summary = "Create a booking (escrow seed hold)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Booking created; teacher notified"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Slot unavailable or insufficient balance")
    })
    public ResponseEntity<BookingResponse> create(
            @Parameter(description = "Optional idempotency key (MVP: accepted, not enforced)")
            @RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody CreateBookingRequest req) {
        BookingResponse response = bookingService.create(CurrentUser.requireId(), req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    @Operation(summary = "List the current user's bookings, optionally filtered by role + status")
    public ResponseEntity<BookingPageResponse> listMine(
            @RequestParam(name = "role") String role,
            @RequestParam(name = "status", required = false) List<BookingStatus> status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        return ResponseEntity.ok(bookingService.listForUser(
                CurrentUser.requireId(), role, status, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a booking the caller participates in")
    public ResponseEntity<BookingResponse> getById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(bookingService.getById(id, CurrentUser.requireId()));
    }

    @PostMapping("/{id}/accept")
    @Operation(summary = "Teacher accepts a pending booking (T-M102)")
    public ResponseEntity<BookingResponse> accept(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(bookingService.accept(id, CurrentUser.requireId()));
    }

    @PostMapping("/{id}/decline")
    @Operation(summary = "Teacher declines a pending booking (T-M102, full refund)")
    public ResponseEntity<BookingResponse> decline(@PathVariable("id") UUID id,
                                                   @Valid @RequestBody(required = false)
                                                   DeclineBookingRequest req) {
        String reason = req == null ? null : req.reason();
        return ResponseEntity.ok(bookingService.decline(id, CurrentUser.requireId(), reason));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Either party cancels (T-M103, refund per policy)")
    public ResponseEntity<BookingResponse> cancel(@PathVariable("id") UUID id,
                                                  @Valid @RequestBody CancelBookingRequest req) {
        return ResponseEntity.ok(bookingService.cancel(id, CurrentUser.requireId(), req));
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "Mark a confirmed booking as IN_PROGRESS (T-M104)")
    public ResponseEntity<BookingResponse> start(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(bookingService.start(id, CurrentUser.requireId()));
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Mark the session as completed; releases escrow to teacher (T-M104)")
    public ResponseEntity<BookingResponse> complete(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(bookingService.complete(id, CurrentUser.requireId()));
    }
}