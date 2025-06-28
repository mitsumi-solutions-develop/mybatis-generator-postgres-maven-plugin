package io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres.executors;

import io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres.MyBatisGeneratorMojoPostgres;
import org.apache.maven.plugin.MojoExecutionException;

@FunctionalInterface
@SuppressWarnings("PMD.CommentRequired")
public interface Executor {

    void execute(MyBatisGeneratorMojoPostgres mojoPostgres) throws MojoExecutionException;
}
