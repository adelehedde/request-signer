package com.adelehedde.signer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class AbstractRequestSignerTest {

    private AbstractRequestSigner abstractRequestSigner = Mockito.mock(AbstractRequestSigner.class, Mockito.CALLS_REAL_METHODS);

    @Test
    public void shouldEncodeData() {
        byte[] data = "1 String To Encode".getBytes(abstractRequestSigner.getCharset());
        Assertions.assertEquals("MSBTdHJpbmcgVG8gRW5jb2Rl", abstractRequestSigner.encode(data));
    }

    @Test
    public void shouldHashText() {
        String text = "Text to hash";
        byte[] hashedData = abstractRequestSigner.hash(text);
        Assertions.assertEquals("mzNZbNHJHn_WJE_LJrFpDLqysK1-2FcqS2vANNIYlts", abstractRequestSigner.encode(hashedData));
    }

    @Test
    public void shouldHashData() {
        byte[] data = "Text to hash".getBytes(abstractRequestSigner.getCharset());
        byte[] hashedData = abstractRequestSigner.hash(data);
        Assertions.assertEquals("mzNZbNHJHn_WJE_LJrFpDLqysK1-2FcqS2vANNIYlts", abstractRequestSigner.encode(hashedData));
    }

    @Test
    public void shouldHashHmacText() {
        String text = "Text to Hash Hmac";
        byte[] key = "Secret Key".getBytes(abstractRequestSigner.getCharset());
        byte[] hashHmac = abstractRequestSigner.hash_hmac(text, key);
        Assertions.assertEquals("Y1Ub2H4Cu-qDfS_34NcTMlAMdfRHhvEgRfZn0BQoVtE", abstractRequestSigner.encode(hashHmac));
    }

    @Test
    public void shouldThrowExceptionWhenAlgorithmIsWrong() {
        Mockito.when(abstractRequestSigner.getHashAlgorithm()).thenReturn("SHA-666");
        Mockito.when(abstractRequestSigner.getHashMacAlgorithm()).thenReturn("HmacSHA666");
        Assertions.assertThrows(RequestSignerException.class, () -> abstractRequestSigner.hash("text"));
        Assertions.assertThrows(RequestSignerException.class, () -> abstractRequestSigner.hash_hmac("text","key".getBytes(abstractRequestSigner.getCharset())));
    }
}
