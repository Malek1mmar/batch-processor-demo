package com.malloc.batchprocessor.job;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;

public class ReportTasklet implements Tasklet {

  private final JdbcTemplate jdbcTemplate;
  private final Path reportPath;

  public ReportTasklet(JdbcTemplate jdbcTemplate, Path reportPath) {
    this.jdbcTemplate = jdbcTemplate;
    this.reportPath = reportPath;
  }

  @Override
  public RepeatStatus execute(@NonNull StepContribution contribution, @NonNull ChunkContext chunkContext) throws Exception {
    var rows = jdbcTemplate.queryForList(
        "SELECT category, SUM(amount) AS total FROM transactions GROUP BY category ORDER BY total DESC");

    StringBuilder sb = new StringBuilder();
    sb.append("===== Transaction Summary =====\n");
    for (Map<String, Object> r : rows) {
      sb.append(r.get("category")).append(": ").append(r.get("total")).append("\n");
    }
    sb.append("===============================\n");

    writeReport(sb.toString());
    return RepeatStatus.FINISHED;
  }

  private void writeReport(String content) throws IOException {
    Files.createDirectories(reportPath.getParent());
    Files.writeString(reportPath, content);
  }
}