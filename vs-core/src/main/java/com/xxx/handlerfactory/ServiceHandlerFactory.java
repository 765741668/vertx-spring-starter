package com.xxx.handlerfactory;

import com.xxx.anno.ServiceMethod;
import com.xxx.anno.VerticleMapping;
import com.xxx.utils.EventBusAddressUtil;
import com.xxx.verticle.ConsumerRegistryVerticle;
import com.xxx.vertx.VertxUtil;
import io.vertx.core.DeploymentOptions;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;

/**
 * 处理器注册工厂
 */
public class ServiceHandlerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceHandlerFactory.class);

    private static final String BASE_ROUTER = "/";

    private volatile String baseRouter = BASE_ROUTER;
    // 需要扫描注册的Router路径
    private static volatile Reflections reflections = null;

    public ServiceHandlerFactory(String handlerScanAddress, String appPrefix) {
        Objects.requireNonNull(handlerScanAddress, "The router package address scan is empty.");
        reflections = new Reflections(handlerScanAddress);
        this.baseRouter = appPrefix;
    }

    public ServiceHandlerFactory(String handlerScanAddress) {
        Objects.requireNonNull(handlerScanAddress, "The router package address scan is empty.");
        reflections = new Reflections(handlerScanAddress);
    }

    /**
     * verticle 服务注册
     */
    public void registerVerticle() {
        LOGGER.debug("Register Service Verticle...");
        Set<Class<?>> verticles = reflections.getTypesAnnotatedWith(VerticleMapping.class);
        String busAddressPrefix;
        for (Class<?> service : verticles) {
            try {
                if (service.isAnnotationPresent(VerticleMapping.class)) {
                    VerticleMapping routeHandler = service.getAnnotation(VerticleMapping.class);
                    if (StringUtils.isEmpty(routeHandler.value())) {
                        busAddressPrefix = service.getName();
                    } else {
                        busAddressPrefix = routeHandler.value();
                    }
                    if (busAddressPrefix.startsWith("/")) {
                        busAddressPrefix = busAddressPrefix.substring(1, busAddressPrefix.length());
                    }
                    if (!baseRouter.endsWith("/")) {
                        busAddressPrefix = baseRouter + "/" + busAddressPrefix;
                    } else {
                        busAddressPrefix = baseRouter + busAddressPrefix;
                    }
                    if (busAddressPrefix.endsWith("/")) {
                        busAddressPrefix = busAddressPrefix.substring(0, busAddressPrefix.length() - 1);
                    }
                    if (busAddressPrefix.startsWith("/")) {
                        busAddressPrefix = busAddressPrefix.substring(1, busAddressPrefix.length());
                    }
                    if (baseRouter.equals(busAddressPrefix)) {
                        /***** 每一个方法都部署一个verticle *****/
                        Method[] methods = service.getDeclaredMethods();
                        for (Method method : methods) {
                            if (method.isAnnotationPresent(ServiceMethod.class)) {
                                ServiceMethod serviceMethod = method.getAnnotation(ServiceMethod.class);
                                String methodTarget = serviceMethod.value();
                                if (!methodTarget.startsWith("/")) {
                                    methodTarget = "/" + methodTarget;
                                }
                                LOGGER.debug("[Method] The register processor address is {}", EventBusAddressUtil.positiveFormate(busAddressPrefix + methodTarget));
                                VertxUtil.getVertxInstance().deployVerticle(
                                        new ConsumerRegistryVerticle(toLowerCaseFirstOne(service.getSimpleName()),
                                                EventBusAddressUtil.positiveFormate(busAddressPrefix.concat(methodTarget))), new DeploymentOptions().setWorker(true));
                            }
                        }
                    } else {
                        LOGGER.debug("The register processor address is {}", EventBusAddressUtil.positiveFormate(busAddressPrefix));
                        VertxUtil.getVertxInstance().deployVerticle(new ConsumerRegistryVerticle(
                                toLowerCaseFirstOne(service.getSimpleName()), EventBusAddressUtil.positiveFormate(busAddressPrefix)), new DeploymentOptions().setWorker(true));
                    }
                }
            } catch (Exception e) {
                LOGGER.error("The {} Verticle register Service is fail，{}", service, e.getMessage());
            }
        }
    }

    /**
     * 获得Spring bean name，交给Spring来提取容器中的bean
     */
    private static String toLowerCaseFirstOne(String serviceName) {
        if (Character.isLowerCase(serviceName.charAt(0))) {
            return serviceName;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(serviceName.charAt(0))).append(serviceName.substring(1)).toString();
        }
    }
}
