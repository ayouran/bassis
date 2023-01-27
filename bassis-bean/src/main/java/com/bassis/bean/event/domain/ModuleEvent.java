package com.bassis.bean.event.domain;

import com.bassis.bean.common.enums.ModuleEnum;
import com.bassis.bean.common.enums.ModuleStateEnum;
import com.bassis.bean.event.ApplicationEvent;

/**
 * 系统模块事件
 */
public class ModuleEvent extends ApplicationEvent {
    private ModuleEnum moduleEnum;
    private ModuleStateEnum moduleStateEnum;

    public ModuleEnum getModuleEnum() {
        return moduleEnum;
    }

    public void setModuleEnum(ModuleEnum moduleEnum) {
        this.moduleEnum = moduleEnum;
    }

    public ModuleStateEnum getModuleStateEnum() {
        return moduleStateEnum;
    }

    public void setModuleStateEnum(ModuleStateEnum moduleStateEnum) {
        this.moduleStateEnum = moduleStateEnum;
    }

    public ModuleEvent(Object source, ModuleEnum moduleEnum, ModuleStateEnum moduleStateEnum) {
        super(source);
        this.moduleEnum = moduleEnum;
        this.moduleStateEnum = moduleStateEnum;
    }

    public ModuleEvent(ModuleEnum moduleEnum, ModuleStateEnum moduleStateEnum) {
        super(new Object());
        this.moduleEnum = moduleEnum;
        this.moduleStateEnum = moduleStateEnum;
    }
}
