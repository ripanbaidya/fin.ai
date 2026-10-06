package ai.fin.shared.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Authentication & Security
    INVALID_AUTH_HEADER(HttpStatus.BAD_REQUEST, "INVALID_AUTH_HEADER", "Invalid authorization header."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "The provided email or password is incorrect."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Authentication is required to access this resource."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "You do not have permission to access this resource."),
    ACCOUNT_DELETED(HttpStatus.GONE, "ACCOUNT_DELETED", "Account deletion has been requested."),

    // JWT
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "The authentication token has expired."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_INVALID", "The authentication token is invalid or malformed."),
    TOKEN_MISSING_CLAIM(HttpStatus.UNAUTHORIZED, "TOKEN_MISSING_CLAIM", "The authentication token is missing a required claim."),
    TOKEN_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_SIGNATURE_INVALID", "Token signature verification failed."),
    TOKEN_UNSUPPORTED(HttpStatus.BAD_REQUEST, "TOKEN_UNSUPPORTED", "The provided token format is not supported."),

    // Refresh Token
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "TOKEN_NOT_FOUND", "The refresh token was not found."),
    TOKEN_REVOKED(HttpStatus.UNAUTHORIZED, "TOKEN_REVOKED", "The refresh token has been revoked and cannot be used."),

    // User & Account
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "The requested user was not found."),
    PHONE_ALREADY_IN_USE(HttpStatus.CONFLICT, "PHONE_ALREADY_IN_USE", "A user with this phone number already exists."),
    EMAIL_ALREADY_IN_USE(HttpStatus.CONFLICT, "EMAIL_ALREADY_IN_USE", "An account with this email address already exists."),
    EMAIL_ALREADY_VERIFIED(HttpStatus.CONFLICT, "EMAIL_ALREADY_VERIFIED", "The email address has already been verified."),

    // OTP Verification
    OTP_INVALID(HttpStatus.BAD_REQUEST, "OTP_INVALID", "The provided OTP is invalid or has already been used."),
    OTP_EXPIRED(HttpStatus.BAD_REQUEST, "OTP_EXPIRED", "The OTP has expired. Please request a new verification code."),
    OTP_MAX_ATTEMPTS_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "OTP_MAX_ATTEMPTS_EXCEEDED", "Too many incorrect attempts. Please request a new OTP."),
    OTP_COOLDOWN_ACTIVE(HttpStatus.TOO_MANY_REQUESTS, "OTP_COOLDOWN_ACTIVE", "Please wait before requesting another code."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL_SEND_FAILED", "Failed to send the verification email. Please try again later."),

    // Category
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND", "The requested category was not found."),
    CATEGORY_ALREADY_EXISTS(HttpStatus.CONFLICT, "CATEGORY_ALREADY_EXISTS", "A category with the same name already exists."),
    CATEGORY_ACCESS_DENIED(HttpStatus.FORBIDDEN, "CATEGORY_ACCESS_DENIED", "You do not have permission to modify this category."),
    INVALID_CATEGORY_TYPE(HttpStatus.BAD_REQUEST, "INVALID_CATEGORY_TYPE", "The provided category type is invalid."),

    // Payment Mode
    PAYMENT_MODE_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_MODE_NOT_FOUND", "The requested payment mode was not found."),
    PAYMENT_MODE_ALREADY_EXISTS(HttpStatus.CONFLICT, "PAYMENT_MODE_ALREADY_EXISTS", "A payment mode with the same name already exists."),
    PAYMENT_MODE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "PAYMENT_MODE_ACCESS_DENIED", "You do not have permission to modify this payment mode."),

    // Transaction
    TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "TRANSACTION_NOT_FOUND", "The requested transaction was not found."),
    TRANSACTION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "TRANSACTION_ACCESS_DENIED", "You do not have permission to modify this transaction."),
    INVALID_TRANSACTION(HttpStatus.BAD_REQUEST, "INVALID_TRANSACTION", "The provided transaction data is invalid or incomplete."),

    // Recurring Transaction
    RECURRING_NOT_FOUND(HttpStatus.NOT_FOUND, "RECURRING_NOT_FOUND", "The requested recurring transaction was not found."),
    RECURRING_ALREADY_INACTIVE(HttpStatus.BAD_REQUEST, "RECURRING_ALREADY_INACTIVE", "The recurring transaction is already inactive."),
    RECURRING_END_DATE_BEFORE_START(HttpStatus.BAD_REQUEST, "RECURRING_END_DATE_BEFORE_START", "The end date must be after the start date."),
    RECURRING_INVALID_FREQUENCY(HttpStatus.BAD_REQUEST, "RECURRING_INVALID_FREQUENCY", "The provided recurring frequency is invalid."),
    RECURRING_ACCESS_DENIED(HttpStatus.FORBIDDEN, "RECURRING_ACCESS_DENIED", "You do not have permission to modify this recurring transaction."),

    // Savings Goal
    GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "GOAL_NOT_FOUND", "The requested savings goal was not found."),
    GOAL_ACCESS_DENIED(HttpStatus.FORBIDDEN, "GOAL_ACCESS_DENIED", "You do not have permission to access this savings goal."),
    GOAL_ALREADY_ACHIEVED(HttpStatus.BAD_REQUEST, "GOAL_ALREADY_ACHIEVED", "This savings goal has already been achieved."),
    GOAL_ALREADY_FAILED(HttpStatus.BAD_REQUEST, "GOAL_ALREADY_FAILED", "This savings goal has already failed because the deadline has passed."),
    GOAL_INVALID_CONTRIBUTION(HttpStatus.BAD_REQUEST, "GOAL_INVALID_CONTRIBUTION", "The contribution amount must be greater than zero."),
    GOAL_DEADLINE_PAST(HttpStatus.BAD_REQUEST, "GOAL_DEADLINE_PAST", "The goal deadline must be set to a future date."),

    // Budget
    BUDGET_NOT_FOUND(HttpStatus.NOT_FOUND, "BUDGET_NOT_FOUND", "The requested budget was not found."),
    BUDGET_ACCESS_DENIED(HttpStatus.FORBIDDEN, "BUDGET_ACCESS_DENIED", "You do not have permission to access this budget."),
    BUDGET_DUPLICATE(HttpStatus.CONFLICT, "BUDGET_DUPLICATE", "A budget already exists for the selected category and month."),
    BUDGET_INVALID_MONTH(HttpStatus.BAD_REQUEST, "BUDGET_INVALID_MONTH", "Invalid month format. Please use yyyy-MM."),
    BUDGET_INVALID_THRESHOLD(HttpStatus.BAD_REQUEST, "BUDGET_INVALID_THRESHOLD", "The alert threshold must be between 1 and 100."),

    // Notification
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION_NOT_FOUND", "The requested notification was not found."),

    // Chat Session
    CHAT_SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT_SESSION_NOT_FOUND", "The requested chat session was not found."),

    // RAG / AI
    RAG_QUERY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "RAG_QUERY_FAILED", "Failed to process the AI query."),
    RAG_NO_CONTEXT(HttpStatus.NOT_FOUND, "RAG_NO_CONTEXT", "No relevant transaction data was found to answer the question."),
    RAG_PROMPT_LOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "RAG_PROMPT_LOAD_FAILED", "Failed to load the AI system configuration."),
    VECTOR_SEARCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "VECTOR_SEARCH_FAILED", "The vector search operation failed."),

    // vector
    VECTOR_DOCUMENT_STORE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "VECTOR_DOCUMENT_STORE_FAILED", "Failed to store the transaction in the vector store."),

    // CSV
    CSV_EXPORT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CSV_EXPORT_FAILED", "Failed to generate the CSV export file."),

    // Key Loading / Security Infrastructure
    KEY_FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "KEY_FILE_NOT_FOUND", "The key file could not be found."),
    KEY_FILE_NOT_READABLE(HttpStatus.BAD_REQUEST, "KEY_FILE_NOT_READABLE", "The key file exists but cannot be read."),
    KEY_FILE_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "KEY_FILE_READ_FAILED", "Failed to read the key file."),
    INVALID_KEY_FORMAT(HttpStatus.BAD_REQUEST, "INVALID_KEY_FORMAT", "The key file format or encoding is invalid."),
    PRIVATE_KEY_LOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PRIVATE_KEY_LOAD_FAILED", "Failed to load the private key."),
    PUBLIC_KEY_LOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PUBLIC_KEY_LOAD_FAILED", "Failed to load the public key."),

    // Subscription
    SUBSCRIPTION_ALREADY_ACTIVE(HttpStatus.CONFLICT, "SUBSCRIPTION_ALREADY_ACTIVE", "You already have an active subscription."),
    SUBSCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "SUBSCRIPTION_NOT_FOUND", "Subscription not found."),
    PAYMENT_ORDER_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT_ORDER_CREATION_FAILED", "Failed to create payment order."),
    PAYMENT_SIGNATURE_INVALID(HttpStatus.BAD_REQUEST, "PAYMENT_SIGNATURE_INVALID", "Payment signature verification failed."),
    SUBSCRIPTION_REQUIRED(HttpStatus.FORBIDDEN, "SUBSCRIPTION_REQUIRED", "An active subscription is required to use AI chat."),

    // General & Request
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "One or more request fields are invalid."),
    MALFORMED_JSON(HttpStatus.BAD_REQUEST, "MALFORMED_JSON", "Malformed JSON request body."),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "MISSING_PARAMETER", "Required request parameter is missing."),
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "TYPE_MISMATCH", "Request parameter type mismatch."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "HTTP method is not supported for this endpoint."),
    NO_HANDLER_FOUND(HttpStatus.NOT_FOUND, "NO_HANDLER_FOUND", "Endpoint does not exist."),
    DATA_INTEGRITY_VIOLATION(HttpStatus.CONFLICT, "DATA_INTEGRITY_VIOLATION", "Data integrity violation."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected internal server error occurred.");

    private final HttpStatus status;
    private final String code;
    private final String defaultMessage;
}