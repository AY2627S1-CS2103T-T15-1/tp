package seedu.address.testutil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.security.SecureClassLoader;
import java.security.cert.Certificate;

/**
 * Gives AppPathsTest and LogsCenterTest a controlled code source and fresh static state.
 * The shared loader avoids duplicating class-loading setup or adding test hooks to production classes.
 * It runs in the test JVM so the coverage agent can observe the loaded classes.
 */
public class AppClassLoader extends SecureClassLoader {
    private final CodeSource codeSource;

    /**
     * Uses the supplied location as the code source of each application class loaded by this loader.
     */
    public AppClassLoader(URL location) {
        super(ClassLoader.getPlatformClassLoader());
        codeSource = new CodeSource(location, (Certificate[]) null);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (!name.equals("seedu.address.commons.util.AppPaths")
                && !name.equals("seedu.address.commons.core.LogsCenter")) {
            throw new ClassNotFoundException(name);
        }
        String resource = name.replace('.', '/') + ".class";
        try (InputStream input = AppClassLoader.class.getClassLoader().getResourceAsStream(resource)) {
            if (input == null) {
                throw new ClassNotFoundException(name);
            }
            byte[] bytes = input.readAllBytes();
            return defineClass(name, bytes, 0, bytes.length, codeSource);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
    }
}
