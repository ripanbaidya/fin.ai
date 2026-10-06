package ai.fin.shared.api;

import java.util.List;

public record ApiErrorResponse(
        boolean success,
        int status,
        String code,
        String message,
        List<ApiErrorDetail> errors,
        ApiResponseMeta meta
) {
    public static ApiErrorResponse of(int status, String code, String message, List<ApiErrorDetail> errors,
                                      ApiResponseMeta meta) {
        return new ApiErrorResponse(false, status, code, message, errors, meta);
    }
}