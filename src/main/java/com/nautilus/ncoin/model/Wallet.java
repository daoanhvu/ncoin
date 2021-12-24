package com.nautilus.ncoin.model;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Wallet {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private final HashMap<String, TransactionOutput> outputs = new HashMap<>();

    public Wallet() {
        generateKeyPair();
    }

    public void generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            keyPairGenerator.initialize(ecSpec, secureRandom);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * TODO: Need to optimize this method
     * @return the total value of un spent transaction
     */
    public float getBalance() {
        outputs.clear();
        List<TransactionOutput> list = NautilusChain.getINSTANCE()
                .filterByRecipient(this.publicKey);
        list.forEach(x -> outputs.put(x.getId(), x));
        return list.stream().map(TransactionOutput::getValue).reduce(0F, Float::sum);
    }

    public Transaction sendFund(PublicKey recipient, float value) {
        if(getBalance() < value) {
            return null;
        }

        List<TransactionInput> trInputs = new ArrayList<>();
        float total = 0F;
        for(TransactionOutput ot: outputs.values()) {
            if(total >= value) {
                break;
            }
            total += ot.getValue();
            trInputs.add(new TransactionInput(ot.getId()));
        }

        Transaction trans = new Transaction(this.publicKey, recipient, value, trInputs);
        trans.generateSignature(this.privateKey);

        // For those input trans that this spent, we remove them from un-spent list
        trInputs.forEach(trI -> outputs.remove(trI.getOutputTransId()));

        return trans;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
