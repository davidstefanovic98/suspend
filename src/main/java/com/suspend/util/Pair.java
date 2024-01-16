package com.suspend.util;

public class Pair<A, B> {

    private A first;
    private B second;

    public Pair() {
    }

    public Pair(A first) {
        this.first = first;
    }

    public Pair(A first, B second) {
        this(first);
        this.second = second;
    }

    public A getFirst() {
        return first;
    }

    public Pair<A, B> setFirst(A first) {
        this.first = first;
        return this;
    }

    public B getSecond() {
        return second;
    }

    public Pair<A, B> setSecond(B second) {
        this.second = second;
        return this;
    }

    @Override
    public String toString() {
        return "Pair{" + "first=" + first + ", second=" + second + '}';
    }
}
