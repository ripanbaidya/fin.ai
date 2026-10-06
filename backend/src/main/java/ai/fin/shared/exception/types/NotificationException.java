package ai.fin.shared.exception.types;

import ai.fin.shared.exception.ErrorCode;

public class NotificationException extends BusinessException {

    public NotificationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotificationException(ErrorCode errorCode, String userMessage) {
        super(errorCode, userMessage);
    }
}
