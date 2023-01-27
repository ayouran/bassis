package com.bassis.bean.event.domain;


import com.bassis.bean.common.enums.ModuleEnum;
import com.bassis.bean.event.ApplicationEvent;

/**
 * @author liucheng
 * @version 1.0
 * @description: bassis启动完成的事件通知
 * @date 2023/1/27 16:29
 */
public class BassisEvent extends ApplicationEvent {
    private ModuleEnum moduleEnum;

    public BassisEvent(ModuleEnum moduleEnum) {
        this.moduleEnum = moduleEnum;
    }

    public BassisEvent() {
    }

    public ModuleEnum getModuleEnum() {
        return moduleEnum;
    }

    public void setModuleEnum(ModuleEnum moduleEnum) {
        this.moduleEnum = moduleEnum;
    }
}
