package io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres.loaders.impl;

import io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres.loaders.ResourceLoader;
import lombok.NoArgsConstructor;
import org.apache.maven.api.Language;
import org.apache.maven.api.ProjectScope;
import org.apache.maven.api.SourceRoot;
import org.apache.maven.project.MavenProject;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.util.ClassloaderUtility;

import java.nio.file.Path;
import java.util.List;

@NoArgsConstructor(staticName = "build")
@SuppressWarnings("PMD.CommentRequired")
public class ResourceLoaderImpl implements ResourceLoader {

    @Override
    public void load(final MavenProject project) {
        // add resource directories to the classpath.  This is required to support
        // use of a properties file in the build.  Typically, the properties file
        // is in the project's source tree, but the plugin classpath does not
        // include the project classpath.
        final List<String> directories = project.getEnabledSourceRoots(ProjectScope.MAIN, Language.RESOURCES)
            .map(SourceRoot::directory)
            .map(Path::toString)
            .toList();

        final ClassLoader classloader = ClassloaderUtility.getCustomClassloader(directories);
        ObjectFactory.addExternalClassLoader(classloader);
    }
}
