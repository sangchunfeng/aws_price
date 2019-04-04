package cn.mobingi.price.dao;

import cn.mobingi.price.pojo.Person;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PersonMapper {

    Person selectPersonById(Integer id);

}
