package io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres.loaders;

import org.apache.maven.project.MavenProject;

@FunctionalInterface
@SuppressWarnings("PMD.CommentRequired")
public interface ResourceLoader {

    void load(MavenProject project);
}
