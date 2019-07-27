package com.bassis.boot.container;

import com.bassis.bean.BeanFactory;
import com.bassis.bean.Scanner;
import com.bassis.boot.common.Declaration;
import com.bassis.tools.exception.CustomException;
import com.bassis.tools.properties.FileProperties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import com.bassis.tools.string.StringUtils;

/**
 * Servlet 核心容器
 */
public class BassisServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(BassisServlet.class);
    static ServletConfig servletConfig;
    static ServletContext servletContext;
    static FileProperties properties;
    static BeanFactory beanFactory;
    static Map<String, Class<?>> mapActions = new HashMap<>();
    static Map<String, Method> mapMethods = new HashMap<>();


    public void init(ServletConfig config) {
        logger.debug("初始化" + this.getClass().getName());
        servletConfig = config;
        // 获得上下文
        servletContext = config.getServletContext();
        //初始化配置文件读取器
        properties = FileProperties.getInstance();
        //启动扫描器
        String scanRoot = servletConfig.getInitParameter(Declaration.scanRoot);
        if (StringUtils.isEmptyString(scanRoot)) {
            CustomException.throwOut("init servlet failure : parameter [ " + Declaration.scanRoot
                    + " ] not allowed");
            return;
        }
        // 启动 beanFactory
        beanFactory = BeanFactory.startBeanFactory(scanRoot);
    }

    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        logger.debug("初始化service资源...");

        logger.debug("service方法完成");
    }


    public void destroy() {
        logger.debug("资源销毁");
    }


}
