package com.xxx.model;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * @author Xu Haidong
 * @date 2018/8/16
 */
public interface BaseRouteFilter {

    Handler<RoutingContext> doFilter();
}
