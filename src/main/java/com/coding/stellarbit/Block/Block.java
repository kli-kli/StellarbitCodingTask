package com.coding.stellarbit.Block;

import com.coding.stellarbit.Transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tigra on 10.08.2017.
 */
public class Block {

    private final int id;

    private final List<Transaction> listOfTransactions = new ArrayList<>();


    public Block(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean validateTransaction(Transaction transaction) {
        if (transaction != null && transaction.getSignature() != null
                && transaction.getSignature().length() == 32) return true;
        return false;

    }

    public void addTransaction(Transaction transaction) {
        if (validateTransaction(transaction)
                && checkNumberOfExistingTransactions()
                && checkTransactionIdIsNotUsed(transaction.getId())) {
            listOfTransactions.add(transaction);
        }


    }

    private boolean checkTransactionIdIsNotUsed(int id) {
        return listOfTransactions.stream().noneMatch(transaction -> transaction.getId() == id);
    }

    private boolean checkNumberOfExistingTransactions() {
        return listOfTransactions.size() < 10;
    }

    public List<Transaction> getListOfTransactions() {
        return new ArrayList(listOfTransactions);
    }
}
