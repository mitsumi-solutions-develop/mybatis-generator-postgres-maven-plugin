package io.github.mitsumi.solutions.mybatis.generator.maven.plugin.postgres;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.generator.api.ProgressCallback;

@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("PMD.CommentRequired")
public class PostgresProgressCallback implements ProgressCallback {
    private final boolean verbose;

    @Override
    public void startTask(final String subTaskName) {
        if (verbose) {
            log.info(subTaskName);
        }
    }
}
