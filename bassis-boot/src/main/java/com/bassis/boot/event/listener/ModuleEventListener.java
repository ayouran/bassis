package com.bassis.boot.event.listener;

import com.bassis.bean.annotation.Listener;
import com.bassis.bean.event.ApplicationListener;
import com.bassis.bean.event.domain.ModuleEvent;
import com.bassis.boot.application.VertxSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Listener
public class ModuleEventListener implements ApplicationListener<ModuleEvent> {
    private static Logger logger = LoggerFactory.getLogger(ModuleEventListener.class);
    private final VertxSupport vertxSupport = VertxSupport.getInstance();

    @Override
    public void onApplicationEvent(ModuleEvent event) {
        switch (event.getModuleEnum()) {
            case BEAN:
                vertxSupport.handleModuleBean(event);
                break;
            case BOOT:
                vertxSupport.handleModuleBoot(event);
                break;
            default:
                logger.warn("Module undefined");
        }
    }
}
