package com.bassis.boot.application;

import com.bassis.bean.BeanFactory;
import com.bassis.bean.common.enums.ModuleEnum;
import com.bassis.bean.event.ApplicationEventPublisher;
import com.bassis.bean.event.domain.BassisEvent;
import com.bassis.bean.event.domain.ModuleEvent;
import com.bassis.boot.common.ApplicationConfig;
import com.bassis.boot.common.Declaration;
import com.bassis.boot.common.HttpPage;
import com.bassis.boot.common.MainArgs;
import com.bassis.boot.web.BassisHttp;
import com.bassis.boot.web.common.enums.RequestMethodEnum;
import com.bassis.tools.exception.CustomException;
import com.bassis.tools.json.GsonUtils;
import com.bassis.tools.reflex.ReflexUtils;
import com.bassis.tools.string.StringUtils;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.http.*;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.bassis.bean.common.enums.ModuleStateEnum.*;

public class VertxSupport {
    private static final long serialVersionUID = 1L;
    private static Logger logger = LoggerFactory.getLogger(VertxSupport.class);

    public void handleModuleBean(ModuleEvent event) {
        if (event.getModuleStateEnum() == COMPLETE) {
            ApplicationEventPublisher.publishEvent(new BassisEvent(ModuleEnum.BEAN));
        } else {
            logger.warn("ModuleState undefined");
        }
    }

    public void handleModuleBoot(ModuleEvent event) {
        if (event.getModuleStateEnum() == INIT) {
            this.appApplicationConfig = (ApplicationConfig) event.getSource();
            logger.debug("Application startSchema : " + appApplicationConfig.getStartSchema());
            switch (appApplicationConfig.getStartSchema()) {
                case Declaration.startSchemaCore:
                    new Thread(BeanFactory::blockStart).start();
                    break;
                case Declaration.startSchemaRpc:
                    //TODO rpc启动
                    break;
                case Declaration.startSchemaWeb:
                    //web启动
                    this.startHttpServer();
                default:
                    //web启动
                    this.startHttpServer();
                    break;
            }
            ApplicationEventPublisher.publishEvent(new ModuleEvent(ModuleEnum.BOOT, COMPLETE));
        } else if (event.getModuleStateEnum() == COMPLETE) {
            logger.info("boot start success");
            ApplicationEventPublisher.publishEvent(new BassisEvent(ModuleEnum.BOOT));
        } else if (event.getModuleStateEnum() == DESTROY) {
            switch (appApplicationConfig.getStartSchema()) {
                case Declaration.startSchemaCore:
                    BeanFactory.blockStop(0);
                    break;
                case Declaration.startSchemaRpc:
                    //TODO rpc关闭
                    break;
                case Declaration.startSchemaWeb:
                    this.downHttpServer();
                default:
                    this.downHttpServer();
                    break;
            }
        } else {
            logger.warn("ModuleState undefined");
        }
    }

    private static class LazyHolder {
        private static final VertxSupport INSTANCE = new VertxSupport();
    }

    private VertxSupport() {
        this.vertx = Vertx.vertx();
    }

    public static VertxSupport getInstance() {
        return VertxSupport.LazyHolder.INSTANCE;
    }

    private static MainArgs mainArgs = MainArgs.getInstance();
    private ApplicationConfig appApplicationConfig;
    private static BassisHttp bassisHttp = BassisHttp.getInstance();
    private HttpServer httpServer;
    private Router router;
    private Vertx vertx;

    /**
     * 启动 httpServer
     */
    private void startHttpServer() {
        if (this.appApplicationConfig.getHttpServerOptions() != null) {
            this.httpServer = vertx.createHttpServer(this.appApplicationConfig.getHttpServerOptions());
        } else {
            this.httpServer = vertx.createHttpServer();
        }
        this.router = Router.router(vertx);
        try {
            defaultConfig(mainArgs.getArgs());
            defaultIndexPage("/index");
            defaultErrorPage();
            registerRouterService();
            // 警告，需要在httpServer 起来之前设置好router
            this.httpServer
                    .requestHandler(this.router)
                    .listen(appApplicationConfig.getPort(), http -> {
                        if (http.succeeded()) {
                            logger.info("HTTP server started on port:" + appApplicationConfig.getPort());
                        } else {
                            logger.error("error", http.cause());
                        }
                    });
        } catch (Exception e) {
            CustomException.throwOut(" start [" + Declaration.startSchemaWeb + "] error ", e);
        }
    }

    /**
     * 获取路由绝对路径
     *
     * @param path 相对路由地址
     * @return 返回绝对路径
     */
    protected String getRouterPath(String path) {
        if (StringUtils.isEmptyString(path)) CustomException.throwOut(" add Router [" + path + "] error ");
        if (StringUtils.isEmptyString(this.appApplicationConfig.getContextPath())) return path;
        return this.appApplicationConfig.getContextPath() + path;
    }

    /**
     * 增加一个路由
     *
     * @param path          相对路由地址
     * @param requestMethod 路由的请求方式
     * @param consumer      回调函数
     * @param consumes      请求的 MIME类型 text/html application/json
     * @param produces      响应的 MIME类型 text/html application/json
     * @param <T>           入参
     * @param <R>           出参
     */
    @SuppressWarnings("all")
    protected <T, R> void addRouter(String path, RequestMethodEnum requestMethod, String consumes, String produces, Consumer<RoutingContext> consumer) {
        path = getRouterPath(path);
        String finalPath = path;
        HttpMethod httpMethod = getHttpMethod(requestMethod);
        logger.info("HTTP addRouter :{} httpMethod:{} consumes:{} produces:{}", finalPath, httpMethod.name(), consumes, produces);
        Route route = router.route(httpMethod, path);
        if (!StringUtils.isEmptyString(consumes)) route.consumes(consumes);
        if (!StringUtils.isEmptyString(produces)) route.produces(produces);
        route.handler(req -> {
            logger.info("HTTP routerHandler :{} httpMethod:{}", finalPath, req.request().method());
            consumer.accept(req);
        });
    }


    /**
     * 默认配置
     */
    private void defaultConfig(String[] args) {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                String s = args[i];
                if (i == 0 && appApplicationConfig.getPort() == null) {
                    this.appApplicationConfig.setPort(Integer.valueOf(s));
                } else if (i == 1 && StringUtils.isEmptyString(appApplicationConfig.getContextPath())) {
                    this.appApplicationConfig.setContextPath(s);
                } else if (i == 2 && StringUtils.isEmptyString(appApplicationConfig.getStartSchema())) {
                    this.appApplicationConfig.setStartSchema(s);
                } else if (i == 3 && StringUtils.isEmptyString(appApplicationConfig.getScanRoot())) {
                    this.appApplicationConfig.setScanRoot(s);
                } else {
                    logger.warn("unrecognized config parameter:{}", s);
                }
            }
        }
    }

    /**
     * 停止 HttpServer
     */
    private void downHttpServer() {
        logger.info("Vertx HTTP server close");
        this.httpServer.close();
    }

    /**
     * 注册所有的业务路由
     */
    private void registerRouterService() {
        bassisHttp.getRequestPaths().forEach(m -> {
            for (RequestMethodEnum requestMethod : m.getRequestMethodEnums()) {
                this.addRouterService(m.getPath(), requestMethod, m.getWrapClass());
            }
        });
    }


    /**
     * 配置默认的错误页面
     */
    private void defaultErrorPage() {
        this.addRouterPage("/404", null, HttpPage.ERROR_404, false);
        this.addRouterPage("/500", null, HttpPage.ERROR_500, false);
        this.addRouterPage("/503", null, HttpPage.ERROR_503, false);
    }

    /**
     * 获取resources下的文件
     *
     * @return 文件路径
     */
    private String getHtmlFilePath(String path) {
        return Objects.requireNonNull(ReflexUtils.getClassLoader().getResource(String.join(File.separator, "html", path))).getPath();
    }

    /**
     * HttpMethod 转换
     *
     * @param requestMethod 框架定义
     * @return vertx定义
     */
    private HttpMethod getHttpMethod(RequestMethodEnum requestMethod) {
        if (requestMethod == null || RequestMethodEnum.GET == requestMethod) return HttpMethod.GET;
        else if (RequestMethodEnum.HEAD == requestMethod) return HttpMethod.HEAD;
        else if (RequestMethodEnum.POST == requestMethod) return HttpMethod.POST;
        else if (RequestMethodEnum.PUT == requestMethod) return HttpMethod.PUT;
        else if (RequestMethodEnum.PATCH == requestMethod) return HttpMethod.PATCH;
        else if (RequestMethodEnum.DELETE == requestMethod) return HttpMethod.DELETE;
        else if (RequestMethodEnum.OPTIONS == requestMethod) return HttpMethod.OPTIONS;
        else if (RequestMethodEnum.TRACE == requestMethod) return HttpMethod.TRACE;
        else return HttpMethod.GET;
    }

    /**
     * 添加一个使用bean处理并且输出为json的路由
     *
     * @param path 路由
     */
    @SuppressWarnings("all")
    private void addRouterService(String path, RequestMethodEnum requestMethod, Boolean customParameters) {
        this.addRouterPage(path, requestMethod, null, customParameters);
    }

    /**
     * 配置请求返回一个页面
     */
    protected <T, R> void addRouterPage(String path, RequestMethodEnum requestMethod, String htmlFile, Boolean customParameters) {
        String consumes = "text/html";
        String produces = "text/html";
        if (StringUtils.isEmptyString(htmlFile)) {
            consumes = "application/json";
            produces = "application/json";
            if (customParameters) {
                consumes = "application/x-www-form-urlencoded";
            }
        }
        this.addRouter(path, requestMethod, consumes, produces, (req) -> {
            HttpServerRequest request = req.request();
            HttpServerResponse response = req.response();
            request.bodyHandler(body -> {
                if (body != null && body.length() > 0) {
                    LinkedHashMap<String, Object> requestParameters = new LinkedHashMap<>();
                    requestParameters.put("@body", body.toString());
                    response(response, htmlFile, bassisHttp.service(path, requestParameters));
                }
            });
            request.uploadHandler(handler -> {
                AsyncFile upload = handler.file();
            });
            String contentType = request.getHeader("content-type");
            if (contentType == null || !contentType.contains("application/json")) {
                LinkedHashMap<String, Object> requestParameters = new LinkedHashMap<>();
                MultiMap multiMap = request.params();
                if (multiMap != null && !multiMap.isEmpty()) {
                    multiMap.entries().forEach(m -> requestParameters.put(m.getKey(), m.getValue()));
                }
                response(response, htmlFile, bassisHttp.service(path, requestParameters));
            }
        });
    }

    /**
     * 返回
     *
     * @param response http返回
     * @param htmlFile 页面地址
     * @param resObj   返回结果
     */
    private void response(HttpServerResponse response, String htmlFile, Object resObj) {
        if (!StringUtils.isEmptyString(htmlFile)) {
            response.sendFile(getHtmlFilePath(htmlFile));
        } else {
            response.putHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            if (Objects.isNull(resObj)) {
                response.send(GsonUtils.objectToJson("null"));
            } else {
                response.send(GsonUtils.objectToJson(resObj));
            }
        }
    }

    /**
     * 配置默认的欢迎页面
     */
    protected void defaultIndexPage(String locationPath) {
        if (StringUtils.isEmptyString(locationPath)) {
            locationPath = "/index";
        }
        this.addRouterPage(locationPath, null, HttpPage.INDEX, false);
    }
}
