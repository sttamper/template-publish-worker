package com.cwtask.worker.job.steps;

import java.util.ArrayList;

import com.cwtask.worker.infra.client.EngagementIndexApiClient;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;

import static com.cwtask.worker.job.TemplatePublishedJobConfig.KNOWN_ENGAGEMENT_IDS;
import static com.cwtask.worker.job.TemplatePublishedJobConfig.UNKNOWN_ENGAGEMENT_IDS;

public class EngagementIdClassifierItemWriter implements ItemWriter<String> {

    private final EngagementIndexApiClient engagementIndexApiClient;
    private final ArrayList<String> knownIds = new ArrayList<>();
    private final ArrayList<String> unknownIds = new ArrayList<>();

    public EngagementIdClassifierItemWriter(EngagementIndexApiClient engagementIndexApiClient) {
        this.engagementIndexApiClient = engagementIndexApiClient;
    }

    @Override
    public void write(Chunk<? extends String> chunk) {
        var ids = new ArrayList<String>(chunk.getItems());
        var chunkKnownIds = this.engagementIndexApiClient.findKnownEngagementIds(ids);

        ids.removeAll(chunkKnownIds);
        this.knownIds.addAll(chunkKnownIds);
        this.unknownIds.addAll(ids);
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        var jobContext = stepExecution.getJobExecution().getExecutionContext();

        jobContext.put(KNOWN_ENGAGEMENT_IDS, this.knownIds);
        jobContext.put(UNKNOWN_ENGAGEMENT_IDS, this.unknownIds);
        return stepExecution.getExitStatus();
    }
}
