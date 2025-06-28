package io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres.validators;

import org.apache.maven.plugin.MojoExecutionException;

@FunctionalInterface
@SuppressWarnings("PMD.CommentRequired")
public interface Validator<T> {

    void validate(T target) throws MojoExecutionException;
}
