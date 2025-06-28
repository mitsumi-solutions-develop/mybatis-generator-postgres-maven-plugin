package io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres;

import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.MojoExecutionException;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

@Slf4j
@Getter
@Builder
@SuppressWarnings({"PMD.LocalVariableCouldBeFinal", "PMD.CommentRequired"})
public class PostgresSqlScriptRunner {
    private static final String CLASS_PATH = "classpath:";

    private final String driver;
    private final String url;
    private final String userid;
    private final String password;
    private final String sourceFile;

    @SneakyThrows
    public void executeScript() {
        try (Connection connection = connection()) {
            run(connection);

            connection.commit();
        }
    }


    private Connection connection() throws
        ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
        InstantiationException, IllegalAccessException, SQLException {

        final Class<?> driverClass = ObjectFactory.externalClassForName(driver);
        final Driver theDriver = (Driver) driverClass.getDeclaredConstructor().newInstance();

        Connection connection = theDriver.connect(url, properties());
        connection.setAutoCommit(false);

        return connection;
    }

    private void run(final Connection connection) throws SQLException, MojoExecutionException, IOException {
        try (Statement statement = connection.createStatement()) {
            execute(statement);
        }
    }

    private void execute(final Statement statement) throws MojoExecutionException, IOException, SQLException {
        try (BufferedReader reader = getScriptReader()) {
            executeSql(statement, reader);
        }
    }

    private void executeSql(final Statement statement, final BufferedReader reader) throws IOException, SQLException {
        final String sql = readStatement(reader);
        if (StringUtility.stringHasValue(sql)) {
            statement.execute(sql);
            executeSql(statement, reader);
        }
    }

    private Properties properties() {
        Properties properties = new Properties();

        properties.setProperty("user", userid);
        properties.setProperty("password", password);

        return properties;
    }

    @SuppressWarnings("PMD.AssignmentInOperand")
    private String readStatement(final BufferedReader bufferedReader) throws IOException {
        StringBuilder builder = new StringBuilder();

        String line;

        while ((line = bufferedReader.readLine()) != null) {
            if (line.startsWith("--") || !StringUtility.stringHasValue(line)) {
                continue;
            }

            if (line.endsWith(";")) {
                builder.append(' ').append(line, 0, line.length() - 1);
                break;
            } else {
                builder.append(' ').append(line);
            }
        }

        final String trimmed = builder.toString().trim();

        if (!trimmed.isEmpty() && log.isDebugEnabled()) {
            log.debug(Messages.getString("Progress.13", trimmed));
        }

        return trimmed.isEmpty() ? null : trimmed;
    }


    private BufferedReader getScriptReader() throws MojoExecutionException, IOException {
        return sourceFile.startsWith("classpath:") ? classPathScriptReader() : scriptReader();
    }

    private BufferedReader classPathScriptReader() throws MojoExecutionException, IOException {
        final String resource = sourceFile.substring("classpath:".length());
        final URL url = ObjectFactory.getResource(resource);
        final InputStream inputStream = url.openStream();

        if (inputStream == null) {
            throw new MojoExecutionException("SQL script file does not exist: " + resource);
        }

        return new BufferedReader(new InputStreamReader(inputStream));
    }

    private BufferedReader scriptReader() throws MojoExecutionException, IOException {
        final File file = new File(sourceFile);

        if (!file.exists()) {
            throw new MojoExecutionException("SQL script file does not exist");
        }

        return Files.newBufferedReader(file.toPath());
    }
}
