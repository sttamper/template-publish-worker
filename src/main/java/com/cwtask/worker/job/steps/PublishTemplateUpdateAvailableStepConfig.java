package com.cwtask.worker.job.steps;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class PublishTemplateUpdateAvailableStepConfig {

    private static final String STEP_NAME = "publishTemplateUpdateAvailableStep";

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
    public PublishTemplateUpdateAvailableTasklet publishTemplateUpdateAvailableTaskletBean() {
        return new PublishTemplateUpdateAvailableTasklet();
    }

}
