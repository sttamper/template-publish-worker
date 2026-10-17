package com.cwtask.worker.job;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
public class TemplatePublishedJobConfig {

    public static final String JOB_NAME = "templatePublishedJob";

    @Bean
    public Job templatePublishedJob(JobRepository jobRepository,
                                    Step conciliateEngagementsStep,
                                    Step helloWorldStepOne,
                                    Step helloWorldStepTwo) {
        Flow parallelSteps = new FlowBuilder<SimpleFlow>("parallelSteps")
                .split(new SimpleAsyncTaskExecutor("parallel-step-"))
                .add(flowOf(helloWorldStepOne), flowOf(helloWorldStepTwo))
                .build();

        return new JobBuilder(JOB_NAME, jobRepository)
                .flow(conciliateEngagementsStep)
                .next(parallelSteps)
                .end()
                .build();
    }

    private static Flow flowOf(Step step) {
        return new FlowBuilder<SimpleFlow>(step.getName() + "Flow").start(step).build();
    }

}
