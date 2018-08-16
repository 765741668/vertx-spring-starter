package com.xxx.web;

import com.xxx.anno.RouteHandler;
import com.xxx.anno.RouteMapping;
import com.xxx.anno.RouteMethod;
import com.xxx.entity.User;
import com.xxx.model.ReplyObj;
import com.xxx.service.UserAsyncService;
import com.xxx.service2.UserTwoAsyncService;
import com.xxx.utils.AsyncServiceUtil;
import com.xxx.utils.HttpUtil;
import com.xxx.utils.ParamUtil;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @author Xu Haidong
 * @date 2018/8/2
 */
@RouteHandler("restapp")
public class RestApi {

    private UserAsyncService userAsyncService = AsyncServiceUtil.getAsyncServiceInstance(UserAsyncService.class);

    private UserTwoAsyncService userTwoAsyncService = AsyncServiceUtil.getAsyncServiceInstance(UserTwoAsyncService.class);

    @RouteMapping(value = "/*", method = RouteMethod.ROUTE, order = 2)
    public Handler<RoutingContext> appFilter() {
        return ctx -> {
            System.err.println("我是appFilter过滤器！");
            ctx.next();
        };
    }

    @RouteMapping(value = "/test/:id", method = RouteMethod.GET)
    public Handler<RoutingContext> myTest() {
        return ctx -> {
            JsonObject param = ParamUtil.getRequestParams(ctx);
            ctx.response().setStatusCode(200);
            ctx.response().end(ReplyObj.build().setMsg("Hello，欢迎使用测试地址.....").setData(param.encode()).toString());
        };
    }

    @RouteMapping(value = "/listUsers", method = RouteMethod.GET)
    public Handler<RoutingContext> listUsers() {
        return ctx -> {
            JsonObject param = ParamUtil.getRequestParams(ctx);
            if (param.containsKey("age")) {
                param.put("age", Integer.valueOf(param.getString("age")));
            }
            User user = new User(param);
            userAsyncService.listUsers(user, ar -> {
                if (ar.succeeded()) {
                    List<User> userList = ar.result();
                    ctx.response().setStatusCode(HTTP_OK).end(ReplyObj.build().setData(userList).toString());
                } else {
                    ctx.response().setStatusCode(500).end(ReplyObj.build().setCode(500).setMsg(ar.cause().getMessage()).toString());
                }
            });
        };
    }

    @RouteMapping(value = "/findUserById", method = RouteMethod.GET)
    public Handler<RoutingContext> findUserById() {
        return ctx -> {
            JsonObject param = ParamUtil.getRequestParams(ctx);
            userTwoAsyncService.findUser(Long.valueOf(param.getString("id")), ar -> {
                if (ar.succeeded()) {
                    User user = ar.result();
                    HttpUtil.fireJsonResponse(ctx.response(), HTTP_OK, ReplyObj.build().setData(user));
                } else {
                    HttpUtil.fireJsonResponse(ctx.response(), HTTP_INTERNAL_ERROR, ReplyObj.build().setData(ar.cause().getMessage()).setCode(HTTP_INTERNAL_ERROR));
                }
            });
        };
    }

}
