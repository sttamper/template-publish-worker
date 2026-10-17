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

import static com.cwtask.worker.job.TemplatePublishedJobConfig.UNKNOWN_ENGAGEMENT_IDS;

public class OpenEngagementTasklet implements Tasklet {

    private static final Logger log = LoggerFactory.getLogger(OpenEngagementTasklet.class);
    private static final Duration OPEN_ENGAGEMENT_DURATION = Duration.ofMinutes(1);

    private final ExecutorService executor;

    public OpenEngagementTasklet(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        var jobContext = contribution.getStepExecution().getJobExecution().getExecutionContext();
        var unknownIds = (List<String>) jobContext.get(UNKNOWN_ENGAGEMENT_IDS);
        log.info("Opening {} engagements", unknownIds.size());

        var openedIds = openEngagements(unknownIds);

        log.info("Opened {} engagements", openedIds.size());
        return RepeatStatus.FINISHED;
    }

    private List<String> openEngagements(List<String> engagementIds) {
        var futures = engagementIds.stream()
                .map(id -> CompletableFuture.supplyAsync(() -> openEngagement(id), this.executor))
                .toList();

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .thenApply(ignored -> futures.stream().map(CompletableFuture::join).toList())
                .join();
    }

    private String openEngagement(String engagementId) {
        log.info("Opening engagement {}", engagementId);
        LockSupport.parkNanos(OPEN_ENGAGEMENT_DURATION.toNanos());
        log.info("Opened engagement {}", engagementId);
        return engagementId;
    }
}
