package com.abc.art.priorityqueues;

import com.abc.art.stack.Stack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleTree<T> implements PQueue<T> {
    int range;
    List<TreeNode> leaves;

    TreeNode root;

    public SimpleTree(int logRange) {
        range = (1 << logRange);
        leaves = new ArrayList<TreeNode>(range);
        root = buildTree(range, 0);
    }
    @Override
    public void add(T item, int score) {
        TreeNode node = leaves.get(score);
        node.bin.push(item);
        while(node != root) {
            TreeNode parent = node.parent;
            if (node == parent.left) {
                parent.counter.getAndIncrement();
            }
            node = parent;
        }
    }

    @Override
    public T removeMin() {
        TreeNode node = root;
        while(!node.isLeaf()) {
            if (node.counter.getAndDecrement() > 0 ) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return node.bin.pop();
    }

    private TreeNode buildTree(int logRange,int score){
        TreeNode treeNode=new TreeNode();
        treeNode.bin=new Stack<>();
        for(int i=0;i<logRange;i++) {
            leaves.add(0, treeNode);
        }
        return treeNode;
    }
    public class TreeNode {
        final AtomicInteger counter=new AtomicInteger(0);
        TreeNode parent, right, left;
        Stack<T> bin;
        public boolean isLeaf() {
            return right == null;
        }
    }
}
