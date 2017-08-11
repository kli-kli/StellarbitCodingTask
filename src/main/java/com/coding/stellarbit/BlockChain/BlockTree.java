package com.coding.stellarbit.BlockChain;

import com.coding.stellarbit.Block.Block;
import com.coding.stellarbit.Transaction.Transaction;
import com.coding.stellarbit.Transaction.TransactionType;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * Created by Tigra on 11.08.2017.
 */
class BlockTree {

    private Node root;

    boolean idIsNotUsed(int id) {
        return !findNodeByBlockId(id, root).isPresent();
    }

    /**
     * return node by block Id
     *
     * @param id
     * @param node
     * @return true if block exists
     */
    private Optional<Node> findNodeByBlockId(int id, Node node) {
        if (node.getBlock().getId() == id) return Optional.of(node);
        for (Node forNode : node.getDescendants()) {
            Optional<Node> result = findNodeByBlockId(id, forNode);
            if (result.isPresent()) return result;
        }
        return Optional.empty();
    }

    void addBlock(int parentBlockId, Block block) {
        Optional<Node> node = findNodeByBlockId(parentBlockId, root);
        if (node.isPresent())
            node.get().addDescendant(block);
    }

    boolean isAddingBlockWillLeadToNegativeBalance(int parentBlockId, Block block) {
        List<Block> blocksToThis = getChainToThisBlock(parentBlockId);
        for (Transaction transaction : block.getListOfTransactions()) {
            if (transaction.getType() == TransactionType.MONEY_EMISSION.getId()) continue;
            boolean invalidTransaction = isAddingTransactionWillLeadToNegBalance(transaction, blocksToThis, block);
            if (invalidTransaction) return true;
        }
        return false;
    }

    private boolean isAddingTransactionWillLeadToNegBalance(Transaction transaction, List<Block> blocks, Block block) {
        List<Block> blocksWithNew = new LinkedList<>(blocks);
        blocksWithNew.add(block);
        String from = transaction.getFrom();
        int sum = getSumOfBlocksByAccount(from, blocksWithNew);
        return sum < 0;
    }

    private List<Block> getChainToThisBlock(int blockId) {
        if (root == null) return Collections.emptyList();
        List<Block> result = new LinkedList<>();
        result.add(root.getBlock());
        getChainToThisBlockRec(root, blockId, result);
        return result;
    }

    private boolean getChainToThisBlockRec(Node node, int blockId, List<Block> blocks) {
        if (blockId == node.getBlock().getId())
            return true;
        for (Node descendant : node.getDescendants()) {
            if (getChainToThisBlockRec(descendant, blockId, blocks)) {
                blocks.add(node.getBlock());
                break;
            }
        }
        return false;
    }


    boolean isParentRefersToNotExistId(int parentBlockId) {
        return idIsNotUsed(parentBlockId);
    }

    //TODO
    int getBalance(String account) {
        List<Block> list = getBlockChain();
        int result = getSumOfBlocksByAccount(account, list);
        return result;
    }

    private int getSumOfBlocksByAccount(String account, List<Block> list) {
        return list.stream()
                .flatMap(block -> block.getListOfTransactions().stream())
                .filter(getTransactionPredicateToAccountFromTo(account))
                .mapToInt(getTransactionToIntFunction(account))
                .sum();
    }

    private ToIntFunction<Transaction> getTransactionToIntFunction(String account) {
        return transaction -> {
            if (account.equals(transaction.getFrom()))
                return -transaction.getAmount();
            else
                return transaction.getAmount();
        };
    }

    private Predicate<Transaction> getTransactionPredicateToAccountFromTo(String account) {
        return transaction -> account.equals(transaction.getTo()) ||
                (transaction.getFrom() != null && account.equals(transaction.getFrom()));

    }


    boolean isRootEmpty() {
        return root == null;
    }

    void addRootBlock(Block block) {
        root = new Node(null, block);
    }

    public List<Block> getBlockChain() {
        if (isRootEmpty()) return Collections.emptyList();
        List<List<Node>> listOfLists = new ArrayList<>();
        List<Node> listOfNodes = new LinkedList<>();
        listOfLists.add(listOfNodes);
        doListWithAllChains(listOfLists, listOfNodes, root);
        Optional<List<Node>> listOptional = listOfLists.stream().max(getListComparator());
        return listOptional.get().stream().map(node -> node.getBlock()).collect(Collectors.toList());
    }

    private Comparator<List<Node>> getListComparator() {
        return (list1, list2) -> {
            if (list1.size() > list2.size()) {
                return 1;
            } else if (list1.size() == list2.size()) {
                return 0;
            } else {
                return -1;
            }
        };
    }

    private void doListWithAllChains(List<List<Node>> listOfLists, List<Node> list, Node node) {
        list.add(node);
        boolean flagOfFirst = true;
        for (Node descendant : node.getDescendants()) {
            if (flagOfFirst) {
                doListWithAllChains(listOfLists, list, descendant);
                flagOfFirst = false;
                break;
            }
            List<Node> newBranch = new LinkedList<>(list);
            listOfLists.add(newBranch);
            doListWithAllChains(listOfLists, newBranch, descendant);
        }
    }

    class Node {
        Node ancestor;
        Block block;
        List<Node> descendants = new LinkedList<>();

        Node(Node ancestor, Block block) {
            this.ancestor = ancestor;
            this.block = block;
        }

        public Block getBlock() {
            return block;
        }

        public Node getAncestor() {
            return ancestor;
        }

        public void addDescendant(Block block) {
            descendants.add(new Node(this, block));
        }

        public List<Node> getDescendants() {
            return descendants;
        }
    }
}
