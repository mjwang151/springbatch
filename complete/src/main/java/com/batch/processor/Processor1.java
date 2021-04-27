package com.batch.processor;

import com.alibaba.fastjson.JSONObject;
import com.bean.Devbankcashflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;


public class Processor1 implements ItemProcessor<Object, Object> {

	private static final Logger log = LoggerFactory.getLogger(Processor1.class);


	@Override
	public Object process(final Object devbankcashflow) throws Exception {
		log.info("格式化："+devbankcashflow.toString());
		return devbankcashflow;
	}
}
