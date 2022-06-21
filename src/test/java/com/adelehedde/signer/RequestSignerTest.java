package com.adelehedde.signer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Stream;

public class RequestSignerTest {

    private static Stream<String> MALFORMED_URL_PARAMETERS() {
        return Stream.of(
                "api.com",
                "signer://api.com"
        );
    }

    private static Stream<Arguments> CANONICAL_REQUEST_PARAMETERS() throws MalformedURLException {
        URL urlWithParameters = new URL("https://api.com/search?product_id=prd1&customer_id=c1");
        URL urlWithoutParameters = new URL("https://api.com/search");
        return Stream.of(
                Arguments.of("GET api.com /search product_id=prd1&customer_id=c1", "get", urlWithParameters, true),
                Arguments.of("GET /search product_id=prd1&customer_id=c1", "GET", urlWithParameters, false),
                Arguments.of("POST api.com /search", "post", urlWithoutParameters, true),
                Arguments.of("POST /search", "POST", urlWithoutParameters, false)
        );
    }

    private RequestSigner requestSigner = new RequestSigner();

    @Test
    public void shouldSignRequest() {
        RequestSignerParameters requestSignerParameters = new RequestSignerParameters("aaa-bbb-ccc", "secret-aaa-bbb-ccc", "v1");
        RequestAuthenticationSchema requestAuthenticationSchema = requestSigner.signRequest("GET", "https://api.com/search?product_id=prd1", requestSignerParameters);
        Assertions.assertNotNull(requestAuthenticationSchema);
        Assertions.assertNotNull(requestAuthenticationSchema.getSignature());
    }

    @Test
    public void shouldSignRequestWithCustomTimestamp() {
        RequestSignerParameters requestSignerParameters = new RequestSignerParameters("aaa-bbb-ccc", "secret-aaa-bbb-ccc", "v1");
        long timestamp = 1585733039477L;
        RequestAuthenticationSchema requestAuthenticationSchema = requestSigner.signRequest("GET", "https://api.com/search?product_id=prd1", requestSignerParameters, timestamp);
        Assertions.assertNotNull(requestAuthenticationSchema);
        Assertions.assertEquals("REQUEST-SIGNATURE ApiKey=aaa-bbb-ccc,ApiVersion=v1,SignedHost=true,Timestamp=1585733039477,Signature=kqZmfo4_lfLoAhmlg0XNFWbygQ7GRnTbjBBOcAVu_po", requestAuthenticationSchema.getAuthorizationHeader());
        Assertions.assertEquals("kqZmfo4_lfLoAhmlg0XNFWbygQ7GRnTbjBBOcAVu_po", requestAuthenticationSchema.getSignature());
    }

    @Test
    public void shouldCalculateSignature() {
        RequestSignerParameters requestSignerParameters = new RequestSignerParameters("aaa-bbb-ccc", "secret-aaa-bbb-ccc", "v1");
        String signature = requestSigner.calculateSignature("GET", "https://api.com", requestSignerParameters, 1585733039477L);
        Assertions.assertNotNull(signature);
        Assertions.assertEquals("oZw3_NJhq8GWvXZ_4B7lteZMHHkRI_yLmIgq4VJlfss", signature);
    }

    @ParameterizedTest
    @MethodSource("MALFORMED_URL_PARAMETERS")
    public void shouldThrowExceptionWhenUrlIsMalformed(String url) {
        Assertions.assertThrows(RequestSignerException.class, () -> requestSigner.createCanonicalRequest("GET", url, true));
    }

    @ParameterizedTest
    @MethodSource("CANONICAL_REQUEST_PARAMETERS")
    public void shouldCreateCanonicalRequest(String expected, String httpMethod, URL url, boolean signedHost) {
        Assertions.assertEquals(expected, requestSigner.createCanonicalRequest(httpMethod, url, signedHost));
    }

    @Test
    public void shouldCreateStringToSign() {
        String canonicalRequest = "GET api.com /search";
        Assertions.assertEquals("REQUEST-SIGNATURE aaa-bbb-ccc v1 1585658784903 zkHoDF4mn0ZLHsmVkueJysX1I0rBaMQWwpWA2bRNHbw", requestSigner.createStringToSign("REQUEST-SIGNATURE", "aaa-bbb-ccc", "v1", 1585658784903L, canonicalRequest));
    }

    @Test
    public void shouldComputeSigningKey() {
        byte[] signinKey = requestSigner.computeSigningKey("SecretApiKey", "v1", 1585658784903L);
        Assertions.assertEquals("uT-NHNKtsf6nl2smF3i57Cen7PiJ-7VDCvi_AjEyyNM", requestSigner.encode(signinKey));
    }

    @Test
    public void shouldSign() {
        String signature = requestSigner.sign("text to sign", "key".getBytes(requestSigner.getCharset()));
        Assertions.assertEquals("JSCFdQh6hHJ7yP702Il9CxR3FA6StoD2GMirIYdeOGU", signature);
    }
}
