package ai.fin.shared.exception.types;

import ai.fin.shared.exception.ErrorCode;

public class RAGQueryException extends BusinessException {

    public RAGQueryException(ErrorCode errorCode) {
        super(errorCode);
    }

    public RAGQueryException(ErrorCode errorCode, String userMessage) {
        super(errorCode, userMessage);
    }
}
