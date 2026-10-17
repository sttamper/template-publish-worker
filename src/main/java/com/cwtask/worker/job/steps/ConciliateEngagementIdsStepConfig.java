package com.cwtask.worker.job.steps;

import com.cwtask.worker.infra.client.EngagementIndexApiClient;
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

    private static final String STEP_NAME = "conciliateEngagementIdsStep";
    private static final int PAGE_SIZE = 100;
    private static final int CHUNK_SIZE = 50;

    @Bean
    public Step conciliateEngagementIdsStepBean(JobRepository jobRepository,
                                        PlatformTransactionManager transactionManager,
                                        JpaPagingItemReader<String> engagementIdItemReader,
                                        ItemWriter<String> engagementIdsItemWriter) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<String, String>chunk(CHUNK_SIZE)
                .transactionManager(transactionManager)
                .reader(engagementIdItemReader)
                .writer(engagementIdsItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<String> engagementIdItemReaderBean(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<String>()
                .name("engagementIdItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select e.id from EngagementRecord e order by e.id")
                .pageSize(PAGE_SIZE)
                .build();
    }

    @Bean
    @StepScope
    public EngagementIdClassifierItemWriter engagementIdClassifierItemWriterBean(EngagementIndexApiClient engagementIndexApiClient) {
        return new EngagementIdClassifierItemWriter(engagementIndexApiClient);
    }

}
