package org.bs.event;

import com.bassis.bean.annotation.Listener;
import com.bassis.bean.event.ApplicationListener;

@Listener
public class TestEventListener implements ApplicationListener<TestEvent> {
    @Override
    public void onApplicationEvent(TestEvent event) {
        System.out.println("TestEventListener onApplicationEvent...............");
    }
}
