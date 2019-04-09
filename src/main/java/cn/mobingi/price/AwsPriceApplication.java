package cn.mobingi.price;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * <p>AWS 查询服务主启动类</p>
 * @author sang
 * @date 2019-04-09
 * @version 1.0
 */
@SpringBootApplication
@EnableSwagger2
@EnableTransactionManagement
public class AwsPriceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AwsPriceApplication.class, args);
    }

}
