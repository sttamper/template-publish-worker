package com.cwtask.worker.job.steps;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class HelloWorldStepConfig {

    private static final String STEP_NAME = "helloWorldStep";

    @Bean
    public Step helloWorldStepBean(JobRepository jobRepository,
                                   PlatformTransactionManager transactionManager,
                                   HelloWorldTasklet helloWorldTasklet) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .tasklet(helloWorldTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public HelloWorldTasklet helloWorldTaskletBean() {
        return new HelloWorldTasklet();
    }

}
