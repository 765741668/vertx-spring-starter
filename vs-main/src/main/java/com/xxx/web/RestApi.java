package com.xxx.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xxx.anno.RouteHandler;
import com.xxx.anno.RouteMapping;
import com.xxx.anno.RouteMethod;
import com.xxx.model.BaseSenderHandler;
import com.xxx.model.ReplyObj;
import com.xxx.service.impl.TestServiceImpl;
import com.xxx.service.impl.UserServiceImpl;
import com.xxx.utils.ParamUtil;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * @author Xu Haidong
 * @date 2018/8/2
 */
@RouteHandler("restapp")
public class RestApi extends BaseSenderHandler {

    @RouteMapping(value = "/test", method = RouteMethod.GET)
    public Handler<RoutingContext> myTest() {
        return ctx -> {
            JsonObject param = ParamUtil.getRequestParams(ctx);
            ctx.response().setStatusCode(200);
            ctx.response().end(ReplyObj.build().setMsg("Hello，欢迎使用测试地址.....").setData(param.encode()).toString());
        };
    }

    @RouteMapping(value = "/doTest", method = RouteMethod.GET)
    public Handler<RoutingContext> doTest() {
        return ctx -> {
            JsonObject param = ParamUtil.getRequestParams(ctx);
            sendProcess(ctx, TestServiceImpl.class.getName(), "doTest", param, ar -> {
                if (ar.succeeded()) {
                    JsonObject result = ar.result().body();
                    ctx.response().setStatusCode(200).end(ReplyObj.build().setCode(200).setData(result).toString());
                } else {
                    ctx.response().setStatusCode(500).end(ReplyObj.build().setCode(500).setMsg(ar.cause().getMessage()).toString());
                }
            });
        };
    }


    @RouteMapping(value = "/findUser", method = RouteMethod.GET)
    public Handler<RoutingContext> findUser() {
        return ctx -> {
            JsonObject param = ParamUtil.getRequestParams(ctx);
            sendProcess(ctx, UserServiceImpl.class.getName(), "findUser", param, ar -> {
                if (ar.succeeded()) {
                    JsonObject result = ar.result().body();
                    ctx.response().setStatusCode(200).end(ReplyObj.build().setCode(200).setData(result).toString());
                } else {
                    ctx.response().setStatusCode(500).end(ReplyObj.build().setCode(500).setMsg(ar.cause().getMessage()).toString());
                }
            });
        };
    }

}
