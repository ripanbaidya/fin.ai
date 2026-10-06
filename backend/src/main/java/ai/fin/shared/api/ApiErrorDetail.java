package ai.fin.shared.api;

public record ApiErrorDetail(
        String field,
        Object rejectedValue,
        String reason
) {
}
