package ai.fin.shared.api;

public record ApiSuccessResponse<T>(
        boolean success,
        int status,
        String code,
        String message,
        T data,
        ApiResponseMeta meta
) {
    public static <T> ApiSuccessResponse<T> of(int status, String code, String message, T data,
                                               ApiResponseMeta meta) {
        return new ApiSuccessResponse<>(true, status, code, message, data, meta);
    }
}
