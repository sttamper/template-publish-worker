package com.cwtask.worker.batch;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TemplatePublishedJobConfig {

    public static final String JOB_NAME = "templatePublishedJob";
    public static final String TEMPLATE_ID = "templateId";
    public static final String VERSION = "version";

    private static final Logger log = LoggerFactory.getLogger(TemplatePublishedJobConfig.class);

    @Bean
    public Job templatePublishedJob(JobRepository jobRepository, Step evaluateEngagementsStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(evaluateEngagementsStep)
                .build();
    }

    @Bean
    public Step evaluateEngagementsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("evaluateEngagementsStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Map<String, Object> params = chunkContext.getStepContext().getJobParameters();
                    log.info("Evaluating engagements for templateId={} version={}",
                            params.get(TEMPLATE_ID), params.get(VERSION));
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
