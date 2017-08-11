package com.coding.stellarbit.Transaction;

/**
 * Created by Tigra on 10.08.2017.
 */
public enum TransactionType {

    MONEY_EMISSION(0), MONEY_TRANSFER(1);

    private int id;

    TransactionType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
