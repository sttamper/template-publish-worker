package com.cwtask.worker.listener;

import com.cwtask.worker.batch.TemplatePublishedJobConfig;
import com.cwtask.worker.domain.events.CurrentVersionChanged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class VersionRegistryListener {

    private static final Logger log = LoggerFactory.getLogger(VersionRegistryListener.class);

    private final JobOperator jobOperator;
    private final Job templatePublishedJob;

    public VersionRegistryListener(JobOperator jobOperator, Job templatePublishedJob) {
        this.jobOperator = jobOperator;
        this.templatePublishedJob = templatePublishedJob;
    }

    @KafkaListener(topics = "version-registry-topic")
    public void onMessage(CurrentVersionChanged event) throws Exception {
        log.info("Received CurrentVersionChanged from version-registry-topic: {}", event);

        JobParameters params = new JobParametersBuilder()
                .addString(TemplatePublishedJobConfig.TEMPLATE_ID, event.getTemplateId())
                .addString(TemplatePublishedJobConfig.VERSION, event.getVersion())
                .toJobParameters();

        try {
            JobExecution execution = jobOperator.start(templatePublishedJob, params);
            log.info("{} finished with status {} for {}", TemplatePublishedJobConfig.JOB_NAME, execution.getStatus(), event);
        } catch (JobInstanceAlreadyCompleteException e) {
            log.info("{} already completed for {}, skipping", TemplatePublishedJobConfig.JOB_NAME, event);
        }
    }

}
