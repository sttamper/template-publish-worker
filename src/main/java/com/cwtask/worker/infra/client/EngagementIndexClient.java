package com.cwtask.worker.infra.client;

import java.util.List;
import java.util.Set;

public interface EngagementIndexClient {

    int MAX_IDS_PER_REQUEST = 50;

    Set<String> findKnownEngagementIds(List<String> engagementIds);

}
