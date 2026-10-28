package com.enterprise.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.Map;

/**
 * Standardized error payload returned across all exceptions.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private boolean success = false;
    private int statusCode;
    private String error;
    private String message;
    private String path;
    private Map<String, String> validationErrors;
    private Instant timestamp;

    public ErrorResponse() {
        this.timestamp = Instant.now();
    }

    public ErrorResponse(int statusCode, String error, String message, String path) {
        this.success = false;
        this.statusCode = statusCode;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = Instant.now();
    }

    public ErrorResponse(int statusCode, String error, String message, String path, Map<String, String> validationErrors) {
        this.success = false;
        this.statusCode = statusCode;
        this.error = error;
        this.message = message;
        this.path = path;
        this.validationErrors = validationErrors;
        this.timestamp = Instant.now();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
