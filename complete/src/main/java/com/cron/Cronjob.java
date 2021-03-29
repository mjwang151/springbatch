package com.cron;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author 12
 */
@Component
@EnableScheduling
public class Cronjob {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    JobOperator jobOperator;

    @Autowired
    @Qualifier("importUserJob2")
    private Job importUserJob2;

    @Scheduled(cron = "*/10 * * * * ?")
    public void job3() throws Exception {
        System.out.println("自动跑批importUserJob2...........");
        JobParameters jobParameter = new JobParametersBuilder().addLong("time",System.currentTimeMillis()).toJobParameters();
        JobExecution run =    jobLauncher.run(importUserJob2, jobParameter);
    }

}
