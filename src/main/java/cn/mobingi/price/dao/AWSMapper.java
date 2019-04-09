package cn.mobingi.price.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>AWS 查询服务数据库访问层接口</p>
 * @author sang
 * @date 2019-04-09
 * @version 1.0
 */
@Mapper
@Repository
public interface AWSMapper {

    /**
     * 根据页面的JSON参数查询相关数据
     * @param map 由页面JSON参数拼装成的参数map
     * @return 返回查询到的数据
     */
    List<Map<String,Object>> selectAWSInfoByParams(Map<String,Object> map);

}
