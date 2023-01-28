package com.bassis.boot.web;

import com.bassis.bean.BeanFactory;
import com.bassis.bean.common.Bean;
import com.bassis.boot.common.HttpPage;
import com.bassis.boot.web.annotation.impl.ControllerImpl;
import com.bassis.boot.web.common.enums.RequestMethodEnum;
import com.bassis.tools.exception.CustomException;
import com.bassis.tools.json.GsonUtils;
import com.bassis.tools.reflex.Reflection;
import com.bassis.tools.reflex.ReflexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servlet 核心容器
 */
public class BassisHttp {
    private static final long serialVersionUID = 1L;
    private static Logger logger = LoggerFactory.getLogger(BassisHttp.class);
    private static final BeanFactory beanFactory = BeanFactory.getInstance();
    private static final ControllerImpl controller = ControllerImpl.getInstance();

    private static class LazyHolder {
        private static final BassisHttp INSTANCE = new BassisHttp();
    }

    private BassisHttp() {
    }

    public static BassisHttp getInstance() {
        return BassisHttp.LazyHolder.INSTANCE;
    }

    /**
     * 获取路由列表
     */
    public List<RequestPath> getRequestPaths() {
        return controller.getRequestPaths().entrySet().stream().map(m -> {
            String path = String.valueOf(m.getKey());
            Method method = ControllerImpl.getMapMethod(path);
            List<Object> parameters = ControllerImpl.getMapParameter(method);
            Class<?> type = (Class<?>) parameters.get(1);
            return new RequestPath(path, m.getValue(), ReflexUtils.isWrapClass(type));
        }).collect(Collectors.toList());
    }

    public static class RequestPath {
        private String path;
        private RequestMethodEnum[] requestMethodEnums;
        //不是基础数据类型为false
        private Boolean isWrapClass;

        public RequestPath() {
        }

        public RequestPath(String path, RequestMethodEnum[] requestMethodEnums, Boolean isWrapClass) {
            this.path = path;
            this.requestMethodEnums = requestMethodEnums;
            this.isWrapClass = isWrapClass;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public RequestMethodEnum[] getRequestMethodEnums() {
            return requestMethodEnums;
        }

        public void setRequestMethodEnums(RequestMethodEnum[] requestMethodEnums) {
            this.requestMethodEnums = requestMethodEnums;
        }

        public Boolean getWrapClass() {
            return isWrapClass;
        }

        public void setWrapClass(Boolean wrapClass) {
            isWrapClass = wrapClass;
        }
    }

    /**
     * 执行方法
     *
     * @param requestPath       请求路径
     * @param requestParameters 请求参数
     * @return 执行结果
     * @throws Exception
     */
    public Object service(String requestPath, LinkedHashMap<String, Object> requestParameters) {
        logger.debug("初始化service资源...");
        Object resInvoke = null;
        try {
            Class<?> actionCla = ControllerImpl.getMapClass(requestPath);
            Method method = ControllerImpl.getMapMethod(requestPath);
            if (HttpPage.ERROR_500.equalsIgnoreCase(requestPath)
                    || HttpPage.ERROR_503.equalsIgnoreCase(requestPath)
                    || HttpPage.ERROR_404.equalsIgnoreCase(requestPath)) {
            } else if (null == actionCla || null == method) {
                CustomException.throwOut("Requested resource not found:{}" + requestPath);
            } else {
                logger.debug("Start processing the request :{}", requestPath);
            }
            assert method != null;
            if (method.isVarArgs())
                CustomException.throwOut("variable parameter:" + requestPath + " method:" + method.getName());
            List<Object> parameters = ControllerImpl.getMapParameter(method);
            //请求参数值，参数值类型
            assert parameters != null;
            //验证方法参数
            int count = parameters.size() / 3;
            if (count <= 0) count = 1;
            Object[] arrayParameters = new Object[count];
            for (int i = 0; i < count; i++) {
                int index = i * 3;
                String name = (String) parameters.get(index);
                Class<?> type = (Class<?>) parameters.get(index + 1);
                Boolean required = (Boolean) parameters.get(index + 2);
                if (!ReflexUtils.isWrapClass(type)) {
                    name = "@body";
                }
                //检查必须参数
                if (!requestParameters.containsKey(name) && required)
                    CustomException.throwOut("method required parameter : " + name + " is null [" + requestPath + "]");
                Object ps = requestParameters.get(name);
                if (!ReflexUtils.isWrapClass(type)) {
                    arrayParameters[i] = GsonUtils.jsonToObject((String) ps, type);
                } else {
                    arrayParameters[i] = ps;
                }
            }
            //交由bean进行生产
            Bean bean = beanFactory.createBean(actionCla);
            logger.info("执行参数 :{}", GsonUtils.objectToJson(arrayParameters));
            resInvoke = Reflection.invokeMethod(bean.getObject(), method, arrayParameters);
            logger.info("resInvoke :{}", GsonUtils.objectToJson(resInvoke));
            //清除资源
            beanFactory.removeBean(bean);
        } catch (Exception e) {
            CustomException.throwOut("controller [" + requestPath + "] error ", e);
        }
        logger.debug("service方法完成");
        return resInvoke;
    }
}
