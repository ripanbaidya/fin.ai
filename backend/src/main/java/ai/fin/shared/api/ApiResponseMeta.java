package ai.fin.shared.api;

import java.time.Instant;

public record ApiResponseMeta(
        Instant timestamp,
        String path,
        String requestId
) {
}
