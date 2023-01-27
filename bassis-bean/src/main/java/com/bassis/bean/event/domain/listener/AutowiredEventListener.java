package com.bassis.bean.event.domain.listener;

import com.bassis.bean.annotation.Listener;
import com.bassis.bean.annotation.impl.AutowiredImpl;
import com.bassis.bean.annotation.impl.ListenerImpl;
import com.bassis.bean.common.enums.ModuleEnum;
import com.bassis.bean.common.enums.ModuleStateEnum;
import com.bassis.bean.event.ApplicationEventPublisher;
import com.bassis.bean.event.ApplicationListener;
import com.bassis.bean.event.domain.AutowiredEvent;
import com.bassis.bean.event.domain.ControllerEvent;
import com.bassis.bean.event.domain.ModuleEvent;

@Listener
public class AutowiredEventListener implements ApplicationListener<AutowiredEvent> {
    private final AutowiredImpl autowired = AutowiredImpl.getInstance();

    @Override
    public void onApplicationEvent(AutowiredEvent event) {
        switch (event.getAutowiredEnum()) {
            case INJECT:
                autowired.analyseFields(event.getSource(), event.isSuperClass());
                autowired.twoStageAutowired();
                break;
            case RESOURCE_READY:
                // 再次执行注入及检查，防止漏掉的bean
                autowired.twoStageAutowired();
                // 加入事件监听器
                ListenerImpl.getInstance().listener();
                // 发布 beanFactroy 完成事件
                ApplicationEventPublisher.publishEvent(new ModuleEvent(ModuleEnum.BEAN, ModuleStateEnum.COMPLETE));
                break;
        }
    }
}
