package cn.mobingi.price.config;

import cn.mobingi.price.dao.AWSMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Component
@Configuration //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling // 2.开启定时任务
public class CreateTableAndInsertDataTask {
    @Autowired
    private AWSMapper awsMapper;

    //3.添加定时任务
    @Scheduled(cron = "0/5 * * * * ?")
    //或直接指定时间间隔，例如：5秒
    private void configureTasks() {
        //System.out.println(awsMapper.getPerson());
    }

}
