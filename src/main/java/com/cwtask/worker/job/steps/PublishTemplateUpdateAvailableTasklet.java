package com.cwtask.worker.job.steps;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.LockSupport;

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
    private static final Duration PUBLISH_EVENT_DURATION = Duration.ofSeconds(2);

    private final ExecutorService executor;

    public PublishTemplateUpdateAvailableTasklet(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        var jobContext = contribution.getStepExecution().getJobExecution().getExecutionContext();
        var knownIds = (List<String>) jobContext.get(KNOWN_ENGAGEMENT_IDS);
        log.info("Publishing {} template update available events to Kafka topic {}",
                knownIds.size(), ENGAGEMENT_UPDATES_TOPIC);

        var publishedIds = publishEvents(knownIds);

        log.info("Published {} template update available events to Kafka topic {}",
                publishedIds.size(), ENGAGEMENT_UPDATES_TOPIC);
        return RepeatStatus.FINISHED;
    }

    private List<String> publishEvents(List<String> engagementIds) {
        var futures = engagementIds.stream()
                .map(id -> CompletableFuture.supplyAsync(() -> publishEvent(id), this.executor))
                .toList();

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .thenApply(ignored -> futures.stream().map(CompletableFuture::join).toList())
                .join();
    }

    private String publishEvent(String engagementId) {
        log.info("Publishing template update available event for engagement {}", engagementId);
        LockSupport.parkNanos(PUBLISH_EVENT_DURATION.toNanos());
        log.info("Published template update available event for engagement {}", engagementId);
        return engagementId;
    }
}
