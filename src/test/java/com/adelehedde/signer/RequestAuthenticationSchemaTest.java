package com.adelehedde.signer;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RequestAuthenticationSchemaTest {

    @Test
    public void shouldCheckEqualsAndHashcode() {
        EqualsVerifier.forClass(RequestAuthenticationSchema.class).suppress(new Warning[]{Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS}).verify();
    }

    @Test
    public void shouldGetAuthorizationHeader() {
        RequestSignerParameters requestSignerParameters = new RequestSignerParameters("api-key", "secret-api-key", "v1");
        RequestAuthenticationSchema requestAuthenticationSchema = new RequestAuthenticationSchema(requestSignerParameters, 1585658784903L, "abcde");
        String authorizationHeader = requestAuthenticationSchema.getAuthorizationHeader();
        Assertions.assertNotNull(authorizationHeader);
        Assertions.assertEquals("REQUEST-SIGNATURE ApiKey=api-key,ApiVersion=v1,SignedHost=true,Timestamp=1585658784903,Signature=abcde", authorizationHeader);
    }
}
