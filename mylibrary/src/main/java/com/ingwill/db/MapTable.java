package com.ingwill.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by netcorner on 15/10/20.
 */
public class MapTable {
    private String name;
    private List<Field> fields=new ArrayList<Field>();
    private List<Field> primarys=new ArrayList<Field>();
    private Map<String, java.lang.reflect.Field> fieldList=new HashMap<String, java.lang.reflect.Field>();



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Field> getFields() {
        return fields;
    }
    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Field> getPrimarys() {
        return primarys;
    }
    public void setPrimarys(List<Field> primarys) {
        this.primarys = primarys;
    }

    public Map<String,  java.lang.reflect.Field> getFieldList() {
        return fieldList;
    }

    public void setFieldList(Map<String,  java.lang.reflect.Field> fieldList) {
        this.fieldList = fieldList;
    }
}
