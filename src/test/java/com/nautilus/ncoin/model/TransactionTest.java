package com.nautilus.ncoin.model;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.Security;

public class TransactionTest {

    @BeforeAll
    static void setup() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testTransaction() {
        Wallet sender = new Wallet();
        Wallet receiver = new Wallet();
        Transaction aTrans = new Transaction(sender.getPublicKey(),
                receiver.getPublicKey(), 1000, null);
        aTrans.generateSignature(sender.getPrivateKey());
        Assertions.assertTrue(aTrans.verifySignature());
    }
}
