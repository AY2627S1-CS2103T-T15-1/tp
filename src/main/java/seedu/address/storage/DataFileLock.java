package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Lets only one copy of Cindy use a data file at a time, so that one copy cannot overwrite the changes saved by
 * another. The lock is held on a separate file next to the data file, which is left in place after the lock is
 * released, as deleting it could let two copies hold locks on different files.
 */
public class DataFileLock {

    private final Path lockFilePath;
    private FileLock lock;

    /**
     * Creates a lock for the data file at {@code dataFilePath}. The lock is not taken until {@link #tryAcquire()}.
     */
    public DataFileLock(Path dataFilePath) {
        requireNonNull(dataFilePath);
        lockFilePath = dataFilePath.resolveSibling(dataFilePath.getFileName() + ".lock");
    }

    public Path getLockFilePath() {
        return lockFilePath;
    }

    /**
     * Takes the lock if no other copy of Cindy holds it. Succeeds at once if this object already holds the lock.
     *
     * @return true if this object now holds the lock, or false if another copy of Cindy holds it.
     * @throws IOException if the lock file cannot be created or locked.
     */
    public boolean tryAcquire() throws IOException {
        if (isHeld()) {
            return true;
        }
        Path parentDir = lockFilePath.getParent();
        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }
        FileChannel channel = FileChannel.open(lockFilePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        try {
            lock = channel.tryLock();
        } catch (OverlappingFileLockException e) {
            // Another DataFileLock in this program already holds the lock
        } finally {
            if (lock == null) {
                channel.close();
            }
        }
        return isHeld();
    }

    public boolean isHeld() {
        return lock != null;
    }

    /**
     * Releases the lock if this object holds it, so that another copy of Cindy can use the data file.
     */
    public void release() throws IOException {
        if (!isHeld()) {
            return;
        }
        try {
            // Closing the channel also releases the lock
            lock.channel().close();
        } finally {
            lock = null;
        }
    }
}
