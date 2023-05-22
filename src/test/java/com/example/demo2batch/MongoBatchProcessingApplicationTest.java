//package com.example.demo2batch;
//
//import com.example.demo2batch.config.MongoBatchProcessingApplication;
//import com.example.demo2batch.entity.A;
//import com.example.demo2batch.entity.B;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.batch.core.BatchStatus;
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.StepExecution;
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.item.ItemProcessor;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.batch.item.data.MongoItemReader;
//import org.springframework.batch.test.JobLauncherTestUtils;
//import org.springframework.batch.test.context.SpringBatchTest;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//
//@Disabled
//@Slf4j
//@ExtendWith({MockitoExtension.class, SpringExtension.class})
//@SpringBatchTest
//@RequiredArgsConstructor
//public class MongoBatchProcessingApplicationTest {
//
//    private final JobLauncherTestUtils jobLauncherTestUtils;
//
//    private final StepBuilderFactory stepBuilderFactory;
//    private final JobBuilderFactory jobBuilderFactory;
//
//    @Mock
//    private MongoTemplate mongoTemplate;
//
//    @Mock
//    private MongoItemReader<A> mongoItemReader;
//
//    @BeforeEach
//    public void setup() throws Exception {
//        List<A> mockData = new ArrayList<>();
//        // TODO Add mock data to the list
//        mockData.add(A.builder().build());
//        mockData.add(A.builder().build());
//        for (var a: mockData) {
//            when(mongoItemReader.read()).thenReturn(a);
//        }
//    }
//
//    @Test
//    public void testStep() throws Exception {
//        Step step = new MongoBatchProcessingApplication(jobBuilderFactory, stepBuilderFactory, mongoTemplate).step(mongoItemReader, itemProcessor(), itemWriter());
//
//        StepExecution stepExecution = jobLauncherTestUtils.launchStep("step", new JobParameters()).getStepExecutions().iterator().next();
//
//        assertEquals(BatchStatus.COMPLETED, stepExecution.getStatus());
//        // Assert other expected outcomes or perform further assertions on the StepExecution
//    }
//
//    public ItemProcessor<A, A> itemProcessor() {
//        return item -> item;
//    }
//
//    public ItemWriter<A> itemWriter() {
//        return items -> {
//            log.info("removing {} items, corrIds = {}", items.size(), items.stream().map(A::getCorrId).collect(Collectors.toList()));
//            for (A item : items) {
//                String corrId = item.getCorrId();
//                mongoTemplate.findAndRemove(Query.query(Criteria.where("corrId").is(corrId)), B.class);
//                log.info("document with corrId on collectionB is removed - corrId {}",  corrId);
//            }
//        };
//    }
//
//    @Configuration
//    @EnableBatchProcessing
//    @Import(MongoBatchProcessingApplication.class)
//    public static class TestConfig {
//        @Bean
//        public StepBuilderFactory stepBuilderFactory(JobBuilderFactory jobBuilderFactory) {
//            return new StepBuilderFactory(jobBuilderFactory, null);
//        }
//    }
//}
