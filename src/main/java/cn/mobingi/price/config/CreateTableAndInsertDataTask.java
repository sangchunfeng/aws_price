package cn.mobingi.price.config;

import cn.mobingi.price.dao.AWSMapper;
import cn.mobingi.price.utils.ReadCsvUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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

    @Autowired
    private AWSMapper awsMapper;

    /**
     * <p>从价格CSV文件中获取数据库表名，字段名以及数据定时任务方法</p>
     */
    @Scheduled(cron = "0 15 14 * * ?")
    private void createTableAndInsertDataTask() {
        try {
            List<String> csvURLs = ReadCsvUtils.getCsvHttpByIndex();
            if (null != csvURLs || csvURLs.size() > 0) {
                for (String csvURL : csvURLs) {
                    Map<String, Object> map = ReadCsvUtils.getTableNameAndTableData(csvURL);
                    String tableName = (String)map.get("tableName");
                    if (!StringUtils.isEmpty(awsMapper.existTable(tableName))) {
                        awsMapper.dropTable(map);
                    }
                    awsMapper.createTable(map);
                    awsMapper.insertData(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
