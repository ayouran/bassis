package org.bs.vo;

import java.io.Serializable;

/**
 * @author liucheng
 * @version 1.0
 * @description: TODO
 * @date 2023/1/27 21:58
 */
public class JsonVO implements Serializable {
    private String name;
    private String pwd;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public JsonVO() {
    }

    public JsonVO(String name, String pwd) {
        this.name = name;
        this.pwd = pwd;
    }
}
