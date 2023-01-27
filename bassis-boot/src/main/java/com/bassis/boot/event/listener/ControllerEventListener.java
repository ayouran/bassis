package com.bassis.boot.event.listener;

import com.bassis.bean.annotation.Listener;
import com.bassis.bean.event.ApplicationListener;
import com.bassis.bean.event.domain.ControllerEvent;

/**
 * @author liucheng
 * @version 1.0
 * @description: TODO
 * @date 2023/1/13 16:59
 */
@Listener
public class ControllerEventListener implements ApplicationListener<ControllerEvent> {
    @Override
    public void onApplicationEvent(ControllerEvent event) {
        
    }
}
