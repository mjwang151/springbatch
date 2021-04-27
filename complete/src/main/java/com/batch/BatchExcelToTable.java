package com.batch;

import javax.sql.DataSource;

import com.batch.processor.Processor1;
import com.batch.processor.Processor2;
import com.bean.Devbankcashflow;
import com.bean.utils.BeanHelper;
import com.example.batchprocessing.ApplicationContextUtils;
import com.example.batchprocessing.JobCompletionNotificationListener;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.lang.reflect.ParameterizedType;


@Configuration
@EnableBatchProcessing
public class BatchExcelToTable implements CommandLineRunner {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	// end::setup[]

	// tag::readerwriterprocessor[]

	/**
	 *"DevbankcashflowItemReader
	 * @param csvPath BankCashFlow.csv
	 * @param classname Devbankcashflow.class
	 * @return
	 */
	public FlatFileItemReader<Object> DevbankcashflowItemReader(String readerName ,String csvPath,Class<?> classname) {
		return new FlatFileItemReaderBuilder<>()
			.name(readerName)
			.resource(new ClassPathResource(csvPath))
			.delimited()
			.names(BeanHelper.getClassDeclaredFieldNames(classname))
			.fieldSetMapper(new BeanWrapperFieldSetMapper<Object>() {{
				setTargetType(classname);
			}})
			.build();
	}


	public Processor1 processor1() {
		return new Processor1();
	}

	public JdbcBatchItemWriter<Object> DevbankcashflowItemWriter(DataSource dataSource,Class<?> classname) {
		return new JdbcBatchItemWriterBuilder<Object>()
			.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
			.sql(BeanHelper.getSqlByClass(classname))
			.dataSource(dataSource)
			.build();
	}

	/**
	 * "devbankcashJob"
	 * @param jobName
	 * @param listener
	 * @param devbankcashStep
	 * @return
	 */
	public Job devbankcashJob(String jobName , JobCompletionNotificationListener listener,Step devbankcashStep) {
		return jobBuilderFactory.get(jobName)
			.incrementer(new RunIdIncrementer())
			.listener(listener)
			.flow(devbankcashStep)
			.end()
			.build();
	}

	/**
	 * devbankcashStep
	 * @param writer
	 * @return
	 */
	public Step devbankcashStep(String stepName, JdbcBatchItemWriter<Object> writer , FlatFileItemReader<Object> reader, ItemProcessor processor) {
		return stepBuilderFactory.get(stepName)
			.<Object, Object> chunk(10)
			.reader(reader)
			.processor(processor)
			.writer(writer)
			.build();
	}

	@Autowired
	@Qualifier("batchOracle")
	DataSource dataSource;
	@Autowired
	JobCompletionNotificationListener listener;

	@Override
	public void run(String... args) throws Exception {
//		Class<?> classname = Devbankcashflow.class;
//		String csvPath = "BankCashFlow.csv";
//		FlatFileItemReader<Object> objectFlatFileItemReader = DevbankcashflowItemReader("DevbankcashflowItemReader", csvPath, classname);
//		JdbcBatchItemWriter<Object> objectJdbcBatchItemWriter =DevbankcashflowItemWriter(dataSource, classname);
//		Processor1 processor1 = new Processor1();
//		Step step = devbankcashStep("devbankcashStep",objectJdbcBatchItemWriter,objectFlatFileItemReader,processor1);
//		Job job =  devbankcashJob("devbankcashJob",listener ,step);
//		JobLauncher jobLauncher = ApplicationContextUtils.getBean(JobLauncher.class);
//		JobParameters jobParameter = new JobParametersBuilder().addLong("time",System.currentTimeMillis()).toJobParameters();
//		JobExecution run =  jobLauncher.run(job, jobParameter);
	}
}

