package com.nautilus.ncoin.model;

import com.nautilus.ncoin.util.HashUtil;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Transaction {

    // A rough count of how many transactions have been generated.
    private static int sequence = 0;

    private String id;
    private PublicKey sender;
    private PublicKey recipient;
    private float value;
    private byte[] signature;

    private final List<TransactionInput> inputs = new ArrayList<>();
    private final List<TransactionOutput> outputs = new ArrayList<>();

    public Transaction(PublicKey from, PublicKey recip, float val,
                       List<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = recip;
        this.value = val;
        if(inputs != null) {
            this.inputs.addAll(inputs);
        }
    }

    private String calculateHash() {
        sequence++;

        return HashUtil.encodeSHA256(HashUtil.getTextFromKey(sender)
                + HashUtil.getTextFromKey(recipient)
                + value + sequence);
    }

    public void generateSignature(PrivateKey prKey) {
        String data = HashUtil.getTextFromKey(sender) + HashUtil.getTextFromKey(recipient)
                + value;
        signature = HashUtil.signECDSA(prKey, data);
    }

    public boolean verifySignature() {
        String data = HashUtil.getTextFromKey(sender) + HashUtil.getTextFromKey(recipient)
                + value;
        return HashUtil.verifyECDSA(sender, data, signature);
    }

    public boolean process() {
        if(!verifySignature()) {
            return false;
        }
        NautilusChain theChain = NautilusChain.getINSTANCE();

        for(TransactionInput input: inputs) {
            input.setOutput(theChain.getUnSpentOutput(input.getOutputTransId()));
        }

        float leftOver = getInputValue() - value;
        this.id = calculateHash();
        // Transfer the value to recipient
        outputs.add(new TransactionOutput(this.recipient, value, this.id));
        // and keep leftOver to the sender
        outputs.add(new TransactionOutput(this.sender, leftOver, this.id));
        // Add outputs to unspent list
        outputs.forEach(o -> theChain.putUnSpentOutput(o.getId(), o));
        // Remove the spent outputs
        inputs.stream()
                .filter(input -> input.getOutput() != null)
                .forEach(input -> theChain.removeOutput(input.getOutput().getId()));
        return true;
    }

    public float getInputValue() {
        return inputs.stream()
                .filter(t -> t.getOutput() != null)
                .map(t -> t.getOutput().getValue())
                .reduce(0.0F, Float::sum);
    }

    public float getOutputValue() {
        return outputs.stream().map(TransactionOutput::getValue).reduce(0.0F, Float::sum);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PublicKey getSender() {
        return sender;
    }

    public void setSender(PublicKey sender) {
        this.sender = sender;
    }

    public PublicKey getRecipient() {
        return recipient;
    }

    public void setRecipient(PublicKey recipient) {
        this.recipient = recipient;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public byte[] getSignature() {
        return signature;
    }
}
