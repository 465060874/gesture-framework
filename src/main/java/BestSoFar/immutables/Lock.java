package BestSoFar.immutables;

import lombok.Synchronized;

/**
 * User: Sam Wright Date: 28/06/2013 Time: 12:12
 */
public class Lock {
    private static int READERS_REQUIRED_FOR_WRITE = 0; // A writer is also a reader.
    private int readers = 0;
    private Object[] lock = new Object[0];
    private boolean waitingToWrite = false;

    @Synchronized
    public void getReadLock() {
            waitForWriteToEnd();
            readers += 1;
    }

    @Synchronized
    public void releaseReadLock() {
            readers -= 1;
            notifyAll();
    }

    @Synchronized
    public void releaseWriteLock() {
            waitingToWrite = false;
            notifyAll();
    }

    @Synchronized
    public void getWriteLock() {
            waitForWriteToEnd();
            waitingToWrite = true;
            waitToBeAbleToWrite();
    }

    private void waitForWriteToEnd() {
        while (waitingToWrite) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void waitToBeAbleToWrite() {
        while (readers != READERS_REQUIRED_FOR_WRITE) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
