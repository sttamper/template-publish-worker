package com.cwtask.worker.infra.web.client;

import java.util.List;
import java.util.Set;

public interface EngagementIndexApiClient {

    int MAX_IDS_PER_REQUEST = 50;

    Set<String> findKnownEngagementIds(List<String> engagementIds);

}
