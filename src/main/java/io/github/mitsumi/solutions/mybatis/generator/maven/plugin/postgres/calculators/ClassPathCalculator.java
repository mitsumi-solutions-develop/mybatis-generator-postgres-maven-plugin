package io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres.calculators;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

@FunctionalInterface
@SuppressWarnings("PMD.CommentRequired")
public interface ClassPathCalculator {

    @SuppressWarnings("PMD.LongVariable")
    void calculate(boolean compileDependencies,
                   boolean allDependencies,
                   MavenProject project) throws MojoExecutionException;
}
