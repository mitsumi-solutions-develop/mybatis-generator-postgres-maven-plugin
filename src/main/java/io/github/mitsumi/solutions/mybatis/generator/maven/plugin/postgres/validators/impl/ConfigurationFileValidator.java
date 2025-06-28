package io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres.validators.impl;

import io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres.validators.Validator;
import lombok.NoArgsConstructor;
import org.apache.maven.plugin.MojoExecutionException;
import org.mybatis.generator.internal.util.messages.Messages;

import java.io.File;

@NoArgsConstructor(staticName = "build")
@SuppressWarnings("PMD.CommentRequired")
public class ConfigurationFileValidator implements Validator<File> {

    @Override
    public void validate(final File configurationFile) throws MojoExecutionException {
        if (configurationFile == null) {
            throw new MojoExecutionException(Messages.getString("RuntimeError.0"));
        }

        if (!configurationFile.exists()) {
            throw new MojoExecutionException(Messages.getString("RuntimeError.1", configurationFile.toString())); //$NON-NLS-1$
        }
    }
}
