package com.bassis.boot.event;

import com.bassis.bean.event.ApplicationEvent;

/**
 * 路由器事件
 * @author liucheng
 * @version 1.0
 * @description: TODO
 * @date 2023/1/13 16:56
 */
public class ControllerEvent extends ApplicationEvent {
    public ControllerEvent(Object source) {
        super(source);
    }
}
