package com.abc.art.counting.combining;

import com.abc.art.mutalexcute.ThreadId;
import com.abc.art.stack.EliminationBackoffStack;
import com.abc.art.stack.LockFreeStack;
import com.abc.art.stack.Stack;
//import java.util.Stack;

public class CombiningTree {
    Node[] leaf;

    public CombiningTree(int width) {
        Node[] nodes = new Node[2 * width - 1];
        nodes[0] = new Node();
        for (int i = 1; i < nodes.length; i++) {
            nodes[i] = new Node(nodes[(i - 1) / 2]);
        }
        leaf = new Node[width];
        for (int i = 0; i < leaf.length; i++) {
            leaf[i] = nodes[nodes.length - i - 1];
        }
    }

    public int getAndIncrement() {
        //LockFreeStack<Node> stack = new LockFreeStack<Node>();
        //LockFreeStack<Node> stack = new LockFreeStack();
        Stack<Node> stack = new Stack();
        Node myLeaf = leaf[ThreadId.get() / 2];
        Node node = myLeaf;
        // precombining phase
        while (node.precombine()) {
            node = node.parent;
        }
        Node stop = node;
        // combining phase
        int combined = 1;
        for (node = myLeaf; node != stop; node = node.parent) {
            combined = node.combine(combined);
            stack.push(node);
        }
        // operation phase
        int prior = stop.op(combined);
        // distribution phase
        while (!stack.empty()) {
            node = stack.pop();
            node.distribute(prior);
        }
        return prior;
    }
}
