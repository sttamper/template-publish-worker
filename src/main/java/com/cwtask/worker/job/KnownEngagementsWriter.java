package com.cwtask.worker.job;

import java.util.stream.Collectors;

import com.cwtask.worker.infra.client.EngagementIndexClient;
import com.cwtask.worker.infra.orm.record.EngagementRecord;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;

public class KnownEngagementsWriter implements ItemWriter<EngagementRecord> {

    private final EngagementIndexClient engagementIndexClient;

    public KnownEngagementsWriter(EngagementIndexClient engagementIndexClient) {
        this.engagementIndexClient = engagementIndexClient;
    }

    @Override
    public void write(Chunk<? extends EngagementRecord> chunk) {
        var ids = chunk.getItems().stream()
                .map(EngagementRecord::getId)
                .collect(Collectors.toList());
        var knownIds = this.engagementIndexClient.findKnownEngagementIds(ids);
    }
}
