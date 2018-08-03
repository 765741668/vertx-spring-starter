package com.xxx.verticle;

import com.xxx.model.ReplyObj;
import com.xxx.utils.SpringContextUtil;
import com.xxx.vertx.VertxUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.springframework.context.ApplicationContext;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 消息处理器发布
 */
public class ConsumerRegistryVerticle extends AbstractVerticle {
    private final Object service;
    private String busAddress;

    public ConsumerRegistryVerticle(String beanName, String eventBusAddress) {
        ApplicationContext applicationContext = SpringContextUtil.getApplicationContext();
        Objects.requireNonNull(applicationContext, "The spring container has not been initialized to complete or has failed.");
        service = applicationContext.getBean(beanName);
        Objects.requireNonNull(service, "Could't find the service processor class in spring context.");
        this.busAddress = eventBusAddress;
    }

    /**
     * 针对verticle处理返回信息解析，主要针对错误信息识别成json数据，用户明文返回出去
     * 依赖与org.util中的exception
     *
     * @param errorObj
     * @param result
     * @return org.rayeye.vertx.result.ReplyObj
     * @throws
     * @method setResult
     * @author Neil.Zhou
     * @version
     * @date 2017/9/20 18:34
     */
    private ReplyObj setResult(Throwable errorObj, ReplyObj result) {
        try {
            JsonObject error = new JsonObject(errorObj.getMessage());
            if (error.containsKey("httpStatus")) {
                result.setCode(error.getInteger("httpStatus", 500));
            }
            /**** 服务正忙 默认是服务异常，为了友好性和调试方便，将错误信息解析后放到msg信息中 ***/
            if (error.containsKey("code")) {
                result.setData(error.getString("code", "服务正忙,稍后再试.").concat("[").concat(error.getString("detailMsg")).concat("]"));
            }
            if (error.containsKey("message")) {
                result.setMsg(error.getString("message", "服务正忙,稍后再试."));
            }
        } catch (Exception ex) {
            Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
            Matcher matcher = p.matcher(errorObj.getMessage());
            if (matcher.find()) {
                result.setMsg(errorObj.getMessage());
                result.setData(errorObj.getLocalizedMessage());
            } else {
                result.setMsg("服务正忙,稍后再试.");
                result.setData(errorObj.getLocalizedMessage());
            }
        }
        return result;
    }

    /**
     * 消息处理handler
     */
    private Handler<Message<JsonObject>> msgHandler() {
        return msg -> {
            String m = msg.headers().get("method");
            try {
                JsonObject message = (JsonObject) service.getClass().getMethod(m, JsonObject.class).invoke(service, msg.body());
                msg.reply(message);
            } catch (Exception e) {
                if (e instanceof NoSuchMethodException) {
                    msg.reply(new JsonObject(ReplyObj.build().setMsg("[NO_HANDLERS] 当前请求资源无效.").setCode(500).setData(e.getMessage()).toString()));
                } else {
                    msg.reply(new JsonObject(Json.encode(ReplyObj.build().setMsg(e.getMessage()).setCode(500))));
                }
            }
        };
    }

    /**
     * 注册事件驱动并
     *
     * @param
     * @return void
     * @throws Exception
     * @method start
     * @author Neil.Zhou
     * @version
     * @date 2017/9/20 18:43
     * @see #start()
     */
    @Override
    public void start() throws Exception {
        super.start();
        VertxUtil.getVertxInstance().eventBus().<JsonObject>consumer(busAddress).handler(msgHandler());
    }
}
