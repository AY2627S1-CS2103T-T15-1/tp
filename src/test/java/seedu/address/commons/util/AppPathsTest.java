package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class AppPathsTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void getHomeDirectory_packagedJar_usesJarFolderInsteadOfWorkingDirectory() throws Exception {
        Path home = Files.createDirectories(temporaryFolder.resolve("app folder 100%"));
        Path workingDirectory = Files.createDirectories(temporaryFolder.resolve("launch folder"));
        Path jar = home.resolve("addressbook.jar");
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
            assertEquals(home.toString(), result);
            assertTrue(Files.exists(home.resolve("addressbook.log.0")), "Log must be created beside the JAR");
            try (var files = Files.list(workingDirectory)) {
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
