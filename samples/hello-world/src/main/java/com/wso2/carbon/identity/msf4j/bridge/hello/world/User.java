package com.wso2.carbon.identity.msf4j.bridge.hello.world;

/**
 * Sample user bean to check the servlet bridge works.
 */
public class User {

    private String name;
    private String lastName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
