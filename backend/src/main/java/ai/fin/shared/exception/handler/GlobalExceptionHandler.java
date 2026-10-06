package ai.fin.shared.exception.handler;

import ai.fin.shared.api.ApiErrorDetail;
import ai.fin.shared.api.ApiErrorResponse;
import ai.fin.shared.api.factory.ApiResponseFactory;
import ai.fin.shared.exception.BaseException;
import ai.fin.shared.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiErrorResponse> handleBaseException(BaseException ex) {
        return ApiResponseFactory.error(
                ex.getErrorCode(),
                ex.getUserMessage(),
                ex.getErrors());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        List<ApiErrorDetail> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toDetail)
                .toList();

        return ApiResponseFactory.error(
                ErrorCode.VALIDATION_FAILED,
                ErrorCode.VALIDATION_FAILED.getDefaultMessage(),
                errors);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiErrorResponse> handleBindException(BindException ex) {

        List<ApiErrorDetail> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toDetail)
                .toList();

        return ApiResponseFactory.error(
                ErrorCode.VALIDATION_FAILED,
                ErrorCode.VALIDATION_FAILED.getDefaultMessage(),
                errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {

        List<ApiErrorDetail> errors = ex.getConstraintViolations()
                .stream()
                .map(v -> new ApiErrorDetail(
                        v.getPropertyPath().toString(),
                        v.getInvalidValue(),
                        v.getMessage()))
                .toList();

        return ApiResponseFactory.error(
                ErrorCode.VALIDATION_FAILED,
                ErrorCode.VALIDATION_FAILED.getDefaultMessage(),
                errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {

        return ApiResponseFactory.error(ErrorCode.MALFORMED_JSON);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex) {

        ApiErrorDetail detail = new ApiErrorDetail(
                ex.getParameterName(),
                null,
                ex.getMessage());

        return ApiResponseFactory.error(
                ErrorCode.MISSING_PARAMETER,
                ErrorCode.MISSING_PARAMETER.getDefaultMessage(),
                List.of(detail));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        String reason = "Expected type: " + (ex.getRequiredType() == null
                ? "unknown"
                : ex.getRequiredType().getSimpleName());

        ApiErrorDetail detail = new ApiErrorDetail(ex.getName(), ex.getValue(), reason);

        return ApiResponseFactory.error(
                ErrorCode.TYPE_MISMATCH,
                ErrorCode.TYPE_MISMATCH.getDefaultMessage(),
                List.of(detail));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return ApiResponseFactory.error(ErrorCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex) {
        return ApiResponseFactory.error(ErrorCode.NO_HANDLER_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ApiResponseFactory.error(ErrorCode.DATA_INTEGRITY_VIOLATION);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        return ApiResponseFactory.error(ErrorCode.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return ApiResponseFactory.error(ErrorCode.ACCESS_DENIED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnhandledException(Exception ex) {
        log.error("Unhandled exception occurred", ex);
        return ApiResponseFactory.error(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ApiErrorDetail toDetail(FieldError fieldError) {
        return new ApiErrorDetail(
                fieldError.getField(),
                fieldError.getRejectedValue(),
                fieldError.getDefaultMessage() == null ? "Invalid value" : fieldError.getDefaultMessage());
    }
}