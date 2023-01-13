package com.bassis.boot.application;


import com.bassis.bean.BeanFactory;
import com.bassis.boot.common.ApplicationConfig;
import com.bassis.boot.common.Declaration;
import com.bassis.boot.common.MainArgs;
import com.bassis.tools.exception.CustomException;
import io.vertx.core.Vertx;
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
    private static  VertxSupport vertxSupport;

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
        vertxSupport = VertxSupport.getInstance();
        vertxSupport.init(appApplicationConfig);
        start();
    }

    /**
     * 启动框架
     */
    private static void start() {
        logger.debug("Application startSchema : " + appApplicationConfig.getStartSchema());
        switch (appApplicationConfig.getStartSchema()) {
            case Declaration.startSchemaWeb:
                vertxSupport.startHttpServer();
                break;
            case Declaration.startSchemaCore:
                new Thread(() -> {
                    while (startSchemaCoreFag) {
                        try {
                            Thread.sleep(10000);
                        } catch (Exception e) {
                            CustomException.throwOut(" start [" + Declaration.startSchemaCore + "] error ", e);
                        }
                    }
                }).start();
                break;
            case Declaration.startSchemaRpc:
                //TODO rpc启动
                break;
            default:
                //web启动
                vertxSupport.startHttpServer();
                //TODO rpc启动
                break;
        }
    }

    /**
     * 停止框架
     */
    private static void stop() {
        switch (appApplicationConfig.getStartSchema()) {
            case Declaration.startSchemaWeb:
                vertxSupport.downHttpServer();
                break;
            case Declaration.startSchemaCore:
                startSchemaCoreFag = false;
                break;
            case Declaration.startSchemaRpc:
                //TODO rpc关闭
                break;
            default:
                //http关闭
                vertxSupport.downHttpServer();
                //TODO rpc关闭
                break;
        }
    }
}
