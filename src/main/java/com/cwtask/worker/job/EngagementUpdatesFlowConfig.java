package com.cwtask.worker.job;

import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.step.Step;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class EngagementUpdatesFlowConfig {

    private static final String FLOW_NAME = "engagementUpdatesFlow";
    private static final String PUBLISH_TEMPLATE_UPDATE_AVAILABLE_FLOW_NAME = "publishTemplateUpdateAvailableFlow";
    private static final String OPEN_ENGAGEMENT_FLOW_NAME = "openEngagementFlow";
    private static final String THREAD_NAME_PREFIX = "engagement-updates-";

    // Both flows only read the conciliation results, so they can run side by side
    @Bean
    public Flow engagementUpdatesFlowBean(@Qualifier("engagementUpdatesTaskExecutorBean")
                                          TaskExecutor engagementUpdatesTaskExecutor,
                                          @Qualifier("publishTemplateUpdateAvailableFlowBean")
                                          Flow publishTemplateUpdateAvailableFlow,
                                          @Qualifier("openEngagementFlowBean")
                                          Flow openEngagementFlow) {
        return new FlowBuilder<SimpleFlow>(FLOW_NAME)
                .split(engagementUpdatesTaskExecutor)
                .add(publishTemplateUpdateAvailableFlow, openEngagementFlow)
                .build();
    }

    @Bean
    public Flow publishTemplateUpdateAvailableFlowBean(@Qualifier("publishTemplateUpdateAvailableStepBean")
                                                       Step publishTemplateUpdateAvailableStep) {
        return new FlowBuilder<SimpleFlow>(PUBLISH_TEMPLATE_UPDATE_AVAILABLE_FLOW_NAME)
                .start(publishTemplateUpdateAvailableStep)
                .build();
    }

    @Bean
    public Flow openEngagementFlowBean(@Qualifier("openEngagementStepBean")
                                       Step openEngagementStep) {
        return new FlowBuilder<SimpleFlow>(OPEN_ENGAGEMENT_FLOW_NAME)
                .start(openEngagementStep)
                .build();
    }

    @Bean
    public TaskExecutor engagementUpdatesTaskExecutorBean() {
        return new SimpleAsyncTaskExecutor(THREAD_NAME_PREFIX);
    }

}
