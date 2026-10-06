package ai.fin.shared.exception.types;

import ai.fin.shared.exception.ErrorCode;

public class TransactionException extends BusinessException {

    public TransactionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TransactionException(ErrorCode errorCode, String userMessage) {
        super(errorCode, userMessage);
    }
}
