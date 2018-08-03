package com.xxx.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * @author Xu Haidong
 * @date 2018/8/2
 */
public class BaseEntity<T extends Model> extends Model<T> {

    private long testId;

    public long getTestId() {
        return testId;
    }

    public void setTestId(long testId) {
        this.testId = testId;
    }

    @Override
    protected Serializable pkVal() {
        return testId;
    }
}
