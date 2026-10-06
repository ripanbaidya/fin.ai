package ai.fin.service.impl;

import ai.fin.dto.paymentmode.CreatePaymentModeRequest;
import ai.fin.dto.paymentmode.PaymentModeResponse;
import ai.fin.dto.paymentmode.UpdatePaymentModeRequest;
import ai.fin.entities.PaymentMode;
import ai.fin.entities.User;
import ai.fin.repository.PaymentModeRepository;
import ai.fin.repository.UserRepository;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.PaymentModeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentModeServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentModeRepository paymentModeRepository;

    @InjectMocks
    private PaymentModeServiceImpl paymentModeService;

    private User testUser;
    private User otherUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("user-123");
        testUser.setEmail("test@walletiq.ai");

        otherUser = new User();
        otherUser.setId("user-999");
        otherUser.setEmail("other@walletiq.ai");
    }

    @Test
    @DisplayName("getAll should return visible payment modes for the user")
    void shouldReturnAllVisiblePaymentModes() {
        PaymentMode defaultMode = new PaymentMode();
        defaultMode.setId("pm-default");
        defaultMode.setName("Cash");
        defaultMode.setUser(null); // system default

        PaymentMode userMode = new PaymentMode();
        userMode.setId("pm-user");
        userMode.setName("Crypto");
        userMode.setUser(testUser);

        when(paymentModeRepository.findAllVisibleToUser("user-123"))
                .thenReturn(List.of(defaultMode, userMode));

        List<PaymentModeResponse> result = paymentModeService.getAll("user-123");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Cash");
        assertThat(result.get(0).isDefault()).isTrue();
        assertThat(result.get(1).name()).isEqualTo("Crypto");
        assertThat(result.get(1).isDefault()).isFalse();

        verify(paymentModeRepository).findAllVisibleToUser("user-123");
    }

    @Test
    @DisplayName("create should save new payment mode using JPA proxy reference without querying user DB")
    void shouldCreatePaymentModeSuccessfully() {
        CreatePaymentModeRequest request = new CreatePaymentModeRequest("UPI");

        when(paymentModeRepository.isNameTaken("user-123", "UPI")).thenReturn(false);
        when(userRepository.getReferenceById("user-123")).thenReturn(testUser);
        when(paymentModeRepository.save(any(PaymentMode.class))).thenAnswer(invocation -> {
            PaymentMode pm = invocation.getArgument(0);
            pm.setId("pm-new-1");
            return pm;
        });

        PaymentModeResponse response = paymentModeService.create("user-123", request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("pm-new-1");
        assertThat(response.name()).isEqualTo("UPI");
        assertThat(response.isDefault()).isFalse();

        ArgumentCaptor<PaymentMode> captor = ArgumentCaptor.forClass(PaymentMode.class);
        verify(paymentModeRepository).save(captor.capture());
        assertThat(captor.getValue().getUser().getId()).isEqualTo("user-123");
        verify(userRepository).getReferenceById("user-123");
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("create should throw PaymentModeException when duplicate name exists")
    void shouldRejectCreateWhenDuplicateNameExists() {
        CreatePaymentModeRequest request = new CreatePaymentModeRequest("Cash");

        when(paymentModeRepository.isNameTaken("user-123", "Cash")).thenReturn(true);

        assertThatThrownBy(() -> paymentModeService.create("user-123", request))
                .isInstanceOf(PaymentModeException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PAYMENT_MODE_ALREADY_EXISTS);

        verify(paymentModeRepository, never()).save(any());
    }

    @Test
    @DisplayName("update should modify name when valid and not duplicate")
    void shouldUpdatePaymentModeSuccessfully() {
        PaymentMode existing = new PaymentMode();
        existing.setId("pm-1");
        existing.setName("Old Name");
        existing.setUser(testUser);

        when(paymentModeRepository.findById("pm-1")).thenReturn(Optional.of(existing));
        when(paymentModeRepository.isNameTakenExcludingId("user-123", "New Name", "pm-1")).thenReturn(false);
        when(paymentModeRepository.save(any(PaymentMode.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdatePaymentModeRequest request = new UpdatePaymentModeRequest("New Name");
        PaymentModeResponse response = paymentModeService.update("user-123", "pm-1", request);

        assertThat(response.name()).isEqualTo("New Name");
        verify(paymentModeRepository).save(existing);
    }

    @Test
    @DisplayName("update should skip duplicate check when name is unchanged")
    void shouldUpdatePaymentModeWhenNameUnchanged() {
        PaymentMode existing = new PaymentMode();
        existing.setId("pm-1");
        existing.setName("Debit Card");
        existing.setUser(testUser);

        when(paymentModeRepository.findById("pm-1")).thenReturn(Optional.of(existing));
        when(paymentModeRepository.save(any(PaymentMode.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdatePaymentModeRequest request = new UpdatePaymentModeRequest("Debit Card");
        PaymentModeResponse response = paymentModeService.update("user-123", "pm-1", request);

        assertThat(response.name()).isEqualTo("Debit Card");
        verify(paymentModeRepository, never()).isNameTakenExcludingId(any(), any(), any());
    }

    @Test
    @DisplayName("update should throw PaymentModeException when updated name already exists")
    void shouldRejectUpdateWhenNameDuplicate() {
        PaymentMode existing = new PaymentMode();
        existing.setId("pm-1");
        existing.setName("Credit Card");
        existing.setUser(testUser);

        when(paymentModeRepository.findById("pm-1")).thenReturn(Optional.of(existing));
        when(paymentModeRepository.isNameTakenExcludingId("user-123", "Cash", "pm-1")).thenReturn(true);

        UpdatePaymentModeRequest request = new UpdatePaymentModeRequest("Cash");

        assertThatThrownBy(() -> paymentModeService.update("user-123", "pm-1", request))
                .isInstanceOf(PaymentModeException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PAYMENT_MODE_ALREADY_EXISTS);

        verify(paymentModeRepository, never()).save(any());
    }

    @Test
    @DisplayName("update should throw access denied when updating system default payment mode")
    void shouldRejectUpdateOnSystemDefault() {
        PaymentMode defaultMode = new PaymentMode();
        defaultMode.setId("pm-default");
        defaultMode.setName("Cash");
        defaultMode.setUser(null); // system default

        when(paymentModeRepository.findById("pm-default")).thenReturn(Optional.of(defaultMode));

        UpdatePaymentModeRequest request = new UpdatePaymentModeRequest("Cash Modified");

        assertThatThrownBy(() -> paymentModeService.update("user-123", "pm-default", request))
                .isInstanceOf(PaymentModeException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PAYMENT_MODE_ACCESS_DENIED);
    }

    @Test
    @DisplayName("update should throw access denied when updating another user's payment mode")
    void shouldRejectUpdateOnOtherUserPaymentMode() {
        PaymentMode otherUserMode = new PaymentMode();
        otherUserMode.setId("pm-other");
        otherUserMode.setName("Secret Vault");
        otherUserMode.setUser(otherUser);

        when(paymentModeRepository.findById("pm-other")).thenReturn(Optional.of(otherUserMode));

        UpdatePaymentModeRequest request = new UpdatePaymentModeRequest("Hacked");

        assertThatThrownBy(() -> paymentModeService.update("user-123", "pm-other", request))
                .isInstanceOf(PaymentModeException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PAYMENT_MODE_ACCESS_DENIED);
    }

    @Test
    @DisplayName("delete should remove owned payment mode")
    void shouldDeleteOwnedPaymentMode() {
        PaymentMode existing = new PaymentMode();
        existing.setId("pm-1");
        existing.setUser(testUser);

        when(paymentModeRepository.findById("pm-1")).thenReturn(Optional.of(existing));

        paymentModeService.delete("user-123", "pm-1");

        verify(paymentModeRepository).delete(existing);
    }

    @Test
    @DisplayName("delete should throw access denied when deleting system default")
    void shouldRejectDeleteOnSystemDefault() {
        PaymentMode defaultMode = new PaymentMode();
        defaultMode.setId("pm-default");
        defaultMode.setName("Cash");
        defaultMode.setUser(null);

        when(paymentModeRepository.findById("pm-default")).thenReturn(Optional.of(defaultMode));

        assertThatThrownBy(() -> paymentModeService.delete("user-123", "pm-default"))
                .isInstanceOf(PaymentModeException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PAYMENT_MODE_ACCESS_DENIED);

        verify(paymentModeRepository, never()).delete(any());
    }

    @Test
    @DisplayName("findById should return entity when present")
    void shouldFindById() {
        PaymentMode pm = new PaymentMode();
        pm.setId("pm-1");
        pm.setName("UPI");

        when(paymentModeRepository.findById("pm-1")).thenReturn(Optional.of(pm));

        PaymentMode result = paymentModeService.findById("pm-1");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("UPI");
    }

    @Test
    @DisplayName("findById should throw NOT_FOUND when missing")
    void shouldThrowWhenMissing() {
        when(paymentModeRepository.findById("missing-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentModeService.findById("missing-id"))
                .isInstanceOf(PaymentModeException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PAYMENT_MODE_NOT_FOUND);
    }
}
