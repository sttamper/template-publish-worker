package com.cwtask.worker.job.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;

import static com.cwtask.worker.job.TemplatePublishedJobConfig.KNOWN_ENGAGEMENT_IDS;

public class PublishTemplateUpdateAvailableTasklet implements Tasklet {

    private static final Logger log = LoggerFactory.getLogger(PublishTemplateUpdateAvailableTasklet.class);
    private static final String ENGAGEMENT_UPDATES_TOPIC = "engagement-updates-topic";

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        var jobContext = contribution.getStepExecution().getJobExecution().getExecutionContext();
        log.info("Publishing template update available to Kafka topic {} for engagement IDs: {}",
                ENGAGEMENT_UPDATES_TOPIC, jobContext.get(KNOWN_ENGAGEMENT_IDS));
        return RepeatStatus.FINISHED;
    }
}
