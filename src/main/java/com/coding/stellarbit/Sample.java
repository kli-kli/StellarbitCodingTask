package com.coding.stellarbit;

import com.coding.stellarbit.Block.Block;
import com.coding.stellarbit.BlockChain.BlockChain;
import com.coding.stellarbit.Transaction.Transaction;
import com.coding.stellarbit.Transaction.TransactionBuilder;

/**
 * Created by Tigra on 11.08.2017.
 */
public class Sample {

    public static void main(String ...args) {
        Transaction transaction = TransactionBuilder
                .newBuilder()
                .id(1)
                .emission()
                .to("Bob")
                .amount(100)
                .build();

        Block block = new Block(1);
        block.addTransaction(transaction);

        BlockChain blockChain = new BlockChain();
        blockChain.addBlock(null, block);

        // bob transfers 40 coins to alice
        Transaction trx = TransactionBuilder.newBuilder()
                .id(2)
                .transfer()
                .from("Bob")
                .to("Alice")
                .amount(40)
                .build();

        Block block2 = new Block(2);
        block2.addTransaction(trx);
        blockChain.addBlock(1, block2);
        System.out.println("Alice has: " + blockChain.getBalance("Alice"));
        System.out.println("Bob has: " + blockChain.getBalance("Bob"));
    }
}
