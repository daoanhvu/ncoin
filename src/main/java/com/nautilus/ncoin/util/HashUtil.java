package com.nautilus.ncoin.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

public final class HashUtil {

    private static final String ALGO = "SHA-256";

    public static String encodeSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGO);
            byte[] hashData = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            // Hexadecimal
            StringBuilder stringBuilder = new StringBuilder();
            for (byte hashDatum : hashData) {
                String hex = Integer.toHexString(0xff & hashDatum);
                if (hex.length() == 1) {
                    stringBuilder.append("0");
                }
                stringBuilder.append(hex);
            }
            return stringBuilder.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static byte[] signECDSA(PrivateKey prKey, String content) {
        try {
            Signature dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(prKey);
            byte[] inputData = content.getBytes();
            dsa.update(inputData);
            return dsa.sign();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static boolean verifyECDSA(PublicKey pubKey, String data, byte[] signature) {
        try {
            Signature dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initVerify(pubKey);
            dsa.update(data.getBytes());
            return dsa.verify(signature);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getTextFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}
