package com.example.demo2batch.config;

import com.example.demo2batch.entity.A;
import com.example.demo2batch.entity.B;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@EnableBatchProcessing
@RequiredArgsConstructor
@Configuration
public class MongoBatchProcessingApplication {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final MongoTemplate mongoTemplate;

    private final MongoOperations mongoOperations;
    @Bean
    public ItemReader<A> itemReader() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        LocalDateTime twelveHoursAgo = currentDateTime.minusHours(12);

        java.util.Date date = java.util.Date.from(twelveHoursAgo.toInstant(ZoneOffset.UTC));

        Criteria criteria = Criteria.where("createdTimestamp");
        criteria.gte(date);

        Query query = new Query(criteria);

        MongoItemReader<A> itemReader = new MongoItemReader<>();
        itemReader.setTemplate(this.mongoTemplate);
        itemReader.setQuery(query);
        itemReader.setTargetType(A.class);
        return itemReader;

//        return new MongoItemReader<>(A.class, mongoOperations, query, "collectionA");
    }

    @Bean
    public ItemProcessor<A, A> itemProcessor() {
        return item -> item;
    }

    @Bean
    public ItemWriter<A> itemWriter() {
        return items -> {
            log.info("removing {} items, corrIds = {}", items.size(), items.stream().map(A::getCorrId).collect(Collectors.toList()));
            for (A item : items) {
                String corrId = item.getCorrId();
//                var a = mongoTemplate.remove(Query.query(Criteria.where("corrId").is(corrId)), "B");
                mongoTemplate.findAndRemove(Query.query(Criteria.where("corrId").is(corrId)), B.class);
                log.info("document with corrId on collectionB is removed - corrId {}",  corrId);
            }
        };
    }

    @Bean
    public Step step(ItemReader<A> itemReader, ItemProcessor<A, A> itemProcessor,
                     ItemWriter<A> itemWriter) {
        return stepBuilderFactory.get("step")
                .<A, A>chunk(20)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

    @Bean
    public Job job(Step step, JobExecutionDecider decider) {

        JobBuilder jobBuilder = jobBuilderFactory.get("job");
        Flow flow = new FlowBuilder<Flow>("flow")
                .start(decider)
                    .on("EXECUTE")
                        .to(step)
                .from(decider)
                    .on("*")
                        .end("END")
                .build();
        FlowJobBuilder flowJobBuilder = jobBuilder.start(flow).end();
        return flowJobBuilder.build();
//        return jobBuilderFactory.get("job")
//                .start(decider).on("EXECUTE").to(step)
//                .from(decider).on("*").end()
//                .end()
//                .build();
    }

    @Bean
    public JobExecutionDecider decider() {
        return new WeekdayDecider();
    }

    private Date getStartDate() {
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        return Date.from(last24Hours.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Component
    public static class WeekdayDecider implements JobExecutionDecider {

        @Override
        public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
            LocalDate currentDate = LocalDate.now();
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
//            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                return new FlowExecutionStatus("EXECUTE");
            } else {
                return new FlowExecutionStatus("SKIP");
            }
        }
    }
}
