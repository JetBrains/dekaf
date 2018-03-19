package org.jetbrains.dekaf.util;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;



/**
 * Service that loads jars and returns an instance of {@link ClassLoader}
 */
public final class JarLoader {

    @NotNull
    public final ClassLoader parentClassLoader;

    public JarLoader() {
        this(ClassLoader.getSystemClassLoader());
    }

    public JarLoader(final @NotNull ClassLoader parentClassLoader) {
        this.parentClassLoader = parentClassLoader;
    }

    @NotNull
    public ClassLoader load(final @NotNull String baseDirectory,
                            final @NotNull String[] jarPaths) {
        File baseDir = new File(baseDirectory);
        if (!baseDir.exists()) throw new IllegalArgumentException("The specified base directory \""+baseDirectory+"\" doesn't exist.");
        if (!baseDir.isDirectory()) throw new IllegalArgumentException("The specified base directory \""+baseDirectory+"\" is not a directory.");
        baseDir = canonizeFile(baseDir);

        return load(baseDir, jarPaths);
    }

    @NotNull
    public ClassLoader load(final @NotNull Path baseDirectory,
                            final @NotNull String[] jarPaths) {
        File file = baseDirectory.toFile();
        return load(file, jarPaths);
    }

    @NotNull
    public ClassLoader load(final @NotNull File baseDirectory,
                            final @NotNull String[] jarPaths) {
        ArrayList<File> jarFiles = new ArrayList<>(jarPaths.length);
        for (String path : jarPaths) {
            File f = new File(baseDirectory, path);
            f = canonizeFile(f);
            jarFiles.add(f);
        }

        return load(jarFiles);
    }

    @NotNull
    public ClassLoader load(final @NotNull List<File> jarFiles) {
        ArrayList<URL> us = new ArrayList<>(jarFiles.size());
        for (File f : jarFiles) {
            if (!f.exists()) throw new IllegalArgumentException("File \"" + f.getPath() + "\" doesn't exist.");
            if (!f.isFile()) throw new IllegalArgumentException("File \"" + f.getPath() + "\" is not a file.");
            us.add(fileToURL(f));
        }
        URL[] urls = us.toArray(new URL[0]);
        return new URLClassLoader(urls, parentClassLoader);
    }

    private static URL fileToURL(File f) {
        try {
            return f.toURI().toURL();
        }
        catch (MalformedURLException e) {
            // TODO handle it somehow
            throw new RuntimeException(e.toString(), e);
        }
    }

    @NotNull
    private static File canonizeFile(@NotNull File file) {
        try {
            return file.getCanonicalFile();
        }
        catch (IOException e) {
            // TODO blame that we couldn't canonize the file
            return file;
        }
    }

}
