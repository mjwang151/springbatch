package com;

import com.example.batchprocessing.ApplicationContextUtils;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@SpringBootApplication
public class BatchProcessingApplication {



	public static void main(String[] args) throws Exception {
		SpringApplication.run(BatchProcessingApplication.class, args);

//
//		Type genType = FlatFileItemReaderBuilder.class.getGenericSuperclass();
//		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
//		Class<?> entityClass = (Class) params[0];
//
//		System.out.println(entityClass.newInstance());
/*
		System.exit(SpringApplication.exit(SpringApplication.runInternal(BatchProcessingApplication.class, args)));
*/

	}
}
