package com.bassis.boot.application;

import com.bassis.boot.common.ApplicationConfig;
import com.bassis.boot.common.Declaration;
import com.bassis.tools.properties.FileProperties;
import com.bassis.tools.reflex.ReflexUtils;
import com.bassis.tools.string.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * 自动配置
 */
public class AutoConfig {
    private static final long serialVersionUID = 1L;
    private static Logger logger = LoggerFactory.getLogger(AutoConfig.class);
    static FileProperties properties = FileProperties.getInstance();

    /**
     * 读取配置
     *
     * @param aClass               启动类
     * @param appApplicationConfig 配置
     * @return 返回读取完成的配置
     */
    protected static ApplicationConfig readProperties(Class aClass, ApplicationConfig appApplicationConfig) {
        if (null != aClass) appApplicationConfig.rootClass(aClass);
        String filePath = Objects.requireNonNull(ReflexUtils.getClassLoader().getResource(Declaration.config_file_name)).getFile();
        logger.debug(filePath);
        try {
            //读取配置文件
            properties.read(filePath);
            String port = FileProperties.getPropertiesFlesh(Declaration.bassis_server_port);
            if (!StringUtils.isEmptyString(port)) appApplicationConfig.setPort(Integer.valueOf(port));

            String contextPath = FileProperties.getPropertiesFlesh(Declaration.bassis_context_path);
            if (!StringUtils.isEmptyString(contextPath)) appApplicationConfig.setContextPath(contextPath);

            String scanRoot = FileProperties.getPropertiesFlesh(Declaration.bassis_scan_root);
            if (!StringUtils.isEmptyString(scanRoot)) appApplicationConfig.setScanRoot(scanRoot);

            String startSchema = FileProperties.getPropertiesFlesh(Declaration.bassis_start_schema);
            if (!StringUtils.isEmptyString(startSchema)) appApplicationConfig.setStartSchema(startSchema);

        } catch (Exception e) {
            logger.error(Declaration.config_file_name + " 配置无法读取,将由默认配置启动", e);
        }
        return appApplicationConfig;
    }
}
