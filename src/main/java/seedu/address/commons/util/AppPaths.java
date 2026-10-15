package seedu.address.commons.util;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Locates the application's home folder for data, preferences and logs.
 */
public final class AppPaths {
    private AppPaths() {}

    /**
     * Returns the folder containing the running JAR. When running compiled classes (for example, with Gradle),
     * uses the working directory, which is the project folder in the development workflow.
     * Fails rather than falling back to another folder if the code source cannot be resolved.
     */
    public static Path getHomeDirectory() {
        try {
            Path codeSource = Path.of(AppPaths.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .toAbsolutePath().normalize();
            if (Files.isDirectory(codeSource)) {
                return Path.of("").toAbsolutePath().normalize();
            }
            return codeSource.getParent();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Cannot determine the application's home folder", e);
        }
    }
}
