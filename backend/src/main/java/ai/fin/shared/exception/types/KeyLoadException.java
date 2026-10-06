package ai.fin.shared.exception.types;

import ai.fin.shared.exception.ErrorCode;

public class KeyLoadException extends BusinessException {

    public KeyLoadException(ErrorCode errorCode) {
        super(errorCode);
    }

    public KeyLoadException(ErrorCode errorCode, String userMessage) {
        super(errorCode, userMessage);
    }
}
