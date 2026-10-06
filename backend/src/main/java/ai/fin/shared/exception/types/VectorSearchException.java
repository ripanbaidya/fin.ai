package ai.fin.shared.exception.types;

import ai.fin.shared.exception.ErrorCode;

public class VectorSearchException extends BusinessException {

    public VectorSearchException(ErrorCode errorCode) {
        super(errorCode);
    }

    public VectorSearchException(ErrorCode errorCode, String userMessage) {
        super(errorCode, userMessage);
    }
}
