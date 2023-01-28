package org.bs.event;

import com.bassis.bean.annotation.Listener;
import com.bassis.bean.event.ApplicationListener;
import com.bassis.bean.event.domain.BassisEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Listener
public class BassisEventListener implements ApplicationListener<BassisEvent> {
    private static Logger logger = LoggerFactory.getLogger(BassisEventListener.class);

    @Override
    public void onApplicationEvent(BassisEvent event) {
        logger.info("bassis-module:{} Start-up complete ...............", event.getModuleEnum());
    }
}
