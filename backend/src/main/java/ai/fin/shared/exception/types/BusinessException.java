package ai.fin.shared.exception.types;

import ai.fin.shared.api.ApiErrorDetail;
import ai.fin.shared.exception.BaseException;
import ai.fin.shared.exception.ErrorCode;

import java.util.List;

public class BusinessException extends BaseException {

    public BusinessException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BusinessException(ErrorCode errorCode, String userMessage) {
        super(errorCode, userMessage);
    }

    public BusinessException(ErrorCode errorCode, String userMessage, List<ApiErrorDetail> errors) {
        super(errorCode, userMessage, errors);
    }
}
