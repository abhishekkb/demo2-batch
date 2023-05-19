package com.example.demo2batch;

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
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@EnableBatchProcessing
@Configuration
public class JobConfig {

    private volatile int a = 0;
    @Autowired
    JobBuilderFactory jobBuilderFactory;
    @Autowired
    StepBuilderFactory stepBuilderFactory;
    @Bean
    public Job simpleJob(){
        JobBuilder jobBuilder = jobBuilderFactory.get("simpleJob");
        Flow flow = new FlowBuilder<Flow>("simpleFlow")
                .start(jobExecutionDecider())
                .on("EXECUTE")
                .to(step1())
                .next(step1())
                .from(jobExecutionDecider())
                .on("SKIP")
                .end("END")
                .build();
        FlowJobBuilder flowJobBuilder = jobBuilder.start(flow).end();
        return flowJobBuilder.build();
    }

    JobExecutionDecider jobExecutionDecider(){
        return new JobExecutionDecider() {
            @Override
            public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
                return new FlowExecutionStatus("EXECUTE");
            }
        };
    }

    Step step1(){
        return stepBuilderFactory.get("step1")
                .<Integer, Integer>chunk(40)
                .reader(step1Reader())
                .processor(step1Processor())
                .writer(step2Writer())
                .faultTolerant()
                .build();
    }

    private ItemWriter<? super Integer> step2Writer() {
        return new ItemWriter<Integer>() {
            @Override
            public void write(List<? extends Integer> list) throws Exception {
                log.info("int vals = {}", list);
            }
        };
    }

    private ItemProcessor<? super Integer, Integer> step1Processor() {
        return new ItemProcessor<Integer, Integer>() {
            @Override
            public Integer process(Integer i) throws Exception {
                log.info("Writer - {}", i);
                return i * 100;
            }
        };
    }

    private ItemReader<Integer> step1Reader() {
        return new ItemReader<Integer>() {
            @Override
            public Integer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                return Math.toIntExact((long) (Math.random() * 100));
            }
        };
    }
}
