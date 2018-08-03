package com.xxx.service.impl;

import com.xxx.anno.VerticleMapping;
import com.xxx.service.TestService;
import io.vertx.core.json.JsonObject;
import org.springframework.stereotype.Service;

/**
 * @author Xu Haidong
 * @date 2018/8/2
 */
@VerticleMapping
@Service
public class TestServiceImpl implements TestService {

    @Override
    public JsonObject doTest(JsonObject param) {
        return param;
    }
}
