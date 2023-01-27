package com.bassis.bean.event.domain;

import com.bassis.bean.common.enums.AutowiredEnum;
import com.bassis.bean.event.ApplicationEvent;

/**
 * 资源加载完成 可以开始进行注入时的通知
 * source=0 资源就绪
 * source=1 资源注入
 * source=2 资源注前曝光
 */
public class AutowiredEvent extends ApplicationEvent {
    /**
     * superClass 是否从父类获取字段
     */
    private boolean superClass;
    private AutowiredEnum autowiredEnum;

    public boolean isSuperClass() {
        return superClass;
    }

    public void setSuperClass(boolean superClass) {
        this.superClass = superClass;
    }

    public AutowiredEnum getAutowiredEnum() {
        return autowiredEnum;
    }

    public void setAutowiredEnum(AutowiredEnum autowiredEnum) {
        this.autowiredEnum = autowiredEnum;
    }

    public AutowiredEvent(Object source, AutowiredEnum autowiredEnum, boolean superClass) {
        super(source);
        this.superClass = superClass;
        this.autowiredEnum = autowiredEnum;
    }

    public AutowiredEvent(AutowiredEnum autowiredEnum) {
        super(new Object());
        this.autowiredEnum = autowiredEnum;
    }
}
