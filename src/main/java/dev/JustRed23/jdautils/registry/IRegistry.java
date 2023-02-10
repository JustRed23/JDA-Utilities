package dev.JustRed23.jdautils.registry;

interface IRegistry<T> {
    void register(T object);
    void unregister(T object);
    default void freeze() {}
    default void unfreeze() {}
    void destroy();
}
