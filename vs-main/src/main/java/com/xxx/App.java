package com.xxx;

import com.xxx.handlerfactory.RouterHandlerFactory;
import com.xxx.vertx.DeployVertxServer;
import com.xxx.vertx.VertxUtil;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

import java.io.IOException;

/**
 * @author Xu Haidong
 * @date 2018/8/2
 */
@SpringBootApplication
@ComponentScan("com.xxx")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @EventListener
    public void deployVerticles(ApplicationReadyEvent event) {
        EventBusOptions eventBusOptions = new EventBusOptions();
        eventBusOptions.setConnectTimeout(1000 * 60 * 30);
        Vertx vertx = Vertx.vertx(
                new VertxOptions().setWorkerPoolSize(20)
                        .setEventBusOptions(eventBusOptions)
                        .setMaxWorkerExecuteTime(Long.MAX_VALUE)
                        .setBlockedThreadCheckInterval(999999999L)
                        .setMaxEventLoopExecuteTime(Long.MAX_VALUE)
        );
        VertxUtil.init(vertx);
        try {
            DeployVertxServer.startDeploy(new RouterHandlerFactory("com.xxx.web", "api").createRouter(),
                    "com.xxx.service", 8989);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
