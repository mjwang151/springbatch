package com.batch;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.bean.Person;
import com.example.batchprocessing.JobCompletionNotificationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.support.HsqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;


// tag::setup[]
@Configuration
@EnableBatchProcessing
public class TodayQueryCountTask {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	// end::setup[]
	@Qualifier("fin_dw")
	@Autowired
	DataSource datasource;
	
	@Bean
	public ItemReader<Person> todayquerycountreader() {
		JdbcPagingItemReader<Person> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(datasource);               //设置数据源
        reader.setFetchSize(2);                         //FetchSize设置为2，表示每次从数据库中,2条数据
        reader.setRowMapper(new BeanPropertyRowMapper<Person>(Person.class));       //把数据库表中每条数据映射到User对象中
        HsqlPagingQueryProvider queryProvider = new HsqlPagingQueryProvider();
        
        queryProvider.setSelectClause("queryaccount as firstName,transcode as thirdName,count(*) as lastName");            //设置查询的列
        queryProvider.setFromClause("from eds_query_history2");     //设置查询的表
        queryProvider.setWhereClause("where begintime>'2021/01/01'");
        queryProvider.setGroupClause("group by queryaccount,transcode");
        Map<String, Order> sortKeys = new HashMap<>(); //定义一个map，用于存放排序列
        sortKeys.put("firstName", Order.ASCENDING);           //按id列升序排列
        sortKeys.put("thirdName", Order.DESCENDING);         //按age的降序排列
        queryProvider.setSortKeys(sortKeys);           //设置排序列
        reader.setQueryProvider(queryProvider);
        return reader;
	}

	@Bean
	public queryProcessor toadycountprocessor() {
		return new queryProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<Person> todayquerycountwriter(@Qualifier("batch") DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Person>()
			.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
			.sql("INSERT INTO people (first_name, last_name,thirdname) VALUES (:firstName, :lastName,:thirdName)")
			.dataSource(dataSource)
			.build();
	}
	// end::readerwriterprocessor[]

	// tag::jobstep[]
	@Bean
	public Job todaycountJob(JobCompletionNotificationListener listener, @Qualifier("todayquerycountstep") Step step) {
		return jobBuilderFactory.get("todaycountJob")
			.incrementer(new RunIdIncrementer())
			.flow(step)
			.end()
			.build();
	}

	@Bean
	public Step todayquerycountstep(@Qualifier("todayquerycountwriter") JdbcBatchItemWriter<Person> writer) {
		return stepBuilderFactory.get("todayquerycountstep")
			.<Person, Person> chunk(10)
			.reader(todayquerycountreader())
			.processor(toadycountprocessor())
			.writer(writer)
			.build();
	}
	// end::jobstep[]
}

class queryProcessor implements ItemProcessor<Person, Person> {

	private static final Logger log = LoggerFactory.getLogger(queryProcessor.class);

	@Override
	public Person process(final Person person) throws Exception {
		final String firstName = person.getFirstName() ==null? "" : person.getFirstName();
		final String lastName = person.getLastName()  ==null? "" : person.getLastName();
		final String thirdname = person.getThirdName() ==null? "" : person.getThirdName();

		final Person transformedPerson = new Person(lastName,firstName, thirdname);

		log.info("Converting (" + person + ") into (" + transformedPerson + ")");

		return transformedPerson;
	}

}
