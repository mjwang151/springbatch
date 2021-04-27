package com.batch.Tasklet;

import com.alibaba.fastjson.JSONObject;
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
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
public class ExecExcelTasklet implements Tasklet {

    private int execCount;
    private String tableName;
    private JdbcTemplate jdbcTemplate;
    private String excelPath;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("......running ExecExcelTasklet............");
        BufferedReaderFactory bufferedReaderFactory = new DefaultBufferedReaderFactory();
        Resource resource = new FileSystemResource(excelPath);
        BufferedReader reader = bufferedReaderFactory.create(resource, "UTF-8");
        String line = null;
        int lineCount = 0;
        String[] header = null;
        String sql = "";
        ArrayList<ArrayList<String>> sqlList = new ArrayList<ArrayList<String>>();
        try {
            while ((line = reader.readLine()) != null) {
                if (lineCount == 0) {
                    header = line.split(",");
                    sql = getSqlByHeader(header);
                } else {
                    ArrayList<String> list_zd = new ArrayList<String>();
                    String[] zds = line.split(",");
                    for (int i = 0; i < header.length; i++) {
                        if(i <= (zds.length-1)){
                            list_zd.add(StringUtils.isBlank(zds[i]) ? "" : zds[i]);
                        }else{
                            list_zd.add("");
                        }
                    }
                    sqlList.add(list_zd);
                }
                if (sqlList.size() % execCount == 0) {
                    execSql(sqlList, sql);
                    sqlList.clear();
                }
                lineCount++;
            }
            execSql(sqlList, sql);
        } catch (IOException e) {
            throw new NonTransientFlatFileException("Unable to read from resource: [" + resource + "]", e, line,
                    lineCount);
        }
        log.info("......finish ExecExcelTasklet............");
        return RepeatStatus.FINISHED;
    }

    private void execSql(ArrayList<ArrayList<String>> sqlList, String sql) {
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                printRealSql(sql, sqlList.get(i));
                for (int j = 1; j <= sqlList.get(i).size(); j++) {
                    ps.setString(j, sqlList.get(i).get(j - 1));
                }
            }

            @Override
            public int getBatchSize() {
                return sqlList.size();
            }
        });
    }


    /**
     * 生成sql
     *
     * @param
     * @return
     */
    private String getSqlByHeader(String[] header) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("insert into " + tableName + " (");
        for (int i = 0; i < header.length; i++) {
            stringBuffer.append(BatchExcelTasklet.regeStr+header[i]+BatchExcelTasklet.regeStr);
            if (i != (header.length - 1)) {
                stringBuffer.append(",");
            }
        }
        stringBuffer.append(") VALUES (");
        for (int i = 0; i < header.length; i++) {
            stringBuffer.append("?");
            if (i != (header.length - 1)) {
                stringBuffer.append(",");
            }
        }
        stringBuffer.append(")");
        return stringBuffer.toString();
    }

    /**
     * 在开发过程，SQL语句有可能写错，如果能把运行时出错的 SQL 语句直接打印出来，那对排错非常方便，因为其可以直接拷贝到数据库客户端进行调试。
     *
     * @param sql    SQL 语句，可以带有 ? 的占位符
     * @param params 插入到 SQL 中的参数，可单个可多个可不填
     * @return 实际 sql 语句
     */
    public static String printRealSql(String sql, ArrayList<String> params) {
        if (params == null || params.size() == 0) {
            log.debug("The SQL is------------>\n" + sql);
            return sql;
        }

        if (!match(sql, params)) {
            log.warn("SQL 语句中的占位符与参数个数不匹配。");
            return null;
        }

        int cols = params.size();
        Object[] values = new Object[cols];
        for (int i = 0; i < cols; i++) {
            values[i] = StringUtils.isNotBlank(params.get(i))?params.get(i):"" ;
        }
        for (int i = 0; i < cols; i++) {
            Object value = values[i];
            if (value instanceof Date) {
                values[i] = "'" + value + "'";
            } else if (value instanceof String) {
                values[i] = "'" + value + "'";
            } else if (value instanceof Boolean) {
                values[i] = (Boolean) value ? 1 : 0;
            }
        }

        String statement = String.format(sql.replaceAll("\\?", "%s"), values);

        log.debug("The SQL is------------>\n" + statement);


        return statement;
    }

    /**
     * ? 和参数的实际个数是否匹配
     *
     * @param sql    SQL 语句，可以带有 ? 的占位符
     * @param params 插入到 SQL 中的参数，可单个可多个可不填
     * @return true 表示为 ? 和参数的实际个数匹配
     */
    private static boolean match(String sql, ArrayList<String> params) {
        if (params == null || params.size() == 0) return true; // 没有参数，完整输出

        Matcher m = Pattern.compile("(\\?)").matcher(sql);
        int count = 0;
        while (m.find()) {
            count++;
        }

        return count == params.size();
    }

}
