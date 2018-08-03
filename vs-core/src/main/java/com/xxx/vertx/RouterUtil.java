package com.xxx.vertx;

import io.vertx.ext.web.Router;

/**
 * router单例
 */
public final class RouterUtil {

    private static Router router;

    private RouterUtil() {
        router = Router.router(VertxUtil.getVertxInstance());
    }

    public static Router getRouter() {
        if (router == null) {
            new RouterUtil();
        }
        return router;
    }
}
