package ai.fin.shared.exception.types;

import ai.fin.shared.exception.ErrorCode;

public class CsvExportException extends BusinessException {

    public CsvExportException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CsvExportException(ErrorCode errorCode, String userMessage) {
        super(errorCode, userMessage);
    }
}
