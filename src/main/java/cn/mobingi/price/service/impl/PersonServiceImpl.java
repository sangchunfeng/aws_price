package cn.mobingi.price.service.impl;

import cn.mobingi.price.dao.PersonMapper;
import cn.mobingi.price.pojo.Person;
import cn.mobingi.price.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "personService")
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonMapper personMapper;

    @Override
    public Person selectPersonById(Integer id) {
        return personMapper.selectPersonById(id);
    }
}
