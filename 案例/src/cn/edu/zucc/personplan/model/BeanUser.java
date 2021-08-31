package cn.edu.zucc.personplan.model;

import java.util.Date;

public class BeanUser {
    public static BeanUser currentLoginUser = null;
    private String user, pwd;
    private Date registerTime;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }
}
