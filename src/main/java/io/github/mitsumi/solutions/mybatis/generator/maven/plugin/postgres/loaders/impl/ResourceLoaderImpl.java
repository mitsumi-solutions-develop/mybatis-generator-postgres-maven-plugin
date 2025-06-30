package io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres.loaders.impl;

import io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres.loaders.ResourceLoader;
import lombok.NoArgsConstructor;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.util.ClassloaderUtility;

import java.util.List;

@NoArgsConstructor(staticName = "build")
@SuppressWarnings({"PMD.CommentRequired", "PMD.LongVariable"})
public class ResourceLoaderImpl implements ResourceLoader {

    @Override
    public void load(final MavenProject project) {
        // add resource directories to the classpath.  This is required to support
        // use of a properties file in the build.  Typically, the properties file
        // is in the project's source tree, but the plugin classpath does not
        // include the project classpath.
        final List<String> resourceDirectories = project.getResources()
            .stream()
            .map(Resource::getDirectory)
            .toList();

        final ClassLoader classloader = ClassloaderUtility.getCustomClassloader(resourceDirectories);
        ObjectFactory.addExternalClassLoader(classloader);
    }
}
