package cn.mobingi.price.service.impl;

import cn.mobingi.price.dao.AwsMapper;
import cn.mobingi.price.service.AwsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>AWS 查询服务数据库访问层接口实现类</p>
 * @author sang
 * @date 2019-04-09
 * @version 1.0
 */
@Service(value = "awsService")
public class AwsServiceImpl implements AwsService {

    private static final Logger logger = LoggerFactory.getLogger(AwsServiceImpl.class);

    @Autowired
    private AwsMapper awsMapper;

    /**
     * 根据页面的JSON参数查询相关数据
     * @param map 由页面JSON参数拼装成的参数map
     * @return 返回查询到的数据
     */
    @Override
    public List<Map<String, Object>> selectAwsInfoByParams(Map<String, Object> map) {
        return awsMapper.selectAwsInfoByParams(map);
    }

}
