package com.cyitce.util.redis.pojo;

/**
 * @author jianhongyu
 * @version 1.0
 * @date 2020/11/19 21:41
 */
public class User {
    private String name;
    private String sex;

    public User() {
    }

    public User(String name, String sex) {
        this.name = name;
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
