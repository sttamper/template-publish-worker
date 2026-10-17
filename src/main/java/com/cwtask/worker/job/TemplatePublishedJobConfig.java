package com.cwtask.worker.job;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemplatePublishedJobConfig {

    public static final String KNOWN_ENGAGEMENT_IDS = "knownEngagementIds";
    public static final String UNKNOWN_ENGAGEMENT_IDS = "unknownEngagementIds";
    public static final String JOB_NAME = "templatePublishedJob";

    @Bean
    public Job templatePublishedJob(JobRepository jobRepository,
                                    @Qualifier("conciliateEngagementIdsStepBean")
                                    Step conciliateEngagementIdsStep,
                                    @Qualifier("engagementUpdatesFlowBean")
                                    Flow engagementUpdatesFlow) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .flow(conciliateEngagementIdsStep)
                .next(engagementUpdatesFlow)
                .end()
                .build();
    }
}
