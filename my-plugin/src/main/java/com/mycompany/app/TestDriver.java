package com.mycompany.app;

/**
 * Created by ca309567 on 22/02/16.
 */
/** tests all contracts of a class T based on a single instance */
public interface TestDriver<T> {
    /** throws an AssertionError is one contract is violated */
    void test(T t);
}

