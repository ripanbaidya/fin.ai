package ai.fin.shared.exception.types;

import ai.fin.shared.exception.ErrorCode;

public class ChatSessionException extends BusinessException {

    public ChatSessionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChatSessionException(ErrorCode errorCode, String userMessage) {
        super(errorCode, userMessage);
    }
}
