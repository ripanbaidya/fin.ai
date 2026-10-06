package ai.fin.shared.exception.types;

import ai.fin.shared.exception.ErrorCode;

public class BudgetException extends BusinessException {

    public BudgetException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BudgetException(ErrorCode errorCode, String userMessage) {
        super(errorCode, userMessage);
    }
}
