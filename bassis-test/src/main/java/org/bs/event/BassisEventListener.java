package org.bs.event;

import com.bassis.bean.annotation.Listener;
import com.bassis.bean.event.ApplicationListener;
import com.bassis.bean.event.domain.BassisEvent;

@Listener
public class BassisEventListener implements ApplicationListener<BassisEvent> {
    @Override
    public void onApplicationEvent(BassisEvent event) {
        System.out.println("BassisEventListener bassis启动完成 onApplicationEvent...............");
    }
}
