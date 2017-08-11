package com.coding.stellarbit.BlockChain;

import com.coding.stellarbit.Block.Block;
import com.coding.stellarbit.Transaction.TransactionType;

import java.util.List;

/**
 * Created by Tigra on 10.08.2017.
 */
public class BlockChain {

    BlockTree blockTree = new BlockTree();

    public List<Block> getBlockChain() {
        return blockTree.getBlockChain();
    }

    public boolean validateBlock(Block block) {
        if (hasAtLeastOneTransaction(block) && idIsNotUsed(block.getId())) {
            return true;
        }
        return false;
    }

    public void addBlock(Integer parentBlockId, Block block) {
        if (block == null) throw new IllegalStateException("Block cant be null when adding to Chain");
        if (parentBlockId == null) {
            if (isRootEmpty() && !doBlockHaveTransfers(block)) {
                blockTree.addRootBlock(block);
            }
            return;
        }
        if (!isParentRefersToNotExistId(parentBlockId)
                && !isAddingBlockWillLeadToNegativeBalance(parentBlockId, block)) {
            blockTree.addBlock(parentBlockId, block);
        }
    }

    private boolean doBlockHaveTransfers(Block block) {
        return block.getListOfTransactions().stream()
                .anyMatch(transaction -> transaction.getType()
                        == TransactionType.MONEY_TRANSFER.getId());
    }

    private boolean isRootEmpty() {
        return blockTree.isRootEmpty();
    }

    public int getBalance(String account) {
        if (account == null || account.length() < 2 || account.length() > 10)
            throw new IllegalStateException("Account cant be null or less then 2 or more then 10 symbols");
        return blockTree.getBalance(account);
    }

    private boolean isAddingBlockWillLeadToNegativeBalance(int parentBlockId, Block block) {
        return blockTree.isAddingBlockWillLeadToNegativeBalance(parentBlockId, block);
    }

    private boolean isParentRefersToNotExistId(int parentBlockId) {
        return blockTree.isParentRefersToNotExistId(parentBlockId);
    }

    private boolean idIsNotUsed(int id) {
        return blockTree.idIsNotUsed(id);
    }

    private boolean hasAtLeastOneTransaction(Block block) {
        return block.getListOfTransactions().size() > 0;
    }
}
