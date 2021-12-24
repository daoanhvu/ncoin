package com.nautilus.ncoin.model;

import com.nautilus.ncoin.util.HashUtil;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class NautilusChain {
    private static NautilusChain INSTANCE;

    private int difficulty;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final List<Block<Hashable>> chain = new ArrayList<>();
    //
    private final HashMap<String, TransactionOutput> unSpentTransactionOutputs =
            new HashMap<>();

    private NautilusChain() {
        difficulty = 5;
    }

    private NautilusChain(int diff) {
        difficulty = diff;
    }

    public static NautilusChain getINSTANCE() {
        if(INSTANCE != null) {
            return INSTANCE;
        }

        INSTANCE = new NautilusChain(5);
        return INSTANCE;
    }

    public TransactionOutput getUnSpentOutput(String key) {
        return unSpentTransactionOutputs.get(key);
    }

    public void putUnSpentOutput(String key, TransactionOutput anOutput) {
        unSpentTransactionOutputs.put(key, anOutput);
    }

    public List<TransactionOutput> filterByRecipient(final PublicKey recip) {
        return unSpentTransactionOutputs.values().stream().filter(x -> x.isMine(recip))
                .collect(Collectors.toList());
    }

    public void removeOutput(String outputId) {
        unSpentTransactionOutputs.remove(outputId);
    }

    public void add(Hashable data) {
        lock.readLock().lock();
        try {
            if(chain.isEmpty()) {
                chain.add(new Block<>(data, "0"));
                return;
            }
            Block<Hashable> last = chain.get(chain.size() - 1);
            chain.add(new Block<>(data, last.getHash()));
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean validate() {
        String target = new String(new char[difficulty]).replace('\0', '0');
        lock.writeLock().lock();
        try {
            Block<Hashable> block;
            Block<Hashable> prevBlock;
            final int size = chain.size();
            for(int i=1; i<size; i++) {
                prevBlock = chain.get(i-1);
                block = chain.get(i);
                String nowHash = HashUtil.encodeSHA256(prevBlock.getHash() +
                        block.getTimestamp() + block.getNonce() + block.getData().hashString());
                if(!block.getHash().equals(nowHash)) {
                    return false;
                }

                //All blocks need to be mined
                if(!block.getHash().substring(0, difficulty).equals(target)) {
                    return false;
                }
            }
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
