package com.xxx.anno;

import java.lang.annotation.*;

/**
 * Verticle 映射，用于发布每个处理器所对应的真实处理服务类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VerticleMapping {
    String value() default "";

    boolean isCluster() default true;

    boolean isServiceReg() default false;
}
