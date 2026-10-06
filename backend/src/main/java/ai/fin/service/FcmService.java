package ai.fin.service;

import java.util.List;
import java.util.Map;

public interface FcmService {

    void sendPushToUser(String userId, String title, String body, Map<String, String> data);

    void sendPushToTokens(List<String> tokens, String title, String body, Map<String, String> data);
}
