package com.aeromaint.exception;

/**
 * The request is valid but breaks a business rule
 * (invalid status transition, duplicate username, zone still has equipment...).
 * Returned as 409 Conflict.
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
