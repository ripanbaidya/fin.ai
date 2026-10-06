package ai.fin.service.impl;

import ai.fin.dto.paymentmode.CreatePaymentModeRequest;
import ai.fin.dto.paymentmode.PaymentModeResponse;
import ai.fin.dto.paymentmode.UpdatePaymentModeRequest;
import ai.fin.entities.PaymentMode;
import ai.fin.mapper.PaymentModeMapper;
import ai.fin.repository.PaymentModeRepository;
import ai.fin.repository.UserRepository;
import ai.fin.service.PaymentModeService;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.PaymentModeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentModeServiceImpl implements PaymentModeService {

    private final UserRepository userRepository;
    private final PaymentModeRepository paymentModeRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "payment_modes", key = "#userId")
    public List<PaymentModeResponse> getAll(String userId) {
        log.debug("Fetching payment modes visible to userId='{}'", userId);

        return paymentModeRepository.findAllVisibleToUser(userId)
                .stream()
                .map(PaymentModeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = "payment_modes", allEntries = true)
    public PaymentModeResponse create(String userId, CreatePaymentModeRequest request) {
        String name = request.name().trim();

        log.debug("Creating payment mode for userId='{}', name='{}'", userId, name);

        if (paymentModeRepository.isNameTaken(userId, name)) {
            throw new PaymentModeException(
                    ErrorCode.PAYMENT_MODE_ALREADY_EXISTS,
                    "A payment mode named '%s' already exists".formatted(name)
            );
        }

        PaymentMode paymentMode = new PaymentMode();
        paymentMode.setName(name);
        paymentMode.setUser(userRepository.getReferenceById(userId));

        PaymentMode savedPaymentMode = paymentModeRepository.save(paymentMode);
        log.info("Payment mode created successfully with id='{}' for userId='{}'", savedPaymentMode.getId(), userId);

        return PaymentModeMapper.toResponse(savedPaymentMode);
    }

    @Override
    @Transactional
    @CacheEvict(value = "payment_modes", allEntries = true)
    public PaymentModeResponse update(String userId, String paymentModeId, UpdatePaymentModeRequest request) {
        PaymentMode paymentMode = findOwnedPaymentMode(paymentModeId, userId);

        String newName = request.name().trim();

        log.debug("Updating payment mode id='{}' for userId='{}' to name='{}'",
                paymentModeId, userId, newName);

        boolean nameChanged = !paymentMode.getName().equalsIgnoreCase(newName);

        if (nameChanged) {
            if (paymentModeRepository.isNameTakenExcludingId(userId, newName, paymentModeId)) {
                throw new PaymentModeException(
                        ErrorCode.PAYMENT_MODE_ALREADY_EXISTS,
                        "A payment mode named '%s' already exists".formatted(newName)
                );
            }
        }

        paymentMode.setName(newName);

        PaymentMode updatedPaymentMode = paymentModeRepository.save(paymentMode);
        log.info("Payment mode id='{}' updated successfully for userId='{}'", paymentModeId, userId);

        return PaymentModeMapper.toResponse(updatedPaymentMode);
    }

    @Override
    @Transactional
    @CacheEvict(value = "payment_modes", allEntries = true)
    public void delete(String userId, String paymentModeId) {
        log.debug("Deleting payment mode id='{}' for userId='{}'", paymentModeId, userId);

        PaymentMode paymentMode = findOwnedPaymentMode(paymentModeId, userId);
        paymentModeRepository.delete(paymentMode);

        log.info("Payment mode id='{}' deleted successfully for userId='{}'", paymentModeId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentMode findById(String paymentModeId) {
        return paymentModeRepository.findById(paymentModeId)
                .orElseThrow(() -> new PaymentModeException(ErrorCode.PAYMENT_MODE_NOT_FOUND));
    }

    /**
     * Finds a payment mode and validates ownership against the requesting user.
     * Throws 404 NOT_FOUND if payment mode doesn't exist, and 403 ACCESS_DENIED if payment mode
     * is a system default or belongs to another user.
     */
    private PaymentMode findOwnedPaymentMode(String paymentModeId, String userId) {
        PaymentMode paymentMode = paymentModeRepository.findById(paymentModeId)
                .orElseThrow(() -> new PaymentModeException(ErrorCode.PAYMENT_MODE_NOT_FOUND));

        if (paymentMode.getUser() == null) {
            throw new PaymentModeException(
                    ErrorCode.PAYMENT_MODE_ACCESS_DENIED,
                    "System default payment modes cannot be modified or deleted."
            );
        }

        if (!userId.equals(paymentMode.getUser().getId())) {
            throw new PaymentModeException(
                    ErrorCode.PAYMENT_MODE_ACCESS_DENIED,
                    "You do not have permission to modify this payment mode."
            );
        }

        return paymentMode;
    }
}
