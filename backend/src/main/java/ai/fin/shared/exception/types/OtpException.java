package ai.fin.shared.exception.types;

import ai.fin.shared.exception.ErrorCode;

public class OtpException extends BusinessException {

    public OtpException(ErrorCode errorCode) {
        super(errorCode);
    }

    public OtpException(ErrorCode errorCode, String userMessage) {
        super(errorCode, userMessage);
    }
}