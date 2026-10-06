package ai.fin.shared.exception;

import ai.fin.shared.api.ApiErrorDetail;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class BaseException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String userMessage;
    private final List<ApiErrorDetail> errors;

    protected BaseException(ErrorCode errorCode) {
        this(errorCode, errorCode.getDefaultMessage(), List.of());
    }

    protected BaseException(ErrorCode errorCode, String userMessage) {
        this(errorCode, userMessage, List.of());
    }

    protected BaseException(ErrorCode errorCode, String userMessage, List<ApiErrorDetail> errors) {
        super(userMessage);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
        this.errors = errors == null ? List.of() : List.copyOf(errors);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public List<ApiErrorDetail> getErrors() {
        return errors;
    }
}