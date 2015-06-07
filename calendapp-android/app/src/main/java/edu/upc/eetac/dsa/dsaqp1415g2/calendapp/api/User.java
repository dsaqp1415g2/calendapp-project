package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jordi on 07/06/2015.
 */
public class User {
    private int userid;
    private String username;
    private String userpass;
    private String name;
    private int age;
    private String email;
    private boolean loginSuccesfull;
    private Map<String,Link> links = new HashMap<String, Link>();
    private String eTag;

    public Map<String, Link> getLinks() {
        return links;
    }

    public void setLinks(Map<String, Link> links) {
        this.links = links;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }


    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpass() {
        return userpass;
    }

    public void setUserpass(String userpass) {
        this.userpass = userpass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isLoginSuccesfull() {
        return loginSuccesfull;
    }

    public void setLoginSuccesfull(boolean loginSuccesfull) {
        this.loginSuccesfull = loginSuccesfull;
    }
}
