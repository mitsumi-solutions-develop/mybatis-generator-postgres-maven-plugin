package io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres;

import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

@SuppressWarnings("PMD.CommentRequired")
public class MavenPostgresShellCallback extends DefaultShellCallback {
    private static final String MAVEN_PROJECT = "MAVEN";
    private final File outputDirectory;

    public MavenPostgresShellCallback(final File outputDirectory,
                                      final boolean overwrite) {
        super(overwrite);
        this.outputDirectory = outputDirectory;
    }

    @Override
    public File getDirectory(final String targetProject, final String targetPackage) throws ShellException {
        return MAVEN_PROJECT.equals(targetProject) ?
            getMavenDirectory(targetPackage) : super.getDirectory(targetProject, targetPackage);
    }

    private File getMavenDirectory(final String targetPackage) throws ShellException {
        // targetProject is the output directory from the MyBatis generator
        // Mojo. It will be created if necessary
        if (!outputDirectory.exists()) {
            final boolean mkdirs = outputDirectory.mkdirs();
            if (!mkdirs) {
                throw new ShellException(getString("Warning.10", outputDirectory.getAbsolutePath()));
            }
        }

        return super.getDirectory(outputDirectory.getAbsolutePath(), targetPackage);
    }
}
