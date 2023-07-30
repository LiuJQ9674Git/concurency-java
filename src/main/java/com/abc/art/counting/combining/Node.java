package com.abc.art.counting.combining;

public class Node {
    enum CStatus {IDLE, FIRST, SECOND, RESULT, ROOT};
    boolean locked;
    CStatus cStatus;
    int firstValue, secondValue;
    int result;
    Node parent;

     Node() {
        cStatus = CStatus.ROOT;
        locked = false;
    }

     Node(Node myParent) {
        parent = myParent;
        cStatus = CStatus.IDLE;
        locked = false;
    }

    synchronized boolean precombine() {
        while (locked) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        switch (cStatus) {
            case IDLE:
                cStatus = CStatus.FIRST;
                return true;
            case FIRST:
                locked = true;
                cStatus = CStatus.SECOND;
                return false;
            case ROOT:
                return false;
            default:
                throw new PanicException("unexpected Node state" + cStatus);
        }
    }

     synchronized int combine(int combined) {
        while (locked) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        locked = true;
        firstValue = combined;
        switch (cStatus) {
            case FIRST:
                return firstValue;
            case SECOND:
                return firstValue + secondValue;
            default:
                throw new PanicException("unexpected Node state " + cStatus);
        }
    }

    synchronized int op(int combined) {
        switch (cStatus) {
            case ROOT:
                int prior = result;
                result += combined;
                return prior;
            case SECOND:
                secondValue = combined;
                locked = false;
                notifyAll(); // wake up waiting threads
                while (cStatus != CStatus.RESULT) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                locked = false;
                notifyAll();
                cStatus = CStatus.IDLE;
                return result;
            default:
                throw new PanicException("unexpected Node state");
        }
    }

    synchronized void distribute(int prior) {
        switch (cStatus) {
            case FIRST:
                cStatus = CStatus.IDLE;
                locked = false;
                break;
            case SECOND:
                result = prior + firstValue;
                cStatus = CStatus.RESULT;
                break;
            default:
                throw new PanicException("unexpected Node state");
        }
        notifyAll();
    }
}
