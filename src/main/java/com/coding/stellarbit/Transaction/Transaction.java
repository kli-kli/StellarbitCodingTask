package com.coding.stellarbit.Transaction;

/**
 * Created by Tigra on 10.08.2017.
 */
public class Transaction {

    private final int id;

    private final int type;

    private final String from;

    private final String to;

    private final int amount;

    private final String signature;

    Transaction(int id, int type, String from, String to, int amount, String signature) {
        this.id = id;
        this.type = type;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.signature = signature;
    }

    public int getId() {
        return id;
    }

    public Integer getType() {
        return type;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getAmount() {
        return amount;
    }

    public String getSignature() {
        return signature;
    }

}
