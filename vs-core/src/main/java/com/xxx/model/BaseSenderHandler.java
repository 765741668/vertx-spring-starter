package com.xxx.model;

import com.xxx.utils.EventBusAddressUtil;
import com.xxx.vertx.VertxUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Xu Haidong
 * @date 2018/8/2
 */
public abstract class BaseSenderHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseSenderHandler.class);

    private static final int TIME_OUT = 60000;

    private static final String DEFAULT_METHOD_COLUMN = "method";

    private static final String DEFAULT_METHOD = "execute";

    public static void sendProcess(RoutingContext ctx, String processor, String method, JsonObject params) {
        VertxUtil.getVertxInstance().eventBus().<JsonObject>send(EventBusAddressUtil.positiveFormate(processor), params,
                new DeliveryOptions().addHeader(DEFAULT_METHOD_COLUMN, method).setSendTimeout(TIME_OUT), resultBody -> {
                    if (resultBody.failed()) {
                        LOGGER.error("Fail for the process.");
                        ctx.fail(resultBody.cause());
                        return;
                    }
                    JsonObject result = resultBody.result().body();
                    if (result == null) {
                        LOGGER.error("Fail by result is null");
                        ctx.fail(500);
                        return;
                    }
                    ctx.response().setStatusCode(200);
                    ctx.response().end(result.encode());
                });
    }

    public static void sendProcess(RoutingContext ctx, String processor, String method, JsonObject params,
                                   Handler<AsyncResult<Message<JsonObject>>> replyHandler) {
        VertxUtil.getVertxInstance().eventBus().send(EventBusAddressUtil.positiveFormate(processor), params,
                new DeliveryOptions().addHeader(DEFAULT_METHOD_COLUMN, method).setSendTimeout(TIME_OUT), replyHandler);
    }
}
