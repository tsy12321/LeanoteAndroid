package com.tsy.leanote.feature.user.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 用户信息
 * Created by tsy on 2016/12/13.
 */

@Entity
public class UserInfo {
    @Id
    private Long id;
    @Unique
    private String uid;
    private String username;
    private String email;
    private String logo;
    private String token;

    @Generated(hash = 1892143106)
    public UserInfo(Long id, String uid, String username, String email, String logo,
            String token) {
        this.id = id;
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.logo = logo;
        this.token = token;
    }

    @Generated(hash = 1279772520)
    public UserInfo() {
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", uid=" + uid +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", logo='" + logo + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogo() {
        return this.logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
