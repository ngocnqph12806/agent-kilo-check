package com.skillseed.skill.exception;

import com.skillseed.shared.exception.DomainException;
import org.springframework.http.HttpStatus;

/**
 * Skill module domain exception.
 *
 * <p>Retained as a thin wrapper around {@link DomainException} for
 * backwards compatibility and to keep {@code throws} clauses expressive
 * (T-M411). Prefer throwing {@link DomainException} directly in new code.
 *
 * @deprecated since Sprint 6 — extend {@link DomainException} directly.
 */
@Deprecated
public class SkillException extends DomainException {

    public SkillException(String code, String message, int httpStatus) {
        super(code, message, httpStatus);
    }

    public SkillException(String code, String message, HttpStatus status) {
        super(code, message, status);
    }

    public static SkillException notFound(String code, String message) {
        return new SkillException(code, message, HttpStatus.NOT_FOUND);
    }

    public static SkillException conflict(String code, String message) {
        return new SkillException(code, message, HttpStatus.CONFLICT);
    }

    public static SkillException badRequest(String code, String message) {
        return new SkillException(code, message, HttpStatus.BAD_REQUEST);
    }
}
