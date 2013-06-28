package BestSoFar.framework.immutables.helper;

import lombok.Synchronized;

/**
 * User: Sam Wright Date: 28/06/2013 Time: 12:12
 */
public class Lock {
    private static int READERS_REQUIRED_FOR_WRITE = 0; // A writer is also a reader.
    private int readers = 0;
    private Object[] lock = new Object[0];
    private boolean waitingToWrite = false;

    public void getReadLock() {
        synchronized (lock) {
            waitForWriteToEnd();
            readers += 1;
        }
    }

    public void releaseReadLock() {
        synchronized (lock) {
            readers -= 1;
            lock.notifyAll();
        }
    }

    public void releaseWriteLock() {
        synchronized (lock) {
            waitingToWrite = false;
            lock.notifyAll();
        }
    }

    @Synchronized
    public void getWriteLock() {
        synchronized (lock) {
            waitForWriteToEnd();
            waitingToWrite = true;
            waitToBeAbleToWrite();
        }
    }

    private void waitForWriteToEnd() {
        while (waitingToWrite) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void waitToBeAbleToWrite() {
        while (readers != READERS_REQUIRED_FOR_WRITE) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
