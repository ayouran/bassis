package com.bassis.boot.common;

import com.bassis.boot.web.common.enums.RequestMethodEnum;

import java.io.Serializable;

/**
 * 请求路径详情
 * @author liucheng
 * @version 1.0
 * @description: TODO
 * @date 2023/1/13 19:54
 */
public class RequestPath implements Serializable {
    private String path;
    private RequestMethodEnum[] requestMethods;

    public RequestPath() {
    }

    public RequestPath(String path, RequestMethodEnum[] requestMethods) {
        this.path = path;
        this.requestMethods = requestMethods;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public RequestMethodEnum[] getRequestMethods() {
        return requestMethods;
    }

    public void setRequestMethods(RequestMethodEnum[] requestMethods) {
        this.requestMethods = requestMethods;
    }
}
