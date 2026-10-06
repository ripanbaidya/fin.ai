package ai.fin.service;

import ai.fin.dto.devices.RegisterDeviceTokenRequest;
import ai.fin.dto.devices.UnregisterDeviceTokenRequest;

import java.util.Collection;
import java.util.List;

public interface DeviceTokenService {

    void register(String userId, RegisterDeviceTokenRequest request);

    void unregister(String userId, UnregisterDeviceTokenRequest request);

    void unregisterAll(String userId);

    List<String> getActiveTokens(String userId);

    void removeInvalidTokens(Collection<String> tokens);

    int purgeStaleTokens(int inactiveDays);
}