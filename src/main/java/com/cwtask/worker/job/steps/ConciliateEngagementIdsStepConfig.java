package com.cwtask.worker.job.steps;

import com.cwtask.worker.infra.client.EngagementIndexClient;
import com.cwtask.worker.infra.orm.record.EngagementRecord;
import com.cwtask.worker.job.KnownEngagementsWriter;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JpaPagingItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ConciliateEngagementIdsStepConfig {

    private static final String STEP_NAME = "conciliateEngagementsStep";
    private static final int PAGE_SIZE = 100;
    private static final int CHUNK_SIZE = 50;

    @Bean
    public Step conciliateEngagementsStep(JobRepository jobRepository,
                                        PlatformTransactionManager transactionManager,
                                        JpaPagingItemReader<EngagementRecord> engagementReader,
                                        ItemWriter<EngagementRecord> engagementWriter) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<EngagementRecord, EngagementRecord>chunk(CHUNK_SIZE)
                .transactionManager(transactionManager)
                .reader(engagementReader)
                .writer(engagementWriter)
                .build();
    }

    @Bean
    public JpaPagingItemReader<EngagementRecord> engagementReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<EngagementRecord>()
                .name("engagementReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select e from EngagementRecord e order by e.id")
                .pageSize(PAGE_SIZE)
                .build();
    }

    @Bean
    public ItemWriter<EngagementRecord> engagementWriter(EngagementIndexClient engagementIndexClient) {
        return new KnownEngagementsWriter(engagementIndexClient);
    }

}
