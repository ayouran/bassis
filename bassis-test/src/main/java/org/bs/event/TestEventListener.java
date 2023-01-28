package org.bs.event;

import com.bassis.bean.annotation.Listener;
import com.bassis.bean.event.ApplicationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Listener
public class TestEventListener implements ApplicationListener<TestEvent> {
    private static Logger logger = LoggerFactory.getLogger(TestEventListener.class);

    @Override
    public void onApplicationEvent(TestEvent event) {
        logger.info("TestEventListener onApplicationEvent...............");
    }
}
