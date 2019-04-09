package cn.mobingi.price.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface AWSMapper {

    List<Map<String,Object>> selectAWSInfoByParams(Map<String,Object> map);

}
