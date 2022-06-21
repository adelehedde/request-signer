package com.adelehedde.signer;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.Base64;

public abstract class AbstractRequestSigner {

    public static final Charset UTF8_CHARSET = StandardCharsets.UTF_8;
    public static final String SHA_256_ALGORITHM = "SHA-256";
    public static final String HMAC_SHA_256_ALGORITHM = "HmacSHA256";

    protected Charset getCharset() {
        return UTF8_CHARSET;
    }

    protected String getCharsetName() {
        return getCharset().toString();
    }

    protected String getHashAlgorithm() {
        return SHA_256_ALGORITHM;
    }

    protected String getHashMacAlgorithm() {
        return HMAC_SHA_256_ALGORITHM;
    }

    protected long getCurrentTimestamp() {
        return Instant.now().toEpochMilli();
    }

    protected String encode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    protected byte[] hash(String text) {
        try {
            return hash(text.getBytes(getCharsetName()));
        } catch (UnsupportedEncodingException e) {
            throw new RequestSignerException(MessageFormat.format("Unable to hash text : {0}", e.getMessage()), e);
        }
    }

    protected byte[] hash(byte[] data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(getHashAlgorithm());
            messageDigest.update(data);
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RequestSignerException(MessageFormat.format("Unable to hash data : {0}", e.getMessage()), e);
        }
    }

    protected byte[] hash_hmac(String text, byte[] key) {
        try {
            Mac mac = Mac.getInstance(getHashMacAlgorithm());
            mac.init(new SecretKeySpec(key, getHashMacAlgorithm()));
            return mac.doFinal(text.getBytes(getCharset()));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RequestSignerException(MessageFormat.format("Unable to hash_hmac : {0}", e.getMessage()), e);
        }
    }
}
