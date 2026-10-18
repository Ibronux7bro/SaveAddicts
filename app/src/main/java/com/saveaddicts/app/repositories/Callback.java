package com.saveaddicts.app.repositories;

/**
 * Standard callback for asynchronous repository operations.
 */
public interface Callback<T> {
    void onSuccess(T result);
    void onError(Exception exception);
}
