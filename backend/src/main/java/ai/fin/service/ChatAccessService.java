package ai.fin.service;

public interface ChatAccessService {

    void checkAccess(String userId);

    void incrementFreeUsage(String userId);
}
