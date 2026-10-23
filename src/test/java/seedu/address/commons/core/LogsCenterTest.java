package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.testutil.AppClassLoader;

public class LogsCenterTest {
    @TempDir
    public Path temporaryFolder;

    private Logger baseLogger;
    private Handler[] originalHandlers;
    private Level originalLevel;
    private boolean wasUsingParentHandlers;

    @BeforeEach
    public void saveLoggerConfiguration() {
        baseLogger = Logger.getLogger("ab3");
        originalHandlers = baseLogger.getHandlers();
        originalLevel = baseLogger.getLevel();
        wasUsingParentHandlers = baseLogger.getUseParentHandlers();
    }

    @AfterEach
    public void restoreLoggerConfiguration() {
        for (Handler handler : baseLogger.getHandlers()) {
            baseLogger.removeHandler(handler);
            handler.close();
        }
        for (Handler handler : originalHandlers) {
            baseLogger.addHandler(handler);
        }
        baseLogger.setLevel(originalLevel);
        baseLogger.setUseParentHandlers(wasUsingParentHandlers);
    }

    @Test
    public void getLogger_logsToHomeFile_preservesExistingContentAndFiltersFineMessages() throws Exception {
        Path home = Files.createDirectories(temporaryFolder.resolve("log folder 100%"));
        Path log = home.resolve("cindy.log.0");
        Files.writeString(log, "previous log content\n");
        Class<?> logs = loadLogsCenter(home);
        Logger logger = (Logger) logs.getMethod("getLogger", String.class).invoke(null, "coverage.file");

        logger.info("visible coverage message");
        logger.fine("filtered coverage message");
        for (Handler handler : baseLogger.getHandlers()) {
            handler.flush();
        }

        String content = Files.readString(log);
        assertTrue(content.contains("previous log content"));
        assertTrue(content.contains("visible coverage message"));
        assertFalse(content.contains("filtered coverage message"));
    }

    @Test
    public void getLogger_existingChildHandler_removesHandlerAndUsesParent() throws Exception {
        Class<?> logs = loadLogsCenter(temporaryFolder);
        Logger child = Logger.getLogger("ab3.coverage.child");
        Handler staleHandler = new ConsoleHandler();
        child.addHandler(staleHandler);
        child.setUseParentHandlers(false);
        try {
            Logger logger = (Logger) logs.getMethod("getLogger", String.class).invoke(null, "coverage.child");

            assertEquals(0, logger.getHandlers().length);
            assertTrue(logger.getUseParentHandlers());
            assertEquals(baseLogger, logger.getParent());
        } finally {
            child.removeHandler(staleHandler);
            staleHandler.close();
        }
    }

    @Test
    public void getLogger_class_usesSimpleClassName() throws Exception {
        Class<?> logs = loadLogsCenter(temporaryFolder);
        Method method = logs.getMethod("getLogger", Class.class);
        Logger logger = (Logger) method.invoke(null, LogsCenterTest.class);

        assertEquals("ab3.LogsCenterTest", logger.getName());
    }

    @Test
    public void getLogger_nullClass_throwsNullPointerException() throws Exception {
        Class<?> logs = loadLogsCenter(temporaryFolder);
        Method method = logs.getMethod("getLogger", Class.class);
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
            method.invoke(null, new Object[] {null});
        });
        assertInstanceOf(NullPointerException.class, exception.getCause());
    }

    @Test
    public void getLogger_logFileIsDirectory_initializesAndContinuesConsoleLogging() throws Exception {
        Files.createDirectory(temporaryFolder.resolve("cindy.log.0"));
        PrintStream originalError = System.err;
        ByteArrayOutputStream console = new ByteArrayOutputStream();
        try (PrintStream capturedError = new PrintStream(console, true, StandardCharsets.UTF_8)) {
            System.setErr(capturedError);
            Class<?> logs = loadLogsCenter(temporaryFolder);
            Logger logger = (Logger) logs.getMethod("getLogger", String.class).invoke(null, "coverage.failure");
            logger.warning("console logging still works");

            assertTrue(console.toString(StandardCharsets.UTF_8).contains("console logging still works"));
            assertTrue(Files.isDirectory(temporaryFolder.resolve("cindy.log.0")));
        } finally {
            System.setErr(originalError);
        }
    }

    private Class<?> loadLogsCenter(Path home) throws Exception {
        Path jar = Files.createFile(home.resolve("app.jar"));
        return new AppClassLoader(jar.toUri().toURL()).loadClass(LogsCenter.class.getName());
    }
}
