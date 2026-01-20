package com.hypixel.hytale.function.consumer;

/**
 * TriConsumer - Functional interface for three-argument consumer.
 */
@FunctionalInterface
public interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v);
}
