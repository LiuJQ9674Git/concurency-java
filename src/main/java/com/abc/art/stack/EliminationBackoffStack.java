package com.abc.art.stack;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class EliminationBackoffStack<T> extends LockFreeStack<T> {
    static final int capacity = Config.capacity;
    EliminationArray<T> eliminationArray = new EliminationArray<T>(capacity);

    static ThreadLocal<RangePolicy> policy = new ThreadLocal<RangePolicy>() {
        protected synchronized RangePolicy initialValue() {
            return new RangePolicy();
        }
    };

    public void push(T value) {
        Node node = new Node(value);
        while (true) {
            if (tryPush(node)) {
                return;
            } else{
                eliminatedPush(value);
            }
        }
    }

    public void eliminatedPush(T value){
        RangePolicy rangePolicy = policy.get();
        try {
            T otherValue = eliminationArray.visit(value, rangePolicy.getRange());
            if (otherValue == null) {
                rangePolicy.recordEliminationSuccessPush(this);
                return;
            }else {
                rangePolicy.recordEliminationFailPush(this);
            }
        } catch (TimeoutException ex) {
            rangePolicy.recordEliminationTimeout();
        }
    }
    public T eliminatedPop(){
        RangePolicy rangePolicy = policy.get();
        try {
            T otherValue = eliminationArray.visit(null, rangePolicy.getRange());
            if (otherValue != null) {
                rangePolicy.recordEliminationSuccessPop(this);
                return otherValue;
            }else{
                rangePolicy.recordEliminationFailPop(this);
            }
        } catch (Exception ex) {
            rangePolicy.recordEliminationTimeout();
        }
        //return null;//eliminatedPop();
        return eliminatedPop();
    }
    public T pop() throws EmptyException {
        while (true) {
            Node returnNode =null;
            try{
                returnNode=tryPop();
            }catch (Exception e){
                //System.err.println("pop null");
            }
            if (returnNode != null) {
                return (T) returnNode.value;
            } else {
                return eliminatedPop();
            }
        }
    }

    public boolean empty() {
        //return (super.empty() && (pushInteger.get()==0));
        return super.empty() &&policy.get().empty();
    }
}
