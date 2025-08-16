package com.malloc.batchprocessor.config;


import com.malloc.batchprocessor.job.ReportTasklet;
import com.malloc.batchprocessor.model.Transaction;
import com.malloc.batchprocessor.model.TransactionCsv;
import com.malloc.batchprocessor.processor.TransactionItemProcessor;
import java.nio.file.Path;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

  // ---- Reader (CSV -> TransactionCsv) ----
  @Bean
  public FlatFileItemReader<TransactionCsv> transactionReader(
      @Value("${app.input-file}") Resource inputFile) {

    return new FlatFileItemReaderBuilder<TransactionCsv>()
        .name("transactionReader")
        .resource(inputFile)
        .linesToSkip(1)
        .delimited()
        .names("date", "description", "amount")
        .fieldSetMapper(fs -> new TransactionCsv(
            fs.readString("date"),
            fs.readString("description"),
            fs.readBigDecimal("amount")
        ))
        .build();
  }

  // ---- Processor (TransactionCsv -> Transaction) ----
  @Bean
  public ItemProcessor<TransactionCsv, Transaction> transactionProcessor() {
    return new TransactionItemProcessor();
  }

  // ---- Writer (Transaction -> DB) ----
  @Bean
  public JdbcBatchItemWriter<Transaction> transactionWriter(
      DataSource dataSource) {

    return new JdbcBatchItemWriterBuilder<Transaction>()
        .dataSource(dataSource)
        .sql("""
             INSERT INTO transactions (trx_date, description, amount, category)
             VALUES (:date, :description, :amount, :category)
             """)
        .beanMapped()
        .build();
  }

  // ---- Step 1: import CSV -> DB (chunk) ----
  @Bean
  public Step importStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      FlatFileItemReader<TransactionCsv> transactionReader,
      ItemProcessor<TransactionCsv, Transaction> transactionProcessor,
      JdbcBatchItemWriter<Transaction> transactionWriter) {

    return new StepBuilder("importStep", jobRepository)
        .<TransactionCsv, Transaction>chunk(500, transactionManager)
        .reader(transactionReader)
        .processor(transactionProcessor)
        .writer(transactionWriter)
        .build();
  }

  // ---- Step 2: aggregate & write report (tasklet) ----
  @Bean
  public Step reportStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      JdbcTemplate jdbcTemplate,
      @Value("${app.report-file}") String reportFile) {

    return new StepBuilder("reportStep", jobRepository)
        .tasklet(new ReportTasklet(jdbcTemplate, Path.of(reportFile)), transactionManager)
        .build();
  }

  // ---- Job: import then report ----
  @Bean
  public Job transactionJob(JobRepository jobRepository,
      @Qualifier("importStep") Step importStep,
      @Qualifier("reportStep") Step reportStep) {
    return new JobBuilder("transactionJob", jobRepository)
        .start(importStep)
        .next(reportStep)
        .build();
  }
}