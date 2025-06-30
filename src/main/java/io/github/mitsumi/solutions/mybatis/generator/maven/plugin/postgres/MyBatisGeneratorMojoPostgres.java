package io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres;

import io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres.calculators.impl.ClassPathCalculatorImpl;
import io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres.executors.impl.MybatisGeneratorExecutor;
import io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres.loaders.impl.ResourceLoaderImpl;
import io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres.validators.impl.ConfigurationFileValidator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;

/**
 * Goal which generates MyBatis artifacts.
 */
@NoArgsConstructor
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES,
    requiresDependencyResolution = ResolutionScope.TEST)
@Slf4j
@SuppressWarnings({"PMD.LongVariable", "PMD.CommentRequired"})
public class MyBatisGeneratorMojoPostgres extends AbstractMojo {
    private static final String FALSE = "false";

    private final ThreadLocal<ClassLoader> savedClassloader = new ThreadLocal<>();

    /**
     * Maven Project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    private MavenProject project;

    /**
     * Output Directory.
     */
    @Getter
    @Parameter(
        property = "mybatis.generator.outputDirectory",
        defaultValue = "${project.build.directory}/generated-sources/mybatis-generator",
        required = true
    )
    private File outputDirectory;

    /**
     * Location of the configuration file.
     */
    @Parameter(
        property = "mybatis.generator.configurationFile",
        defaultValue = "${project.basedir}/src/main/resources/generatorConfig.xml",
        required = true
    )
    private File configurationFile;

    /**
     * Specifies whether the mojo writes progress messages to the log.
     */
    @Parameter(property = "mybatis.generator.verbose", defaultValue = FALSE)
    private boolean verbose;

    /**
     * Specifies whether the mojo overwrites existing Java files. Default is false.
     * <br>
     * Note that XML files are always merged.
     */
    @Parameter(property = "mybatis.generator.overwrite", defaultValue = FALSE)
    private boolean overwrite;

    /**
     * Location of a SQL script file to run before generating code. If null,
     * then no script will be run. If not null, then jdbcDriver, jdbcURL must be
     * supplied also, and jdbcUserId and jdbcPassword may be supplied.
     */
    @Parameter(property = "mybatis.generator.sqlScript")
    private String sqlScript;

    /**
     * JDBC Driver to use if a sql.script.file is specified.
     */
    @Parameter(property = "mybatis.generator.jdbcDriver")
    private String jdbcDriver;

    /**
     * JDBC URL to use if a sql.script.file is specified.
     */
    @Parameter(property = "mybatis.generator.jdbcURL")
    private String jdbcURL;

    /**
     * JDBC user ID to use if a sql.script.file is specified.
     */
    @Parameter(property = "mybatis.generator.jdbcUserId")
    private String jdbcUserId;

    /**
     * JDBC password to use if a sql.script.file is specified.
     */
    @Parameter(property = "mybatis.generator.jdbcPassword")
    private String jdbcPassword;

    /**
     * Comma-delimited list of table names to generate.
     */
    @Parameter(property = "mybatis.generator.tableNames")
    private String tableNames;

    /**
     * Comma-delimited list of contexts to generate.
     */
    @Parameter(property = "mybatis.generator.contexts")
    private String contexts;

    /**
     * Skip generator.
     */
    @Parameter(property = "mybatis.generator.skip", defaultValue = FALSE)
    private boolean skip;

    /**
     * If true, then dependencies in scope compile, provided, and system scopes will be
     * added to the classpath of the generator.
     * These dependencies will be searched for
     * JDBC drivers, root classes, root interfaces, generator plugins, etc.
     */
    @Parameter(property = "mybatis.generator.includeCompileDependencies", defaultValue = FALSE)
    private boolean compileDependencies;

    /**
     * If true, then dependencies in all scopes will be
     * added to the classpath of the generator.
     * These dependencies will be searched for
     * JDBC drivers, root classes, root interfaces, generator plugins, etc.
     */
    @Parameter(property = "mybatis.generator.includeAllDependencies", defaultValue = FALSE)
    private boolean allDependencies;

    @Override
    public void execute() throws MojoExecutionException {
        if (skip) {
            log.info("MyBatis generator is skipped.");
            return;
        }

        saveClassLoader();

        calculateClassPath();

        validateConfiguration();

        loadResource();

        runScriptIfNecessary();

        generate();

        after();
    }

    private void validateConfiguration() throws MojoExecutionException {
        ConfigurationFileValidator.build().validate(configurationFile);
    }

    private void loadResource() {
        ResourceLoaderImpl.build().load(project);
    }

    private void generate() throws MojoExecutionException {
        MybatisGeneratorExecutor.builder()
            .tableNames(tableNames)
            .contexts(contexts)
            .project(project)
            .configurationFile(configurationFile)
            .overwrite(overwrite)
            .verbose(verbose)
            .build()
            .execute(this);
    }

    @SuppressWarnings("PMD.LocalVariableCouldBeFinal")
    private void after() {
        if (project != null && outputDirectory != null && outputDirectory.exists()) {
            project.addCompileSourceRoot(outputDirectory.getAbsolutePath());

            Resource resource = new Resource();
            resource.setDirectory(outputDirectory.getAbsolutePath());
            resource.addInclude("**/*.xml");

            project.addResource(resource);
        }

        restoreClassLoader();
    }

    private void calculateClassPath() throws MojoExecutionException {
        ClassPathCalculatorImpl.build().calculate(compileDependencies, allDependencies, project);
    }

    private void runScriptIfNecessary() throws MojoExecutionException {
        if (sqlScript == null) {
            return;
        }

        PostgresSqlScriptRunner.builder()
            .sourceFile(sqlScript)
            .driver(jdbcDriver)
            .url(jdbcURL)
            .userid(jdbcUserId)
            .password(jdbcPassword)
            .build()
            .executeScript();

    }

    private void saveClassLoader() {
        savedClassloader.set(Thread.currentThread().getContextClassLoader());
    }

    @SuppressWarnings("PMD.DoNotUseThreads")
    private void restoreClassLoader() {
        Thread.currentThread().setContextClassLoader(savedClassloader.get());
    }
}
