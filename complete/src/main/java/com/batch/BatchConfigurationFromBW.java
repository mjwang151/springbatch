package com.batch;

import java.util.ArrayList;

import javax.sql.DataSource;

import com.bean.Person;
import com.example.batchprocessing.JobCompletionNotificationListener;
import com.example.batchprocessing.PersonItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// tag::setup[]
@Configuration
@EnableBatchProcessing
public class BatchConfigurationFromBW {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	// end::setup[]

	// tag::readerwriterprocessor[]
	@Bean("reader2")
	public ListItemReader<Person> reader2() {
		ArrayList<Person> perlist = new ArrayList<Person>();
		perlist.add(new Person("ce1", "ce2"));
		perlist.add(new Person("ce3", "ce4"));
		return new ListItemReader<Person>(perlist);
	}

	@Bean
	public PersonItemProcessor processor2() {
		return new PersonItemProcessor();
	}

	@Bean("writer2")
	public JdbcBatchItemWriter<Person> writer2(@Qualifier("batch") DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Person>()
			.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
			.sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
			.dataSource(dataSource)
			.build();
	}
	// end::readerwriterprocessor[]

	// tag::jobstep[]
	@Bean("importUserJob2")
	public Job importUserJob2(JobCompletionNotificationListener listener, @Qualifier("step2") Step step) {
		return jobBuilderFactory.get("importUserJob2")
			.incrementer(new RunIdIncrementer())
			.listener(listener)
			.flow(step)
			.end()
			.build();
	}

	@Bean
	public Step step2(JdbcBatchItemWriter<Person> writer) {
		return stepBuilderFactory.get("step2")
			.<Person, Person> chunk(10)
			.reader(reader2())
			.processor(processor2())
			.writer(writer)
			.build();
	}
	// end::jobstep[]
}
