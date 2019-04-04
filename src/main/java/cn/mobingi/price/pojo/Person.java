package cn.mobingi.price.pojo;

import org.springframework.stereotype.Component;

@Component
public class Person {

    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person [id = "+ this.id + ", name = " + this.name + "]";
    }
}
