package com.ingwill.widget.wheel.entity;

/**
 * Created by shijiufeng on 2018/7/9.
 */

public class KeyValue {
    private String key;
    private Object value;
    private Object primaryValue;

    public Object getPrimaryValue() {
        return primaryValue;
    }

    public void setPrimaryValue(Object primaryValue) {
        this.primaryValue = primaryValue;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
