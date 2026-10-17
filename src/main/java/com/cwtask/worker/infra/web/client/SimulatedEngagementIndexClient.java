package com.cwtask.worker.infra.web.client;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Stands in for engagement-index-api: no HTTP call, just latency and a deterministic "known" rule.
 */
@Component
public class SimulatedEngagementIndexClient implements EngagementIndexApiClient {

    private static final long SIMULATED_LATENCY_MS = 300;

    private static final Logger log = LoggerFactory.getLogger(SimulatedEngagementIndexClient.class);

    @Override
    public Set<String> findKnownEngagementIds(List<String> engagementIds) {
        if (engagementIds.size() > MAX_IDS_PER_REQUEST) {
            throw new IllegalArgumentException("At most " + MAX_IDS_PER_REQUEST
                    + " engagement IDs per request, got " + engagementIds.size());
        }

        log.info("Requesting known engagements for {} IDs", engagementIds.size());
        try {
            Thread.sleep(SIMULATED_LATENCY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while calling engagement-index-api", e);
        }

        Set<String> known = engagementIds.stream()
                .filter(id -> Math.floorMod(id.hashCode(), 2) == 0)
                .collect(Collectors.toSet());
        log.info("engagement-index-api knows {} of {} IDs", known.size(), engagementIds.size());
        return known;
    }

}
