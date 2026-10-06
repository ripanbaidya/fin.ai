package ai.fin.mapper;

import ai.fin.dto.paymentmode.PaymentModeResponse;
import ai.fin.entities.PaymentMode;

public final class PaymentModeMapper {

    private PaymentModeMapper() {
    }

    public static PaymentModeResponse toResponse(PaymentMode paymentMode) {
        return new PaymentModeResponse(
                paymentMode.getId(),
                paymentMode.getName(),
                paymentMode.getUser() == null
        );
    }
}
