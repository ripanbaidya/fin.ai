package ai.fin.shared.exception.types;

import ai.fin.shared.api.ApiErrorDetail;
import ai.fin.shared.exception.BaseException;
import ai.fin.shared.exception.ErrorCode;

import java.util.List;

public class PaymentException extends BaseException {

    public PaymentException(ErrorCode errorCode) {
        super(errorCode);
    }

    public PaymentException(ErrorCode errorCode, String userMessage) {
        super(errorCode, userMessage);
    }

    public PaymentException(ErrorCode errorCode, String userMessage, List<ApiErrorDetail> errors) {
        super(errorCode, userMessage, errors);
    }
}
