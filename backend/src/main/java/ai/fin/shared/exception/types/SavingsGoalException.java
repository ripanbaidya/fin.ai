package ai.fin.shared.exception.types;

import ai.fin.shared.exception.ErrorCode;

public class SavingsGoalException extends BusinessException {

    public SavingsGoalException(ErrorCode errorCode) {
        super(errorCode);
    }

    public SavingsGoalException(ErrorCode errorCode, String userMessage) {
        super(errorCode, userMessage);
    }
}
