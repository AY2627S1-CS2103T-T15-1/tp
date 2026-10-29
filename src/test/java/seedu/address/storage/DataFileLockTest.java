package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests that only one {@code DataFileLock} at a time can hold the lock on a data file.
 */
public class DataFileLockTest {

    @TempDir
    public Path testFolder;

    private Path dataFilePath;
    private DataFileLock lock;
    private DataFileLock otherLock;

    @BeforeEach
    public void setUp() {
        dataFilePath = testFolder.resolve("data").resolve("cindy.json");
        lock = new DataFileLock(dataFilePath);
        otherLock = new DataFileLock(dataFilePath);
    }

    @AfterEach
    public void tearDown() throws IOException {
        // Release the locks so that the temporary folder can be deleted
        lock.release();
        otherLock.release();
    }

    @Test
    public void constructor_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DataFileLock(null));
    }

    @Test
    public void getLockFilePath_nextToDataFile() {
        assertEquals(testFolder.resolve("data").resolve("cindy.json.lock"), lock.getLockFilePath());
    }

    @Test
    public void tryAcquire_free_takesLockAndCreatesFolder() throws IOException {
        assertFalse(lock.isHeld());
        assertTrue(lock.tryAcquire());
        assertTrue(lock.isHeld());
        assertTrue(Files.exists(lock.getLockFilePath()));
        assertFalse(Files.exists(dataFilePath));

        // Taking it again does nothing
        assertTrue(lock.tryAcquire());
    }

    @Test
    public void tryAcquire_heldByAnother_returnsFalse() throws IOException {
        lock.tryAcquire();

        assertFalse(otherLock.tryAcquire());
        assertFalse(otherLock.isHeld());
        assertTrue(lock.isHeld());
    }

    @Test
    public void release_held_letsAnotherTakeLock() throws IOException {
        lock.tryAcquire();

        lock.release();

        assertFalse(lock.isHeld());
        assertTrue(Files.exists(lock.getLockFilePath()));
        assertTrue(otherLock.tryAcquire());
    }

    @Test
    public void release_notHeld_doesNothing() throws IOException {
        lock.release();
        assertFalse(lock.isHeld());
    }

    @Test
    public void tryAcquire_folderIsAFile_throwsIoException() throws IOException {
        Files.writeString(testFolder.resolve("data"), "not a folder");
        assertThrows(IOException.class, lock::tryAcquire);
        assertFalse(lock.isHeld());
    }
}
