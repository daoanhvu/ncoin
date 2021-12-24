package com.nautilus.ncoin.model;

import com.nautilus.ncoin.util.HashUtil;

import java.util.ArrayList;
import java.util.List;

public class Block<T extends Hashable> {
    private String hash;
    private String previousHash;
    private T data;
    private long timestamp;
    private int nonce;
    private List<Transaction> transactions = new ArrayList<>();

    public Block(T data, String preHash) {
        this.data = data;
        this.previousHash = preHash;
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    private String calculateHash() {
        String hashData = previousHash + timestamp
                + nonce
                + data.hashString();
        return HashUtil.encodeSHA256(hashData);
    }

    public void mine(int difficult) {
        String target = new String(new char[difficult]).replace('\0', '0');
        while(hash.substring(0, difficult).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

}
