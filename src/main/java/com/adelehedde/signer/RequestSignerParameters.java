package com.adelehedde.signer;

public class RequestSignerParameters {

    private static final boolean DEFAULT_SIGNED_HOST = true;
    private static final String DEFAULT_AUTHENTICATION_TYPE = RequestAuthenticationSchema.SIGNATURE_AUTHENTICATION_TYPE;

    private String apiKey;
    private String secretApiKey;
    private String apiVersion;
    private boolean signedHost;
    private String authenticationType;

    public RequestSignerParameters() {
    }

    public RequestSignerParameters(String apiKey, String secretApiKey, String apiVersion) {
        this(apiKey, secretApiKey, apiVersion, DEFAULT_SIGNED_HOST, DEFAULT_AUTHENTICATION_TYPE);
    }

    public RequestSignerParameters(String apiKey, String secretApiKey, String apiVersion, boolean signedHost, String authenticationType) {
        this.apiKey = apiKey;
        this.secretApiKey = secretApiKey;
        this.apiVersion = apiVersion;
        this.signedHost = signedHost;
        this.authenticationType = authenticationType;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecretApiKey() {
        return secretApiKey;
    }

    public void setSecretApiKey(String secretApiKey) {
        this.secretApiKey = secretApiKey;
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

    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    @Override
    public String toString() {
        return "RequestSignerParameters{" +
                "apiKey='" + apiKey + '\'' +
                ", secretApiKey='" + secretApiKey + '\'' +
                ", apiVersion='" + apiVersion + '\'' +
                ", signedHost=" + signedHost +
                ", authenticationType=" + authenticationType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestSignerParameters)) return false;

        RequestSignerParameters that = (RequestSignerParameters) o;

        if (signedHost != that.signedHost) return false;
        if (apiKey != null ? !apiKey.equals(that.apiKey) : that.apiKey != null) return false;
        if (secretApiKey != null ? !secretApiKey.equals(that.secretApiKey) : that.secretApiKey != null) return false;
        if (apiVersion != null ? !apiVersion.equals(that.apiVersion) : that.apiVersion != null) return false;
        return authenticationType != null ? authenticationType.equals(that.authenticationType) : that.authenticationType == null;
    }

    @Override
    public int hashCode() {
        int result = apiKey != null ? apiKey.hashCode() : 0;
        result = 31 * result + (secretApiKey != null ? secretApiKey.hashCode() : 0);
        result = 31 * result + (apiVersion != null ? apiVersion.hashCode() : 0);
        result = 31 * result + (signedHost ? 1 : 0);
        result = 31 * result + (authenticationType != null ? authenticationType.hashCode() : 0);
        return result;
    }
}
