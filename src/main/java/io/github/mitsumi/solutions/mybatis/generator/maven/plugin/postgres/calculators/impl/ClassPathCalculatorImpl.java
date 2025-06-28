package io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres.calculators.impl;

import io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres.calculators.ClassPathCalculator;
import lombok.NoArgsConstructor;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.mybatis.generator.internal.util.ClassloaderUtility;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(staticName = "build")
@SuppressWarnings("PMD.CommentRequired")
public class ClassPathCalculatorImpl implements ClassPathCalculator {

    @Override
    @SuppressWarnings({"deprecation", "PMD.LocalVariableCouldBeFinal", "PMD.LongVariable", "PMD.DoNotUseThreads"})
    public void calculate(final boolean compileDependencies,
                          final boolean allDependencies,
                          final MavenProject project) throws MojoExecutionException {
        if (compileDependencies || allDependencies) {
            try {
                // add the project compile classpath to the plugin classpath,
                // so that the project dependency classes can be found
                // directly, without adding the classpath to configuration's classPathEntries
                // repeatedly.Examples are JDBC drivers, root classes, root interfaces, etc.
                Set<String> entries = new HashSet<>();
                if (compileDependencies) {
                    entries.addAll(project.getCompileClasspathElements());
                }

                if (allDependencies) {
                    entries.addAll(project.getTestClasspathElements());
                }

                // remove the output directories (target/classes and target/test-classes)
                // because this mojo runs in the generate-sources phase and
                // those directories have not been created yet (typically)
                entries.remove(project.getBuild().getOutputDirectory());
                entries.remove(project.getBuild().getTestOutputDirectory());

                final ClassLoader classloader = ClassloaderUtility.getCustomClassloader(entries);
                Thread.currentThread().setContextClassLoader(classloader);
            } catch (DependencyResolutionRequiredException e) {
                throw new MojoExecutionException("Dependency Resolution Required", e);
            }
        }
    }

}
