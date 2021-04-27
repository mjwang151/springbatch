package com.batch.processor;

import com.bean.Devbankcashflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;


public class Processor2 implements ItemProcessor<Devbankcashflow, Devbankcashflow> {

	private static final Logger log = LoggerFactory.getLogger(Processor2.class);


	@Override
	public Devbankcashflow process(final Devbankcashflow devbankcashflow) throws Exception {
		log.info("格式化："+devbankcashflow.toString());
		return devbankcashflow;
	}


}
