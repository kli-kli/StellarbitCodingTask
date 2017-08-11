package com.coding.stellarbit.Transaction;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TransactionBuilder {

    public static IdStep newBuilder() {
        return new Steps();
    }

    private TransactionBuilder() {
    }

    public static String calculateMD5(int id, int type, String from, String to, int amount) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String signature = new StringBuilder()
                .append(id)
                .append(":")
                .append(type)
                .append(":")
                .append(from)
                .append(":")
                .append(to)
                .append(":")
                .append(amount)
                .toString();
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(signature.getBytes());
        BigInteger number = new BigInteger(1, messageDigest);
        String hashtext = number.toString(16);
        return hashtext;
    }

    public static interface IdStep {
        /**
         * @param id unique identifier for this Transaction
         * @return ServerStep
         */
        TypeStep id(Integer id);
    }

    public static interface TypeStep {
        /**
         * type of transaction emission
         *
         * @return BuildStep
         */
        public ToStep emission();

        /**
         * type of transaction transfer
         *
         * @return CredentialsStep
         */
        public FromStep transfer();
    }

    public static interface FromStep {
        /**
         * set from where transaction comes
         *
         * @return BuildStep
         */
        public ToStep from(String from);
    }

    public static interface ToStep {
        /**
         * set destination of transaction
         *
         * @return ToStep
         */
        public AmountStep to(String to);
    }

    public static interface AmountStep {
        /**
         * set destination of transaction
         *
         * @return ToStep
         */
        public BuildStep amount(Integer amount);
    }

    public static interface BuildStep {
        /**
         * set amount of transaction
         *
         * @return ToStep
         */
        public Transaction build();
    }

    private static class Steps implements IdStep, TypeStep, FromStep, ToStep, AmountStep, BuildStep {

        private Integer id;
        private Integer type;
        private String from;
        private String to;
        private Integer amount;

        @Override
        public TypeStep id(Integer id) {
            if (id == null) throw new RuntimeException("Transaction cant be created without id");
            this.id = id;
            return this;
        }

        @Override
        public ToStep emission() {
            this.type = TransactionType.MONEY_EMISSION.getId();
            return this;
        }

        @Override
        public FromStep transfer() {
            this.type = TransactionType.MONEY_TRANSFER.getId();
            return this;
        }

        @Override
        public ToStep from(String from) {
            if (from == null) throw new RuntimeException("Transfer transaction cant be created without FROM property");
            if (from.length() < 2 || from.length() > 10)
                throw new RuntimeException("Transfer transaction can be created with length of propertie FROM between 2 till 10");
            this.from = from;
            return this;
        }

        @Override
        public AmountStep to(String to) {
            if (to == null) throw new RuntimeException("Transaction cant be created without TO property");
            if (to.length() < 2 || to.length() > 10)
                throw new RuntimeException("Transfer transaction can be created with length of propertie TO between 2 till 10");
            this.to = to;
            return this;
        }

        @Override
        public BuildStep amount(Integer amount) {
            if (amount == null) throw new RuntimeException("Transaction cant be created without AMOUNT property");
            if (amount < 0) throw new RuntimeException("Transaction cant be created with negative AMOUNT property");
            this.amount = amount;
            return this;
        }

        @Override
        public Transaction build() {
            String signature = doSignature();
            return new Transaction(id, type, from, to, amount, signature);
        }

        private String doSignature() {
            String signature = null;
            try {
                signature = TransactionBuilder.calculateMD5(id, type, from, to, amount);
            } catch (Exception e) {
                throw new RuntimeException("Exception when do signature for transaction: " + e.getCause());
            }
            if (signature.length() != 32)
                if (amount == null) throw new RuntimeException("Transaction signature should has length 32");
            return signature;
        }
    }
}