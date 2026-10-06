package ai.fin.shared.exception.types;

import ai.fin.shared.exception.ErrorCode;

public class VectorStoreException extends BusinessException {

    public VectorStoreException(ErrorCode errorCode) {
        super(errorCode);
    }

    public VectorStoreException(ErrorCode errorCode, String userMessage) {
        super(errorCode, userMessage);
    }
}
