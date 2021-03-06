package cn.mobingi.price.controller;

import cn.mobingi.price.service.AwsService;
import cn.mobingi.price.utils.CurrentRateUtils;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * <p>AWS 查询服务Controller</p>
 * @author sang
 * @date 2019-04-09
 * @version 1.0
 */
@RestController
@EnableScheduling
public class AwsController {

    private static final Logger logger = LoggerFactory.getLogger(AwsController.class);

    public static final Map<String,Object> DATA_MAP = new HashMap<>();

    @Autowired
    private AwsService awsService;

    @Autowired
    private Environment env;

    /**
     * 根据页面传递的JSON参数，查询相关数据
     * @param jsonParam 接收页面传递的JSON数据
     * @return 返回查询到的数据
     */
    @RequestMapping(value = "/price_service", method = RequestMethod.POST)
    public Object selectAwsInfoByParams(@RequestBody JSONObject jsonParam) {
        Map<String,Object> resultMap = new HashMap<>();
        try {
            if (null == jsonParam) {
                resultMap.put("message","参数错误");
                resultMap.put("code","500");
                return resultMap;
            }
            Map<String, Object> paramMap = new HashMap<>();
            //获取JSON中的service_name,映射表名
            String serviceName = (String) jsonParam.get("service_name");
            //获取JSON中的request_name,映射列名
            String requestName = (String) jsonParam.get("request_name");
            //判断要查询的数据是否存在,如存在，直接从Map中获取并返回
            if (null != DATA_MAP.get(jsonParam.toString().replace(" ","").trim())) {
                return DATA_MAP.get(jsonParam.toString().replace(" ","").trim());
            }
            paramMap.put("service_name", env.getProperty(serviceName + ".table"));
            //获取properties文件中配置的列名
            paramMap.put("request_name", env.getProperty(serviceName + "." + requestName));
            //获取JSON数据中的params,映射查询参数
            Map<String, Object> jsonParamsMap = (Map<String, Object>) jsonParam.get("params");
            if (null == jsonParamsMap) {
                resultMap.put("message","参数错误");
                resultMap.put("code","500");
                return resultMap;
            }
            Map<String, Object> paramsMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : jsonParamsMap.entrySet()) {
                paramsMap.put(entry.getKey(), entry.getValue());
            }
            paramMap.put("params", paramsMap);
            //获取查询group by条件
            paramMap.put("group_by", env.getProperty(serviceName + "." + requestName + ".groupBy"));
            paramMap.put("having", env.getProperty(serviceName + "." + requestName + ".having"));
            List<Map<String, Object>> mapList = awsService.selectAwsInfoByParams(paramMap);
            //返回数据库查询的数据
            if (null == mapList || mapList.size() == 0) {
                resultMap.put("message","未找到数据");
                resultMap.put("code","400");
                return resultMap;
            }
            //如果Map中不存在当前数据，则将数据存入Map
            DATA_MAP.putIfAbsent(jsonParam.toString().replace(" ","").trim(), mapList);

            return mapList;
        } catch (Exception e) {
            resultMap.put("message","服务器繁忙，请稍后重试");
            resultMap.put("code","500");
            e.printStackTrace();
            return resultMap;
        }
    }

    /**
     * 查询美元兑日元汇率
     * @return 返回美元兑日元汇率
     */
    @RequestMapping(value = "/price_service/getRate", method = RequestMethod.GET)
    public Object getRate() {
        Map<String, Object> resultMap = new HashMap<>();
        String rate = CurrentRateUtils.getUSDJPYTare();
        resultMap.put("status",true);
        resultMap.put("data", rate);
        return resultMap;
    }


    /**
     * 每天凌晨1点,清空缓存Map,因为0点做数据库更新,会有数据变化
     */
    @Scheduled(cron = "0 0 1 * * ?")
    private void clearDataMap() {
        DATA_MAP.clear();
    }

}
