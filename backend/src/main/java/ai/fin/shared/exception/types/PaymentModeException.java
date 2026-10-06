package ai.fin.shared.exception.types;

import ai.fin.shared.exception.ErrorCode;

public class PaymentModeException extends BusinessException {

    public PaymentModeException(ErrorCode errorCode) {
        super(errorCode);
    }

    public PaymentModeException(ErrorCode errorCode, String userMessage) {
        super(errorCode, userMessage);
    }
}
