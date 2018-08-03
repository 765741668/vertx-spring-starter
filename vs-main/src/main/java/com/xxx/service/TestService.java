package com.xxx.service;

import io.vertx.core.json.JsonObject;

/**
 * @author Xu Haidong
 * @date 2018/8/2
 */
public interface TestService {

    JsonObject doTest(JsonObject param);
}
