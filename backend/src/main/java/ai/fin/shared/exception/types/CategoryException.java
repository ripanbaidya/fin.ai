package ai.fin.shared.exception.types;

import ai.fin.shared.exception.ErrorCode;

public class CategoryException extends BusinessException {

    public CategoryException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CategoryException(ErrorCode errorCode, String userMessage) {
        super(errorCode, userMessage);
    }
}
