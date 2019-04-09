package cn.mobingi.price.service.impl;

import cn.mobingi.price.dao.AWSMapper;
import cn.mobingi.price.service.AWSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service(value = "awsService")
public class AWSServiceImpl implements AWSService {

    @Autowired
    private AWSMapper awsMapper;

    @Override
    public List<Map<String, Object>> selectAWSInfoByParams(Map<String, Object> map) {
        return awsMapper.selectAWSInfoByParams(map);
    }
}
