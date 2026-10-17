package com.cwtask.worker.job.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Placeholder steps that run in parallel once every chunk of evaluateEngagementsStep has been written.
 */
@Configuration
public class HelloWorldSteps {

    private static final Logger log = LoggerFactory.getLogger(HelloWorldSteps.class);

    @Bean
    public Step helloWorldStepOne(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return helloWorldStep("helloWorldStepOne", jobRepository, transactionManager);
    }

    @Bean
    public Step helloWorldStepTwo(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return helloWorldStep("helloWorldStepTwo", jobRepository, transactionManager);
    }

    private Step helloWorldStep(String name, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder(name, jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("Hello world from {}", name);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
