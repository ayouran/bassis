package com.bassis.boot.application;


import com.bassis.bean.BeanFactory;
import com.bassis.bean.common.enums.ModuleEnum;
import com.bassis.bean.common.enums.ModuleStateEnum;
import com.bassis.bean.event.ApplicationEventPublisher;
import com.bassis.bean.event.domain.ModuleEvent;
import com.bassis.boot.common.ApplicationConfig;
import com.bassis.boot.common.MainArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 启动起点
 */
public class BassisApplication {
    private static final long serialVersionUID = 1L;
    private static Logger logger = LoggerFactory.getLogger(BassisApplication.class);
    private static ApplicationConfig appApplicationConfig = new ApplicationConfig();
    private static final MainArgs mainArgs = MainArgs.getInstance();
    private static boolean startSchemaCoreFag = true;

    /**
     * 带参数启动
     * 优先以 properties文件配置为准
     *
     * @param aClass 调起BassisApplication的类实例
     * @param args   参数（暂时忽略当前参数）
     */
    public static void run(Class aClass, String[] args) {
        appApplicationConfig = AutoConfig.readProperties(aClass, appApplicationConfig);
        mainArgs.setArgs(args);
        //启动 BeanFactory
        logger.debug("BeanFactory start...");
        BeanFactory.startBeanFactory(appApplicationConfig.getScanRoot());
        ApplicationEventPublisher.publishEvent(new ModuleEvent(appApplicationConfig, ModuleEnum.BOOT, ModuleStateEnum.INIT));
    }

    /**
     * 停止框架
     */
    private static void stop() {
        ApplicationEventPublisher.publishEvent(new ModuleEvent(ModuleEnum.BOOT, ModuleStateEnum.DESTROY));
    }
}
