package cn.mobingi.price.service;

import java.util.List;
import java.util.Map;

public interface AWSService {

    List<Map<String,Object>> selectAWSInfoByParams(Map<String,Object> map);
}
