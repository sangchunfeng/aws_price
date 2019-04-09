package cn.mobingi.price.controller;

import cn.mobingi.price.service.AWSService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AWSController {

    @Autowired
    private AWSService awsService;

    @Autowired
    private Environment env;

    @RequestMapping(value = "price_service", method = RequestMethod.POST)
    public List<Map<String,Object>> selectAWSInfoByParams(@RequestBody JSONObject jsonParam) {
        Map<String,Object> map = new HashMap<>();
        String serviceName = (String)jsonParam.get("service_name");
        String requestName = (String)jsonParam.get("request_name");
        map.put("service_name","amazonec2 ec2");
        map.put("request_name",env.getProperty("aws." + serviceName + "." + requestName));
        Map<String,Object> jsonParamsMap = (Map<String,Object>)jsonParam.get("params");
        Map<String,Object> paramsMap = new HashMap<>();
         for (Map.Entry<String, Object> entry : jsonParamsMap.entrySet()) {
             paramsMap.put(entry.getKey(),entry.getValue());
        }
        map.put("params",paramsMap);
        map.put("group_by",env.getProperty("aws." + serviceName + "." + requestName + ".groupBy"));
        System.out.println(map);
        List<Map<String,Object>> mapList = awsService.selectAWSInfoByParams(map);
        System.out.println(mapList);
        return mapList;
    }
}
