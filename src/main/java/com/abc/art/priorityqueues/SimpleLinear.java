package com.abc.art.priorityqueues;

import com.abc.art.ErrorDebug;
import com.abc.art.stack.EmptyException;
import com.abc.art.stack.Stack;

@ErrorDebug
public class SimpleLinear<T> implements PQueue<T> {

    final int range;
    //Bin A Bin is a pool that holds arbitrary items
    Stack<T>[] pqueue;

    public SimpleLinear(int myRange) {
        range = myRange;
        pqueue = (Stack<T>[]) new Stack[range];
        for (int i = 0; i < pqueue.length; i++){
            pqueue[i] = new Stack();
        }
    }
    @Override
    public void add(T item, int key) {
        pqueue[key].push(item);
    }

    @Override
    public T removeMin() {
        for (int i = 0; i < range; i++) {
            T item=null;
            try {
                item = pqueue[i].pop();
            }catch (EmptyException e){
            }
            if (item != null) {
                return item;
            }
        }
        return null;
    }
}
