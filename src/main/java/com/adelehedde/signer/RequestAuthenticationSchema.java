package com.adelehedde.signer;

public class RequestAuthenticationSchema {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String SIGNATURE_AUTHENTICATION_TYPE = "REQUEST-SIGNATURE";
    public static final String REQUEST_SIGNER_SCHEMA = "REQUEST_SIGNER";
    public static final String REQUEST_SIGNER_REQUEST = "REQUEST_SIGNER_REQUEST";

    public static final String API_KEY = "ApiKey";
    public static final String API_VERSION = "ApiVersion";
    public static final String SIGNED_HOST = "SignedHost";
    public static final String TIMESTAMP = "Timestamp";
    public static final String SIGNATURE = "Signature";

    public static final String KEY_VALUE_SEPARATOR = "=";
    public static final String COMPONENT_SEPARATOR = ",";
    public static final String SPACE = " ";

    private String authenticationType;
    private String apiKey;
    private String apiVersion;
    private boolean signedHost;
    private long timestamp;
    private String signature;
    private String authorizationHeader;

    public RequestAuthenticationSchema() {
    }

    public RequestAuthenticationSchema(RequestSignerParameters requestSignerParameters, long timestamp, String signature) {
        this.authenticationType = requestSignerParameters.getAuthenticationType();
        this.apiKey = requestSignerParameters.getApiKey();
        this.apiVersion = requestSignerParameters.getApiVersion();
        this.signedHost = requestSignerParameters.isSignedHost();
        this.timestamp = timestamp;
        this.signature = signature;
        this.authorizationHeader = computeAuthorizationHeader();
    }

    private String computeAuthorizationHeader() {
        return new StringBuilder()
                .append(authenticationType)
                .append(SPACE)
                .append(API_KEY).append(KEY_VALUE_SEPARATOR).append(apiKey).append(COMPONENT_SEPARATOR)
                .append(API_VERSION).append(KEY_VALUE_SEPARATOR).append(apiVersion).append(COMPONENT_SEPARATOR)
                .append(SIGNED_HOST).append(KEY_VALUE_SEPARATOR).append(signedHost).append(COMPONENT_SEPARATOR)
                .append(TIMESTAMP).append(KEY_VALUE_SEPARATOR).append(timestamp).append(COMPONENT_SEPARATOR)
                .append(SIGNATURE).append(KEY_VALUE_SEPARATOR).append(signature)
                .toString();
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public boolean isSignedHost() {
        return signedHost;
    }

    public void setSignedHost(boolean signedHost) {
        this.signedHost = signedHost;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAuthorizationHeader() {
        return authorizationHeader;
    }

    public void setAuthorizationHeader(String authorizationHeader) {
        this.authorizationHeader = authorizationHeader;
    }

    @Override
    public String toString() {
        return "RequestAuthenticationSchema{" +
                "authenticationType='" + authenticationType + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", apiVersion='" + apiVersion + '\'' +
                ", signedHost=" + signedHost +
                ", timestamp=" + timestamp +
                ", signature='" + signature + '\'' +
                ", authorizationHeader='" + authorizationHeader + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestAuthenticationSchema)) return false;

        RequestAuthenticationSchema that = (RequestAuthenticationSchema) o;

        if (signedHost != that.signedHost) return false;
        if (timestamp != that.timestamp) return false;
        if (authenticationType != null ? !authenticationType.equals(that.authenticationType) : that.authenticationType != null) return false;
        if (apiKey != null ? !apiKey.equals(that.apiKey) : that.apiKey != null) return false;
        if (apiVersion != null ? !apiVersion.equals(that.apiVersion) : that.apiVersion != null) return false;
        if (signature != null ? !signature.equals(that.signature) : that.signature != null) return false;
        return authorizationHeader != null ? authorizationHeader.equals(that.authorizationHeader) : that.authorizationHeader == null;
    }

    @Override
    public int hashCode() {
        int result = authenticationType != null ? authenticationType.hashCode() : 0;
        result = 31 * result + (apiKey != null ? apiKey.hashCode() : 0);
        result = 31 * result + (apiVersion != null ? apiVersion.hashCode() : 0);
        result = 31 * result + (signedHost ? 1 : 0);
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (signature != null ? signature.hashCode() : 0);
        result = 31 * result + (authorizationHeader != null ? authorizationHeader.hashCode() : 0);
        return result;
    }
}
