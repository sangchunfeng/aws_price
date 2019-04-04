package cn.mobingi.price.controller;

import cn.mobingi.price.pojo.Person;
import cn.mobingi.price.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonController {

    @Autowired
    private PersonService personService;

    @RequestMapping("/getById")
    public String getPersonById(Integer id) {
        Person person = personService.selectPersonById(id);
        System.out.println(person);
        return "success";
    }
}
