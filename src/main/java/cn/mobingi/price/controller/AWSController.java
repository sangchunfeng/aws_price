package cn.mobingi.price.controller;

import cn.mobingi.price.service.AWSService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>AWS 查询服务Controller</p>
 * @author sang
 * @date 2019-04-09
 * @version 1.0
 */
@RestController
public class AWSController {

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
        try {
            Map<String, Object> map = new HashMap<>();
            //获取JSON中的service_name,映射表名
            String serviceName = (String) jsonParam.get("service_name");
            //获取JSON中的request_name,映射列名
            String requestName = (String) jsonParam.get("request_name");
            //判断要查询的数据是否存在,如存在，直接从Map中获取并返回
            if (null != DATA_MAP.get(jsonParam.toString().replace(" ","").trim())) {
                return DATA_MAP.get(jsonParam.toString().replace(" ","").trim());
            }
            map.put("service_name", env.getProperty(serviceName + ".table"));
            //获取properties文件中配置的列名
            map.put("request_name", env.getProperty(serviceName + "." + requestName));
            //获取JSON数据中的params,映射查询参数
            Map<String, Object> jsonParamsMap = (Map<String, Object>) jsonParam.get("params");
            Map<String, Object> paramsMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : jsonParamsMap.entrySet()) {
                paramsMap.put(entry.getKey(), entry.getValue());
            }
            map.put("params", paramsMap);
            //获取查询group by条件
            map.put("group_by", env.getProperty(serviceName + "." + requestName + ".groupBy"));
            map.put("having", env.getProperty(serviceName + "." + requestName + ".having"));
            List<Map<String, Object>> mapList = awsService.selectAWSInfoByParams(map);
            //返回数据库查询的数据
            if (null == mapList || mapList.size() == 0) {
                Map<String,Object> result = new HashMap<>();
                result.put("message","未找到数据");
                result.put("code","400");
                return result;
            }
            //如果Map中不存在当前数据，则将数据存入Map
            DATA_MAP.putIfAbsent(jsonParam.toString().replace(" ","").trim(), mapList);

            return mapList;
        } catch (Exception e) {
            Map<String,Object> result = new HashMap<>();
            result.put("message","服务器繁忙，请稍后重试");
            result.put("code","500");
            e.printStackTrace();
            return result;
        }
    }
}
