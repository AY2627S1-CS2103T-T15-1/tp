package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.testutil.AppClassLoader;

public class AppPathsTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void getHomeDirectory_jarCodeSource_usesContainingFolder() throws Exception {
        Path jar = Files.createFile(temporaryFolder.resolve("app 100%.jar"));
        Class<?> paths = new AppClassLoader(jar.toUri().toURL()).loadClass(AppPaths.class.getName());

        Path home = (Path) paths.getMethod("getHomeDirectory").invoke(null);

        assertTrue(Files.isSameFile(temporaryFolder, home));
        assertTrue(home.isAbsolute());
    }

    @Test
    public void getHomeDirectory_classesDirectory_usesWorkingDirectory() throws Exception {
        Class<?> paths = new AppClassLoader(temporaryFolder.toUri().toURL()).loadClass(AppPaths.class.getName());

        Path home = (Path) paths.getMethod("getHomeDirectory").invoke(null);

        assertEquals(Path.of("").toAbsolutePath().normalize(), home);
    }

    @Test
    @SuppressWarnings("deprecation") // A deliberately invalid URI cannot be constructed through URL.of(URI, ...).
    public void getHomeDirectory_invalidCodeSourceUri_failsInsteadOfUsingAnotherFolder() throws Exception {
        // URL permits a literal space, whereas conversion to URI rejects it.
        URL validLocation = URI.create("file:/invalid%20folder/app.jar").toURL();
        URL invalidLocation = new URL(validLocation.toString().replace("%20", " "));
        Class<?> paths = new AppClassLoader(invalidLocation).loadClass(AppPaths.class.getName());

        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
            paths.getMethod("getHomeDirectory").invoke(null);
        });

        assertInstanceOf(IllegalStateException.class, exception.getCause());
        assertInstanceOf(URISyntaxException.class, exception.getCause().getCause());
    }

    @Test
    public void getHomeDirectory_packagedJar_usesJarFolderInsteadOfWorkingDirectory() throws Exception {
        // Keep a redundant path segment to exercise different spellings of the same folder on every platform.
        Path home = Files.createDirectories(temporaryFolder.resolve(".").resolve("app folder 100%"));
        Path workingDirectory = Files.createDirectories(temporaryFolder.resolve("launch folder"));
        Path jar = home.resolve("cindy.jar");
        try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(jar))) {
            addClass(output, "seedu/address/commons/util/AppPaths.class");
            addClass(output, "seedu/address/commons/core/LogsCenter.class");
            addClass(output, "seedu/address/commons/util/AppPathsTest$HomeProbe.class");
        }

        Process process = new ProcessBuilder(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                "-cp", jar.toString(), HomeProbe.class.getName())
                .directory(workingDirectory.toFile()).redirectErrorStream(true).start();
        try {
            assertTrue(process.waitFor(20, TimeUnit.SECONDS), "Path probe timed out");
            String result = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).strip();
            assertEquals(0, process.exitValue(), result);
            assertTrue(Files.isSameFile(home, Path.of(result)),
                    "Expected JAR folder " + home + " but the probe reported " + result);
            assertTrue(Files.exists(home.resolve("cindy.log.0")), "Log must be created beside the JAR");
            try (Stream<Path> files = Files.list(workingDirectory)) {
                assertEquals(0L, files.count(), "Launching must not create files in the working directory");
            }
        } finally {
            process.destroyForcibly();
        }
    }

    private void addClass(JarOutputStream output, String resource) throws Exception {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resource)) {
            assertNotNull(input, "Missing compiled class: " + resource);
            output.putNextEntry(new JarEntry(resource));
            input.transferTo(output);
            output.closeEntry();
        }
    }

    /**
     * Runs in a separate JVM so its code source is the test JAR rather than Gradle's classes directory.
     */
    public static class HomeProbe {
        public static void main(String[] args) throws Exception {
            Class<?> paths = Class.forName("seedu.address.commons.util.AppPaths");
            System.out.println(paths.getMethod("getHomeDirectory").invoke(null));
            Class<?> logs = Class.forName("seedu.address.commons.core.LogsCenter");
            logs.getMethod("getLogger", String.class).invoke(null, "probe");
        }
    }
}
