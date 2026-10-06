package ai.fin.shared.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    OK("OK"),
    CREATED("CREATED"),
    UPDATED("UPDATED"),
    DELETED("DELETED");

    private final String code;
}
