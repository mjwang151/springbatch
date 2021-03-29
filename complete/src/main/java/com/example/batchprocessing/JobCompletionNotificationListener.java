package com.example.batchprocessing;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	private final JdbcTemplate jdbcTemplate;
	
	static List<String> jobIds = new ArrayList<String>();
	static {
//		jobIds.add("importDbjob");
		jobIds.add("test111");
	}
	
	@Autowired
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		log.info("===================JOB任务开始运行【"+jobExecution.getJobInstance().getJobName()+"】，jobid为【"+jobExecution.getId()+"】=====================");
		if(jobIds.contains(jobExecution.getJobInstance().getJobName())) {
			super.beforeJob(jobExecution);
		}else {
			jobExecution.stop();
			log.info("===================JOB任务无需运行【"+jobExecution.getJobInstance().getJobName()+"】，jobid为【"+jobExecution.getId()+"】=====================");
		}
		
	}
	
	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {	
			log.info("!!! JOB FINISHED! Time to verify the results");	
		}
		log.info("===================JOB任务运行结束【"+jobExecution.getJobInstance().getJobName()+"】，jobid为【"+jobExecution.getId()+"】=====================");
		log.info("===================JOB任务花费时间【"+(jobExecution.getEndTime().getTime()-jobExecution.getStartTime().getTime())/1000+"秒】");

	}
}
