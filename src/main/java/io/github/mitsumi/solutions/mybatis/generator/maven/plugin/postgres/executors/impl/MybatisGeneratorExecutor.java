package io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres.executors.impl;

import io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres.MavenPostgresShellCallback;
import io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres.MyBatisGeneratorMojoPostgres;
import io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres.PostgresProgressCallback;
import io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres.executors.Executor;
import io.github.mitsumi.solutions.mybatis.generator.postgres.config.parsers.PostgresConfigurationParser;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.util.StringUtility;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Builder
@SuppressWarnings("PMD.CommentRequired")
public class MybatisGeneratorExecutor implements Executor {
    private final String tableNames;
    private final String contexts;
    private final MavenProject project;
    private final File configurationFile;
    private final boolean overwrite;
    private final boolean verbose;

    @SuppressWarnings("PMD.LocalVariableCouldBeFinal")
    @Override
    public void execute(final MyBatisGeneratorMojoPostgres mojoPostgres) throws MojoExecutionException {
        final Set<String> tables = StringUtility.tokenize(tableNames);

        final Set<String> contextsToRun = StringUtility.tokenize(contexts);

        List<String> warnings = new ArrayList<>();

        try {
            final ConfigurationParser configParser = new PostgresConfigurationParser(project.getProperties(), warnings);
            final Configuration config = configParser.parseConfiguration(configurationFile);

            final ShellCallback callback = new MavenPostgresShellCallback(mojoPostgres.getOutputDirectory(), overwrite);

            final MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);

            myBatisGenerator.generate(new PostgresProgressCallback(verbose), contextsToRun, tables);

        } catch (XMLParserException | InvalidConfigurationException e) {
            e.getErrors().forEach(log::error);

            throw new MojoExecutionException(e.getMessage(), e);
        } catch (SQLException | IOException | InterruptedException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }

        warnings.forEach(log::warn);
    }
}
