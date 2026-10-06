package ai.fin.shared.exception.types;

import ai.fin.shared.exception.ErrorCode;

public class AuthException extends BusinessException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, String userMessage) {
        super(errorCode, userMessage);
    }
}
