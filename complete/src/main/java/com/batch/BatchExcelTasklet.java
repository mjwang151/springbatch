package com.batch;

import com.batch.Tasklet.ExecExcelTasklet;
import com.batch.Tasklet.ExecExcelTaskletCreate;
import com.batch.processor.Processor1;
import com.batch.processor.Processor2;
import com.bean.Devbankcashflow;
import com.bean.utils.BeanHelper;
import com.example.batchprocessing.ApplicationContextUtils;
import com.example.batchprocessing.JobCompletionNotificationListener;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ReaderNotOpenException;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.BufferedReaderFactory;
import org.springframework.batch.item.file.DefaultBufferedReaderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.NonTransientFlatFileException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Configuration
@EnableBatchProcessing
@Slf4j
public class BatchExcelTasklet implements CommandLineRunner {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    public static final String regeStr = "\"";
    @Autowired
    @Qualifier("batchOracleJdbcTemplate")
    JdbcTemplate jdbcTemplate;

    @Autowired
    JobCompletionNotificationListener listener;

    /**
     * @param stepName
     * @param tasklet
     * @return
     */
    public Step createStep(String stepName, Tasklet tasklet) {
        return stepBuilderFactory.get(stepName).tasklet(tasklet).build();
    }

    public Tasklet createTasklet2(int execCount,String tableName,JdbcTemplate jdbcTemplate,String excelPath) {
        return new ExecExcelTasklet(execCount,tableName,jdbcTemplate,excelPath);
    }


    public Tasklet createTasklet1(String tableName,JdbcTemplate jdbcTemplate,String excelPath) {
        return new ExecExcelTaskletCreate(tableName,jdbcTemplate,excelPath);

    }

    public Job createJob(String jobName, Step step1, Step step2) {
        return jobBuilderFactory.get(jobName)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .next(step2)
                .end()
                .build();
    }


    @Override
    public void run(String... args) throws Exception {
        JobLauncher jobLauncher = ApplicationContextUtils.getBean(JobLauncher.class);
        String tableName = "devBankFinalDebt";
        String excelPath = "H:\\workspace4\\springbatch\\complete\\src\\main\\resources\\ListedCompBasicInfo.csv";
        Job job = createJob("testjob",
                createStep("teststep1", createTasklet1(tableName,jdbcTemplate,excelPath)),
                createStep("teststep2", createTasklet2(500,tableName,jdbcTemplate,excelPath))
        ) ;
        JobParameters jobParameter = new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters();
        JobExecution run = jobLauncher.run(job, jobParameter);
    }
}

