package ai.fin.util;

import java.security.SecureRandom;

public final class SecurityValueGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateOtp(int length) {
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            otp.append(RANDOM.nextInt(10));
        }
        return otp.toString();
    }
}
