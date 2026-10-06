package ai.fin.service;

import ai.fin.dto.paymentmode.CreatePaymentModeRequest;
import ai.fin.dto.paymentmode.PaymentModeResponse;
import ai.fin.dto.paymentmode.UpdatePaymentModeRequest;
import ai.fin.entities.PaymentMode;

import java.util.List;

public interface PaymentModeService {

    /**
     * Retrieves all payment modes visible to the current user.
     * Includes both system default and user-defined payment modes.
     */
    List<PaymentModeResponse> getAll(String userId);

    /**
     * Creates a new payment mode for the current user.
     * Ensures no duplicate payment mode exists with the same name (case-insensitive) for the user.
     */
    PaymentModeResponse create(String userId, CreatePaymentModeRequest request);

    /**
     * Updates an existing payment mode owned by the current user.
     * Validates uniqueness only when the name is changed.
     */
    PaymentModeResponse update(String userId, String paymentModeId, UpdatePaymentModeRequest request);

    /**
     * Deletes a payment mode owned by the current user.
     */
    void delete(String userId, String paymentModeId);

    /**
     * Retrieves a payment mode by its ID.
     */
    PaymentMode findById(String id);
}