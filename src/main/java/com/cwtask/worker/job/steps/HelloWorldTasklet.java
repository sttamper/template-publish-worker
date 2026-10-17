package com.cwtask.worker.job.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;

import static com.cwtask.worker.job.TemplatePublishedJobConfig.KNOWN_ENGAGEMENT_IDS;
import static com.cwtask.worker.job.TemplatePublishedJobConfig.UNKNOWN_ENGAGEMENT_IDS;

public class HelloWorldTasklet implements Tasklet {

    private static final Logger log = LoggerFactory.getLogger(HelloWorldTasklet.class);

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        var stepName = contribution.getStepExecution().getStepName();
        var jobContext = contribution.getStepExecution().getJobExecution().getExecutionContext();
        log.info("-----------------------Hello world from {}", stepName);
        log.info("Known engagement IDs: {}", jobContext.get(KNOWN_ENGAGEMENT_IDS));
        log.info("Unknown engagement IDs: {}", jobContext.get(UNKNOWN_ENGAGEMENT_IDS));
        return RepeatStatus.FINISHED;
    }
}
