package ai.fin.util;

import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.KeyLoadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
public final class KeyUtils {

    private static final String PRIVATE_KEY_HEADER = "-----BEGIN PRIVATE KEY-----";
    private static final String PRIVATE_KEY_FOOTER = "-----END PRIVATE KEY-----";
    private static final String PUBLIC_KEY_HEADER = "-----BEGIN PUBLIC KEY-----";
    private static final String PUBLIC_KEY_FOOTER = "-----END PUBLIC KEY-----";
    private static final String KEY_ALGORITHM = "RSA";

    private KeyUtils() {
    }

    public static PrivateKey loadPrivateKey(String pemPath, ResourceLoader resourceLoader) {
        log.debug("Loading private key from {}", pemPath);

        try {
            byte[] keyBytes = decodePem(pemPath, resourceLoader, PRIVATE_KEY_HEADER, PRIVATE_KEY_FOOTER);
            PrivateKey key = KeyFactory.getInstance(KEY_ALGORITHM)
                    .generatePrivate(new PKCS8EncodedKeySpec(keyBytes));

            log.info("Private key loaded successfully");
            return key;
        } catch (GeneralSecurityException ex) {
            throw new KeyLoadException(ErrorCode.PRIVATE_KEY_LOAD_FAILED, "Failed to load private key!");
        }
    }

    public static PublicKey loadPublicKey(String pemPath, ResourceLoader resourceLoader) {
        log.debug("Loading public key from {}", pemPath);

        try {
            byte[] keyBytes = decodePem(pemPath, resourceLoader, PUBLIC_KEY_HEADER, PUBLIC_KEY_FOOTER);
            PublicKey key = KeyFactory.getInstance(KEY_ALGORITHM)
                    .generatePublic(new X509EncodedKeySpec(keyBytes));

            log.info("Public key loaded successfully");
            return key;
        } catch (GeneralSecurityException ex) {
            throw new KeyLoadException(ErrorCode.PUBLIC_KEY_LOAD_FAILED, "Failed to load public key!");
        }
    }

    // Helpers

    /**
     * Decodes a PEM file and returns the raw key bytes.
     */
    private static byte[] decodePem(String pemPath, ResourceLoader resourceLoader, String header, String footer) {
        String raw = readPemContent(pemPath, resourceLoader);
        String base64Body = raw.replace(header, "").replace(footer, "").replaceAll("\\s", "");

        try {
            return Base64.getDecoder().decode(base64Body);
        } catch (IllegalArgumentException ex) {
            throw new KeyLoadException(ErrorCode.INVALID_KEY_FORMAT, "Invalid key format!");
        }
    }

    /**
     * Reads the content of a PEM file from the specified location using the provided ResourceLoader.
     */
    private static String readPemContent(String location, ResourceLoader resourceLoader) {
        Resource resource = resourceLoader.getResource(location);

        if (!resource.exists()) {
            throw new KeyLoadException(ErrorCode.KEY_FILE_NOT_FOUND, "Key file not found!");
        }
        if (!resource.isReadable()) {
            throw new KeyLoadException(ErrorCode.KEY_FILE_NOT_READABLE, "Key file exists but not readable!");
        }

        try (InputStream in = resource.getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new KeyLoadException(ErrorCode.KEY_FILE_READ_FAILED, "Failed to read key file!");
        }
    }
}
