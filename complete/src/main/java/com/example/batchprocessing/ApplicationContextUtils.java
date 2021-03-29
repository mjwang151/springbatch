package com.example.batchprocessing;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;




@Component
public class ApplicationContextUtils implements ApplicationListener<ContextRefreshedEvent> {
	
	private static volatile ApplicationContext ctx;
	
	private static volatile boolean isInit = false;
	
	Logger logger = LoggerFactory.getLogger(ApplicationContextUtils.class);

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (isInit) {
			return;
		}
		ctx = event.getApplicationContext();
		initAllBatch();
	}
	
	public static String getProperty(String key) {
		return ctx.getEnvironment().getProperty(key);
	}
	
	public static Integer getIntProperty(String key) {
		String value = getProperty(key);
		if (value == null) {
			return null;
		}
		
		return Integer.parseInt(value);
	}
	
	public static Object getBean(String name) {
		return ctx.getBean(name);
	}
	
	public static <T> T getBean(Class<T> requiredType) {
		return ctx.getBean(requiredType);
	}
	
	public static <T> T getBean(String name, Class<T> requiredType) {
		return ctx.getBean(name, requiredType);
	}

	public void initAllBatch() {
//        Map<String, Order> sortKeys = new HashMap<>(); //定义一个map，用于存放排序列
//        sortKeys.put("first_name", Order.ASCENDING);           //按id列升序排列
//        sortKeys.put("last_name", Order.DESCENDING);         //按age的降序排列
//		DbQueryBean dbBean = new DbQueryBean("batch", "mysql", "*", "person", 2, "from people", sortKeys);
//		DbWriteBean dbwrite = new DbWriteBean("batch", "INSERT INTO people2 (first_name, last_name) VALUES (:firstName, :lastName)");
//		new BatchInitUtils(dbBean, dbwrite,"test111");
	}
}
