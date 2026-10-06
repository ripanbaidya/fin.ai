package ai.fin.shared.api.factory;

import ai.fin.shared.api.*;
import ai.fin.shared.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class ApiResponseFactory {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    private ApiResponseFactory() {
    }

    public static <T> ResponseEntity<ApiSuccessResponse<T>> success(
            HttpStatus status, String code, String message, T data
    ) {
        ApiResponseMeta meta = buildMeta();
        ApiSuccessResponse<T> body = ApiSuccessResponse.of(status.value(), code, message, data, meta);
        return ResponseEntity.status(status).body(body);
    }

    public static <T> ResponseEntity<ApiSuccessResponse<PaginatedData<T>>> successPage(
            String code, String message, Page<T> page
    ) {
        ApiResponseMeta meta = buildMeta();
        PaginatedData<T> data = PaginatedData.from(page);
        ApiSuccessResponse<PaginatedData<T>> body = ApiSuccessResponse.of(HttpStatus.OK.value(),
                code, message, data, meta);
        return ResponseEntity.ok(body);
    }

    public static ResponseEntity<ApiErrorResponse> error(
            ErrorCode errorCode, String message, List<ApiErrorDetail> errors
    ) {
        ApiResponseMeta meta = buildMeta();
        ApiErrorResponse body = ApiErrorResponse.of(errorCode.getStatus().value(), errorCode.getCode(),
                message, errors, meta);
        return ResponseEntity.status(errorCode.getStatus()).body(body);
    }

    public static ResponseEntity<ApiErrorResponse> error(ErrorCode errorCode) {
        return error(errorCode, errorCode.getDefaultMessage(), List.of());
    }

    /*
     * In Spring MVC, each HTTP request is processed on a single thread. We can therefore
     * access the current servlet request via Spring's RequestContextHolder, which stores
     * request-scoped data in a ThreadLocal.
     */
    private static ApiResponseMeta buildMeta() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        return new ApiResponseMeta(Instant.now(), request.getRequestURI(), requestId);
    }
}
