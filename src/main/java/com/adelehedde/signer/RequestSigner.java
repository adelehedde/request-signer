package com.adelehedde.signer;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

public class RequestSigner extends AbstractRequestSigner {

    public RequestAuthenticationSchema signRequest(String httpMethod, String url, RequestSignerParameters requestSignerParameters) {
        long timestamp = getCurrentTimestamp();
        return signRequest(httpMethod, url, requestSignerParameters, timestamp);
    }

    public RequestAuthenticationSchema signRequest(String httpMethod, String url, RequestSignerParameters requestSignerParameters, long timestamp) {
        String signature = calculateSignature(httpMethod, url, requestSignerParameters, timestamp);
        return new RequestAuthenticationSchema(requestSignerParameters, timestamp, signature);
    }

    protected String calculateSignature(String httpMethod, String url, RequestSignerParameters requestSignerParameters, long timestamp) {
        String canonicalRequest = createCanonicalRequest(httpMethod, url, requestSignerParameters.isSignedHost());
        String stringToSign = createStringToSign(requestSignerParameters.getAuthenticationType(), requestSignerParameters.getApiKey(), requestSignerParameters.getApiVersion(), timestamp, canonicalRequest);
        byte[] signingKey = computeSigningKey(requestSignerParameters.getSecretApiKey(), requestSignerParameters.getApiVersion(), timestamp);
        return sign(stringToSign, signingKey);
    }

    protected String createCanonicalRequest(String httpMethod, String url, boolean isSignedHost) {
        try {
            URL endpointUrl = new URL(url);
            return createCanonicalRequest(httpMethod, endpointUrl, isSignedHost);
        } catch (MalformedURLException e) {
            throw new RequestSignerException(MessageFormat.format("Unable to parse url : {0}", e.getMessage()), e);
        }
    }

    protected String createCanonicalRequest(String httpMethod, URL url, boolean isSignedHost) {
        StringBuilder canonicalRequest = new StringBuilder();
        canonicalRequest.append(httpMethod.toUpperCase());
        if (isSignedHost) {
            canonicalRequest.append(RequestAuthenticationSchema.SPACE);
            canonicalRequest.append(url.getHost());
        }
        canonicalRequest.append(RequestAuthenticationSchema.SPACE);
        canonicalRequest.append(url.getPath());
        String query = url.getQuery();
        if (query != null && !"".equals(query)) {
            canonicalRequest.append(RequestAuthenticationSchema.SPACE);
            canonicalRequest.append(url.getQuery());
        }
        return canonicalRequest.toString();
    }

    protected String createStringToSign(String authenticationType, String apiKey, String apiVersion, long timestamp, String canonicalRequest) {
        return new StringBuilder()
                .append(authenticationType).append(RequestAuthenticationSchema.SPACE)
                .append(apiKey).append(RequestAuthenticationSchema.SPACE)
                .append(apiVersion).append(RequestAuthenticationSchema.SPACE)
                .append(timestamp).append(RequestAuthenticationSchema.SPACE)
                .append(encode(hash(canonicalRequest)))
                .toString();
    }

    protected byte[] computeSigningKey(String secretApiKey, String apiVersion, long timestamp) {
        byte[] secretKey = (RequestAuthenticationSchema.REQUEST_SIGNER_SCHEMA + secretApiKey).getBytes(getCharset());
        byte[] secretApiVersionKey = hash_hmac(apiVersion, secretKey);
        byte[] secretTimestampKey = hash_hmac(Long.toString(timestamp), secretApiVersionKey);
        return hash_hmac(RequestAuthenticationSchema.REQUEST_SIGNER_REQUEST, secretTimestampKey);
    }

    protected String sign(String text, byte[] signingKey) {
        byte[] signature = hash_hmac(text, signingKey);
        return encode(signature);
    }
}
