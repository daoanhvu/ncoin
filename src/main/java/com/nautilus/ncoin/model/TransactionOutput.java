package com.nautilus.ncoin.model;

import java.security.PublicKey;

public class TransactionOutput {
    private String id;
    private PublicKey recipient;
    private float value;
    private String parentTransactionId;

    public TransactionOutput(PublicKey recipient, float value, String transId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = transId;
    }

    public boolean isMine(PublicKey pub) {
        return (recipient == pub);
    }

    public String getId() {
        return id;
    }

    public PublicKey getRecipient() {
        return recipient;
    }

    public float getValue() {
        return value;
    }

    public String getParentTransactionId() {
        return parentTransactionId;
    }
}
