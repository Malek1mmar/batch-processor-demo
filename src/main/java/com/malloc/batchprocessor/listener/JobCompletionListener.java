package com.malloc.batchprocessor.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionListener implements JobExecutionListener {
  private static final Logger log = LoggerFactory.getLogger(JobCompletionListener.class);

  @Override
  public void beforeJob(@NonNull JobExecution jobExecution) {
    log.info("Job starting...");
  }
  @Override
  public void afterJob(JobExecution jobExecution) {
    log.info("Job ended with status: {}", jobExecution.getStatus());
  }
}