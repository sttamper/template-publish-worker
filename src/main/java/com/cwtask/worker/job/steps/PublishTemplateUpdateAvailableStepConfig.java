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
public class PublishTemplateUpdateAvailableStepConfig {

    private static final String STEP_NAME = "publishTemplateUpdateAvailableStep";
    private static final int MAX_PARALLEL_PUBLICATIONS = 20;

    @Bean
    public Step publishTemplateUpdateAvailableStepBean(JobRepository jobRepository,
                                   PlatformTransactionManager transactionManager,
                                   PublishTemplateUpdateAvailableTasklet publishTemplateUpdateAvailableTasklet) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .tasklet(publishTemplateUpdateAvailableTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public PublishTemplateUpdateAvailableTasklet publishTemplateUpdateAvailableTaskletBean(@Qualifier("publishTemplateUpdateAvailableExecutorBean")
                                                                                           ExecutorService publishTemplateUpdateAvailableExecutor) {
        return new PublishTemplateUpdateAvailableTasklet(publishTemplateUpdateAvailableExecutor);
    }

    @Bean(destroyMethod = "close")
    public ExecutorService publishTemplateUpdateAvailableExecutorBean() {
        return Executors.newFixedThreadPool(MAX_PARALLEL_PUBLICATIONS);
    }

}
