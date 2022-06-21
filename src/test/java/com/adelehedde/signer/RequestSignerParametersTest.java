package com.adelehedde.signer;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

public class RequestSignerParametersTest {

    @Test
    public void shouldCheckEqualsAndHashcode() {
        EqualsVerifier.forClass(RequestSignerParameters.class).suppress(new Warning[]{Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS}).verify();
    }
}
