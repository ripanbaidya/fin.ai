package ai.fin.shared.exception.types;

import ai.fin.shared.exception.ErrorCode;

public class RecurringTransactionException extends BusinessException {

    public RecurringTransactionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public RecurringTransactionException(ErrorCode errorCode, String userMessage) {
        super(errorCode, userMessage);
    }
}
