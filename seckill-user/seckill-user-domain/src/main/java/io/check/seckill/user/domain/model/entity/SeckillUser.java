package io.check.seckill.user.domain.model.entity;

import java.io.Serializable;

/**
 * @author check
 * @version 1.0.0
 * @description 用户信息
 */
public class SeckillUser implements Serializable {
    private static final long serialVersionUID = -3004624289691589697L;
    //用户id
    private Long id;
    //用户名
    private String userName;
    //密码
    private String password;
    //1：正常；2：冻结
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
