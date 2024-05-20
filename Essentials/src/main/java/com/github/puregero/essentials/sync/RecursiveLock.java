package com.github.puregero.essentials.sync;

import java.io.Closeable;

/**
 * Prevent a method from being called recursively.
 * Thread-safe, only uses the local thread's stack.
 * @author PureGero
 */
public class RecursiveLock {

    private final AutoUnlock autoUnlock = new AutoUnlock();
    private final ThreadLocal<Boolean> locked = ThreadLocal.withInitial(() -> false);

    public AutoUnlock lock() {
        if (isLocked()) {
            throw new IllegalStateException("Recursive call detected");
        }
        locked.set(true);
        return autoUnlock;
    }

    public void unlock() {
        locked.set(false);
    }

    public boolean isLocked() {
        return Boolean.TRUE.equals(locked.get());
    }

    public class AutoUnlock implements Closeable {
        public void close() {
            unlock();
        }
    }

}
