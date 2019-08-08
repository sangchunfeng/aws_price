package cn.mobingi.price.controller;

import cn.mobingi.price.service.AWSService;
import cn.mobingi.price.service.impl.AWSServiceImpl;
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

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>AWS 查询服务Controller</p>
 * @author sang
 * @date 2019-04-09
 * @version 1.0
 */
@RestController
@EnableScheduling
public class AWSController {

    private static final Logger logger = LoggerFactory.getLogger(AWSController.class);

    public static final Map<String,Object> DATA_MAP = new HashMap<>();

    @Autowired
    private AWSService awsService;

    @Autowired
    private Environment env;

    /**
     * 根据页面传递的JSON参数，查询相关数据
     * @param jsonParam 接收页面传递的JSON数据
     * @return 返回查询到的数据
     */
    @RequestMapping(value = "/price_service", method = RequestMethod.POST)
    public Object selectAWSInfoByParams(@RequestBody JSONObject jsonParam) {
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
            List<Map<String, Object>> mapList = awsService.selectAWSInfoByParams(paramMap);
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

    @Scheduled(cron = "0 0 1 * * ?")
    private void clearDataMap() {
        DATA_MAP.clear();
    }


    @RequestMapping(value = "/price_service/users/getUserUuid", method = RequestMethod.POST)
    public Object getUserUUid() {
        Map<String,Object> resultMap = new HashMap<>();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        resultMap.put("status", true);
        resultMap.put("data", uuid);
        return resultMap;
    }


    @RequestMapping(value = "/price_service/histories/saveToHistory", method = RequestMethod.POST)
    public void saveHistory(@RequestBody JSONObject jsonParam, HttpServletRequest request) {
        String uuid = request.getHeader("uuid");
        List data = (List)jsonParam.get("data");
        String dataString = data.toString();
        String exchangeRate = (String)jsonParam.get("exchange_rate");
        String title = (String)jsonParam.get("title");
        Map<String, Object> paramMap = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createtime = sdf.format(new Date());
        paramMap.put("data", dataString);
        paramMap.put("exchangeRate", exchangeRate);
        paramMap.put("title", title);
        paramMap.put("uuid", uuid);
        paramMap.put("createtime", createtime);
        awsService.saveHistory(paramMap);

    }

    @RequestMapping(value = "/price_service/histories/getHistoryList", method = RequestMethod.GET)
    public Object listHistory(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();
        String uuid = request.getHeader("uuid");
        List<Map<String, Object>> historyList = awsService.selectHistoryByUUID(uuid);
        resultMap.put("status",true);
        resultMap.put("data", historyList);
        return resultMap;
    }

    @RequestMapping(value = "/price_service/templates/getTemplateList", method = RequestMethod.GET)
    public Object listTemplate() {
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> templateList = awsService.selectTemplateList();
        resultMap.put("status",true);
        resultMap.put("data", templateList);
        return resultMap;
    }

    @RequestMapping(value = "/price_service/getRate", method = RequestMethod.GET)
    public Object getRate() {
        Map<String, Object> resultMap = new HashMap<>();
        String rate = CurrentRateUtils.getUSDJPYTare();
        resultMap.put("status",true);
        resultMap.put("data", rate);
        return resultMap;
    }


}
