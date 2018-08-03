package com.xxx.vertx;

import com.xxx.handlerfactory.ServiceHandlerFactory;
import com.xxx.verticle.RouterRegistryVerticle;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 开始注册vertx相关服务
 */
public class DeployVertxServer {

    private static Logger LOGGER = LoggerFactory.getLogger(DeployVertxServer.class);

    public static void startDeploy(Router router, int port) throws IOException {
        LOGGER.debug("Start Deploy....");
        VertxUtil.getVertxInstance().deployVerticle(new RouterRegistryVerticle(router, port));
    }

    public static void startDeploy(Router router, String handlerScan, int port) throws IOException {
        LOGGER.debug("Start Deploy....");
        VertxUtil.getVertxInstance().deployVerticle(new RouterRegistryVerticle(router, port));
        LOGGER.debug("Start registry handler....");
        new ServiceHandlerFactory(handlerScan).registerVerticle();
    }

    public static void startDeploy(Router router, String handlerScan, String appPrefix, int port) throws IOException {
        LOGGER.debug("Start Deploy....");
        VertxUtil.getVertxInstance().deployVerticle(new RouterRegistryVerticle(router, port));
        LOGGER.debug("Start registry handler....");
        new ServiceHandlerFactory(handlerScan, appPrefix).registerVerticle();
    }

    public static void startDeploy(String handlerScan, String appPrefix) throws IOException {
        LOGGER.debug("Start registry handler....");
        new ServiceHandlerFactory(handlerScan, appPrefix).registerVerticle();
    }
}
