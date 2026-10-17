package com.cwtask.worker.job.steps;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class OpenEngagementStepConfig {

    private static final String STEP_NAME = "openEngagementStep";
    private static final int MAX_PARALLEL_OPENINGS = 50;

    @Bean
    public Step openEngagementStepBean(JobRepository jobRepository,
                                       PlatformTransactionManager transactionManager,
                                       OpenEngagementTasklet openEngagementTasklet) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .tasklet(openEngagementTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public OpenEngagementTasklet openEngagementTaskletBean(@Qualifier("openEngagementExecutorBean")
                                                           ExecutorService openEngagementExecutor) {
        return new OpenEngagementTasklet(openEngagementExecutor);
    }

    @Bean(destroyMethod = "close")
    public ExecutorService openEngagementExecutorBean() {
        return Executors.newFixedThreadPool(MAX_PARALLEL_OPENINGS);
    }

}
