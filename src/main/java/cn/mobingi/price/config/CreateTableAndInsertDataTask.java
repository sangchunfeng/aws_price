package cn.mobingi.price.config;

import cn.mobingi.price.dao.AwsMapper;
import cn.mobingi.price.utils.ReadCsvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * <p>开启定时任务，创建数据库并插入数据定时任务启动类</p>
 * @author sang
 * @date 2019-04-11
 * @version 1.0
 */
@Component
@Configuration
@EnableScheduling
public class CreateTableAndInsertDataTask {

    private static final Logger logger = LoggerFactory.getLogger(CreateTableAndInsertDataTask.class);

    @Autowired
    private AwsMapper awsMapper;

    @Autowired
    private Environment env;

    /**
     * <p>从价格CSV文件中获取数据库表名，字段名以及数据定时任务方法</p>
     */
    @Scheduled(cron = "0 7 14 * * ?")
    private void createTableAndInsertDataTask() {
        try {
            List<String> csvUrls = ReadCsvUtils.getCsvHttpByIndex();
            if (null != csvUrls && csvUrls.size() > 0) {
                for (String csvUrl : csvUrls) {
                    Map<String, Object> map = ReadCsvUtils.getTableNameAndTableData(csvUrl);
                    String tableName = (String)map.get("tableName");
                    if (!StringUtils.isEmpty(awsMapper.existTable(tableName))) {
                        awsMapper.dropTable(map);
                    }
                    awsMapper.createTable(map);
                    awsMapper.insertData(map);
                }
            }
            ReadCsvUtils.dropDir(env.getProperty("csv.path"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
