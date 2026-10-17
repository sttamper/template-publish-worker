package com.cwtask.worker.infra.listener;

import java.util.UUID;

import com.cwtask.worker.domain.events.CurrentVersionChanged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
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
    public void onMessage(CurrentVersionChanged event, Acknowledgment acknowledgment) throws Exception {
        log.info("Received CurrentVersionChanged from version-registry-topic: {}", event);

        // At-most-once: commit before processing so a failed job is never redelivered
        acknowledgment.acknowledge();

        // A unique runId makes every event start a new job instance instead of matching a completed one
        JobParameters params = new JobParametersBuilder()
                .addString("runId", UUID.randomUUID().toString())
                .toJobParameters();

        JobExecution execution = jobOperator.start(templatePublishedJob, params);
        log.info("{} finished with status {} for {}", templatePublishedJob.getName(), execution.getStatus(), event);
    }

}
