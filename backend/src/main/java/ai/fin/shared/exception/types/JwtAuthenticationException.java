package ai.fin.shared.exception.types;

import ai.fin.shared.exception.ErrorCode;

public class JwtAuthenticationException extends BusinessException {

    public JwtAuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
