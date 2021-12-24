package com.nautilus.ncoin.model;

public class TransactionInput {
    private String outputTransId;
    private TransactionOutput output;

    public TransactionInput(String _outputTransId) {
        this.outputTransId = _outputTransId;
    }

    public String getOutputTransId() {
        return outputTransId;
    }

    public TransactionOutput getOutput() {
        return output;
    }

    public void setOutput(TransactionOutput output) {
        this.output = output;
    }
}
