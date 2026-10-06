package ai.fin.shared.exception.types;

import ai.fin.shared.exception.ErrorCode;

public class UserException extends BusinessException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserException(ErrorCode errorCode, String userMessage) {
        super(errorCode, userMessage);
    }
}
