package com.batch;

import com.bean.QueryDistributedBean;
import com.example.batchprocessing.ApplicationContextUtils;
import com.example.batchprocessing.JobCompletionNotificationListener;
import com.service.BaiduSearchIp;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.util.DateUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// tag::setup[]
@Configuration
@EnableBatchProcessing
public class BatchDistributedMap {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

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

    @Autowired
    @Qualifier("batchJdbcTemplate")
    JdbcTemplate jdbcTemplate;


    @Autowired
    @Qualifier("fin_dwJdbcTemplate")
    JdbcTemplate fin_dwJdbcTemplate;

    @Autowired
    BaiduSearchIp baiduSearchIp;

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2").tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                String date = DateUtils.format(new Date(), "yyyy-MM-dd");
                String getTotaySql = "select requestaddr as reqAddr,count(*) as querycount from eds_query_history2 where begintime>? and requestaddr is not null and requestaddr <>'' group by requestaddr";
                List<QueryDistributedBean> list = fin_dwJdbcTemplate.query(getTotaySql, new Object[]{"2010/01/01"}, new BeanPropertyRowMapper<QueryDistributedBean>(QueryDistributedBean.class));
                String sql = "INSERT INTO query_Distributed_Map (datadate, province , city, querycount,province_shortName) VALUES (?,?,?,?,?)";
                Map<String, QueryDistributedBean> map = initCity();
                list.stream().forEach(v -> {
                    QueryDistributedBean queryDistributedBean = baiduSearchIp.getData(v.getReqAddr());
                    if (StringUtils.isNotBlank(queryDistributedBean.getProvince()) || StringUtils.isNotBlank(queryDistributedBean.getCity())) {
                        if (map.containsKey(queryDistributedBean.getProvince() + queryDistributedBean.getCity())) {
                            long total = map.get(queryDistributedBean.getProvince() + queryDistributedBean.getCity()).getQuerycount() + v.getQuerycount();
                            map.get(queryDistributedBean.getProvince() + queryDistributedBean.getCity()).setQuerycount(total);
                        } else {
                            queryDistributedBean.setDatadate(date);
                            queryDistributedBean.setQuerycount(v.getQuerycount());
                            queryDistributedBean.setCity(queryDistributedBean.getCity());
                            queryDistributedBean.setProvince(queryDistributedBean.getProvince());
                            queryDistributedBean.setProvince_shortName(getShortName(queryDistributedBean.getProvince()));
                            map.put(queryDistributedBean.getProvince() + queryDistributedBean.getCity(), queryDistributedBean);
                        }
                    }
                });
                for (Map.Entry<String, QueryDistributedBean> entry : map.entrySet()) {
                    jdbcTemplate.update(sql, new Object[]{entry.getValue().getDatadate(), entry.getValue().getProvince(), entry.getValue().getCity(), entry.getValue().getQuerycount(),entry.getValue().getProvince_shortName()});
                }
                return null;
            }
        }).build();
    }

    /**
     * @return 城市省份列表
     */
    private Map<String, QueryDistributedBean> initCity() {
        String filePath = ApplicationContextUtils.getProperty("provAndCity.config");
        Map<String, QueryDistributedBean> map = new HashMap<String, QueryDistributedBean>();
        String date = DateUtils.format(new Date(), "yyyy-MM-dd");
        try {
            List<String> list1 = IOUtils.readLines(new FileInputStream(new File(filePath)), "UTF-8");
            list1.parallelStream().forEach(v -> {
                String prov = v.split(":")[0];
                String[] citys = v.split(":")[1].split("@");
                for (int i = 0; i < citys.length; i++) {
                    QueryDistributedBean queryDistributedBean = new QueryDistributedBean();
                    queryDistributedBean.setCity(citys[i]);
                    queryDistributedBean.setProvince(prov);
                    queryDistributedBean.setDatadate(date);
                    queryDistributedBean.setQuerycount(0);
                    queryDistributedBean.setProvince_shortName(getShortName(prov));
                    map.put(prov + citys[i], queryDistributedBean);
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     *
     * @param prov
     * @return
     */
    public static String getShortName(String prov) {
        String regex = "回族自治区|自治区|壮族自治区|维吾尔自治区|特别行政区|市|省|区";
        return prov.replaceAll(regex,"");

    }

}
