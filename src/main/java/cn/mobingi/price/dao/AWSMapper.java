package cn.mobingi.price.dao;

import cn.mobingi.price.pojo.History;
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
     * <p>根据页面的JSON参数查询相关数据</p>
     * @param map 由页面JSON参数拼装成的参数map
     * @return 返回查询到的数据
     */
    List<Map<String,Object>> selectAWSInfoByParams(Map<String, Object> map);

    /**
     * <p>判断要插入的表是否存在</p>
     * @param tableName 表名
     * @return 返回已存在的表名
     */
    String existTable(String tableName);

    /**
     * <p>删除已经存在的表</p>
     * @param map 包含数据库表名的map
     */
    void dropTable(Map<String, Object> map);

    /**
     * <p>创建数据库表</p>
     * @param map 包含数据库表名与字段的map
     */
    void createTable(Map<String, Object> map);

    /**
     * <p>插入数据至数据库表</p>
     * @param map 包含数据库表名与数据的map
     */
    void insertData(Map<String, Object> map);


    void saveHistory(Map<String,Object> paramMap);

    List<Map<String, Object>> selectHistoryByUUID(String uuid);

    List<Map<String,Object>> selectTemplateList();
}
