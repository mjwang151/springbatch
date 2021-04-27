package com.batch.Tasklet;

import com.batch.BatchExcelTasklet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.BufferedReaderFactory;
import org.springframework.batch.item.file.DefaultBufferedReaderFactory;
import org.springframework.batch.item.file.NonTransientFlatFileException;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName ExecExcelTasklet
 * @Author mjwang
 * @Date 2021/4/27 17:02
 * @Description ExecExcelTasklet
 * @Version 1.0
 */
@Slf4j
@Setter
@Getter
@AllArgsConstructor
public class ExecExcelTaskletCreate implements Tasklet {

    String tableName;
    private JdbcTemplate jdbcTemplate;
    private String excelPath;


    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("......running ExecExcelTaskletCreate............");
        BufferedReaderFactory bufferedReaderFactory = new DefaultBufferedReaderFactory();
        Resource resource = new FileSystemResource(excelPath);
        BufferedReader reader = bufferedReaderFactory.create(resource, "UTF-8");
        String line = null;
        int lineCount = 0;
        String[] header = null;
        String createSql = "";
        ArrayList<ArrayList<String>> sqlList = new ArrayList<ArrayList<String>>();
        try {
            while ((line = reader.readLine()) != null) {
                if (lineCount == 0) {
                    header = line.split(",");
                    createSql = getTableByHeader(header);
                    break;
                }
                lineCount++;
            }
            execSql(createSql);
        } catch (IOException e) {
            throw new NonTransientFlatFileException("Unable to read from resource: [" + resource + "]", e, line,
                    lineCount);
        }
        log.info("......finish ExecExcelTaskletCreate............");
        return RepeatStatus.FINISHED;
    }

    private void execSql(String sql) {
        String dropSql = "drop table "+tableName;
        log.info("DropSQL:" + dropSql);
        try {
            jdbcTemplate.execute(dropSql);
        } catch (DataAccessException e) {
            log.info(tableName+" not exsist.");
        }
        log.info("CreateSQL:" + sql);
        jdbcTemplate.execute(sql);
    }

    /**
     * 生成sql
     *
     * @param
     * @return
     */
    private String getTableByHeader(String[] header) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("create table  " + tableName +" (");
        for (int i = 0; i < header.length; i++) {
            stringBuffer.append(BatchExcelTasklet.regeStr+header[i]+BatchExcelTasklet.regeStr);
            stringBuffer.append(" varchar2(4000)");
            if (i != (header.length - 1)) {
                stringBuffer.append(",");
            }
        }
        stringBuffer.append(")");
        return stringBuffer.toString();
    }


}
