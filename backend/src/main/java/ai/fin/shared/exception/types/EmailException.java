package ai.fin.shared.exception.types;

import ai.fin.shared.exception.ErrorCode;

public class EmailException extends BusinessException {

    public EmailException(ErrorCode errorCode) {
        super(errorCode);
    }

    public EmailException(ErrorCode errorCode, String userMessage) {
        super(errorCode, userMessage);
    }
}
