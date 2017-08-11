package com.coding.stellarbit.BlockChain;

import com.coding.stellarbit.Block.Block;
import com.coding.stellarbit.Transaction.Transaction;
import com.coding.stellarbit.Transaction.TransactionType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Tigra on 11.08.2017.
 */
public class BlockChainTest {

    BlockChain blockChain;

    @Before
    public void before() {
        blockChain = new BlockChain();
    }

    @Test
    public void testGetBlockChainWhenChainIsEmpty() {
        Assert.assertTrue(blockChain.getBlockChain().isEmpty());
    }

    @Test
    public void testAddFirstBlock() {
        Block block = new Block(1);
        blockChain.addBlock(null, block);
        Assert.assertEquals(1, blockChain.getBlockChain().size());
    }

    @Test
    public void testAddBlockWithoutParentWhenRootIsExist() {
        Block block = new Block(1);
        blockChain.addBlock(null, block);
        Block invalidBlock = new Block(2);
        blockChain.addBlock(null, invalidBlock);
        Assert.assertEquals(1, blockChain.getBlockChain().size());
    }

    @Test
    public void testAddChainOfBlocks() {
        Block block = new Block(1);
        blockChain.addBlock(null, block);
        Block block2 = new Block(2);
        blockChain.addBlock(1, block2);
        Block block3 = new Block(3);
        blockChain.addBlock(2, block3);
        Assert.assertEquals(3, blockChain.getBlockChain().size());
    }

    @Test
    public void testAddChainOfBlocks2() {
        Block block = new Block(1);
        blockChain.addBlock(null, block);
        Block block2 = new Block(2);
        blockChain.addBlock(1, block2);
        Block block3 = new Block(3);
        blockChain.addBlock(2, block3);
        Block block4 = new Block(4);
        blockChain.addBlock(2, block4);
        Assert.assertEquals(3, blockChain.getBlockChain().size());
    }

    @Test
    public void testAddBlockWithTheSameId() {
        Block block = new Block(1);
        blockChain.addBlock(null, block);
        Block block2 = new Block(2);
        blockChain.addBlock(1, block2);
        Block block3 = new Block(2);
        blockChain.addBlock(1, block3);
        Assert.assertEquals(2, blockChain.getBlockChain().size());
    }

    @Test(expected = IllegalStateException.class)
    public void testAddNullBlock() {
        Block block = null;
        blockChain.addBlock(null, block);
    }

    @Test
    public void testGetBalanceAfterOneEmission() {
        Transaction transaction = Mockito.mock(Transaction.class);
        Mockito.when(transaction.getType()).thenReturn(TransactionType.MONEY_EMISSION.getId());
        Mockito.when(transaction.getTo()).thenReturn("Bob");
        Mockito.when(transaction.getAmount()).thenReturn(100);
        Block block = Mockito.mock(Block.class);
        Mockito.when(block.getId()).thenReturn(1);
        Mockito.when(block.getListOfTransactions()).thenReturn(Arrays.asList(transaction));
        blockChain.addBlock(null, block);
        int actualBalance = blockChain.getBalance("Bob");
        Assert.assertEquals(100, actualBalance);
    }

    @Test
    public void testInitialBlockHasTransfers() {
        Transaction transaction = Mockito.mock(Transaction.class);
        Mockito.when(transaction.getType()).thenReturn(TransactionType.MONEY_TRANSFER.getId());
        Mockito.when(transaction.getFrom()).thenReturn("Bob");
        Mockito.when(transaction.getTo()).thenReturn("Alisa");
        Mockito.when(transaction.getAmount()).thenReturn(30);

        Block block = Mockito.mock(Block.class);
        Mockito.when(block.getId()).thenReturn(1);
        Mockito.when(block.getListOfTransactions()).thenReturn(Arrays.asList(transaction));
        blockChain.addBlock(null, block);

        List<Block> actual = blockChain.getBlockChain();
        Assert.assertTrue(actual.isEmpty());
    }

    @Test
    public void testAddBlockWithLeadNegativeBalance() {
        Transaction transaction = Mockito.mock(Transaction.class);
        Mockito.when(transaction.getType()).thenReturn(TransactionType.MONEY_EMISSION.getId());
        Mockito.when(transaction.getTo()).thenReturn("Bob");
        Mockito.when(transaction.getAmount()).thenReturn(100);
        Block block = Mockito.mock(Block.class);
        Mockito.when(block.getId()).thenReturn(1);
        Mockito.when(block.getListOfTransactions()).thenReturn(Arrays.asList(transaction));
        blockChain.addBlock(null, block);


        Transaction transaction2 = Mockito.mock(Transaction.class);
        Mockito.when(transaction2.getType()).thenReturn(TransactionType.MONEY_TRANSFER.getId());
        Mockito.when(transaction2.getFrom()).thenReturn("Bob");
        Mockito.when(transaction2.getTo()).thenReturn("Alisa");
        Mockito.when(transaction2.getAmount()).thenReturn(110);

        Block block2 = Mockito.mock(Block.class);
        Mockito.when(block2.getId()).thenReturn(2);
        Mockito.when(block2.getListOfTransactions()).thenReturn(Arrays.asList(transaction2));
        blockChain.addBlock(block.getId(), block2);

        int actualBalanceOfBob = blockChain.getBalance("Bob");
        Assert.assertEquals(100, actualBalanceOfBob);

        int actualBalanceOfAlice = blockChain.getBalance("Alice");
        Assert.assertEquals(0, actualBalanceOfAlice);

    }

    @Test
    public void testGetBalanceAfterOneTransferTransaction() {
        Transaction transaction = Mockito.mock(Transaction.class);
        Mockito.when(transaction.getType()).thenReturn(TransactionType.MONEY_EMISSION.getId());
        Mockito.when(transaction.getTo()).thenReturn("Bob");
        Mockito.when(transaction.getAmount()).thenReturn(100);
        Block block = Mockito.mock(Block.class);
        Mockito.when(block.getId()).thenReturn(1);
        Mockito.when(block.getListOfTransactions()).thenReturn(Arrays.asList(transaction));
        blockChain.addBlock(null, block);


        Transaction transaction2 = Mockito.mock(Transaction.class);
        Mockito.when(transaction2.getType()).thenReturn(TransactionType.MONEY_TRANSFER.getId());
        Mockito.when(transaction2.getFrom()).thenReturn("Bob");
        Mockito.when(transaction2.getTo()).thenReturn("Alice");
        Mockito.when(transaction2.getAmount()).thenReturn(70);

        Block block2 = Mockito.mock(Block.class);
        Mockito.when(block2.getId()).thenReturn(2);
        Mockito.when(block2.getListOfTransactions()).thenReturn(Arrays.asList(transaction2));
        blockChain.addBlock(block.getId(), block2);

        int actualBalanceOfBob = blockChain.getBalance("Bob");
        Assert.assertEquals(30, actualBalanceOfBob);

        int actualBalanceOfAlice = blockChain.getBalance("Alice");
        Assert.assertEquals(70, actualBalanceOfAlice);
    }
}
