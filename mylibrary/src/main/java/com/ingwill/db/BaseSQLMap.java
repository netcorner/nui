package com.ingwill.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ingwill.lib.Validator;
import com.ingwill.utils.DateUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by netcorner on 15/9/26.
 */
public abstract class BaseSQLMap  {
    private static BaseSQLMap sqlDB;

    public SQLiteDatabase getDb() {
        return db;
    }
    public  void setDb(SQLiteDatabase db){this.db=db;}

    private SQLiteDatabase db;



    /**
     * 取单条数据,可直接调用
     * @param sql
     * @param args
     * @return
     */
    public Map<String,Object> get(String sql, String[] args){
        Cursor cursor= db.rawQuery(sql, args);
        Map<String,Object> obj=null;
        if(cursor.moveToNext()){
            obj= getHash(cursor);
        }
        cursor.close();
        return obj;
    }


    /**
     * 取单条数据，不能直接调用
     * @param entityType
     * @param mapTable
     * @param sql
     * @param args
     * @return
     */
    public Object getEntity(Class<?> entityType, MapTable mapTable, String sql, String[] args)  {
        Cursor cursor= db.rawQuery(sql, args);
        Object obj=null;
        if(cursor.moveToNext()){
            obj =getEntity(entityType, mapTable, cursor);
        }
        return obj;
    }

    /**
     * 取单条数据
     * @param sql
     * @return
     */
    public Map<String,Object> get(String sql){
        return get(sql,null);
    }
    /**
     * 取多条数据
     * @param sql
     * @return
     */
    public List<Map<String,Object>> queryForList(String sql) {
        return queryForList(sql,null);
    }
    /**
     * 取多条数据
     * @param sql
     * @param args
     * @return
     */
    public List<Map<String,Object>> queryForList(String sql, String[] args){
        Cursor cursor=db.rawQuery(sql, args);
        List<Map<String,Object>> list= getList(cursor);
        return list;
    }



    /**
     * 得到列表
     * @param cursor
     * @return
     */
    public List<Map<String,Object>> getList(Cursor cursor){
        List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
        while (cursor.moveToNext()) {
            list.add(getHash(cursor));
        }
        cursor.close();
        return list;
    }
    public List getEntityList(Class<?> entityType, MapTable mapTable, Cursor cursor){
        List list=new ArrayList();
        while (cursor.moveToNext()) {
            list.add(getEntity(entityType, mapTable, cursor));
        }
        cursor.close();
        return list;
    }
    /**
     * 得到hashtable
     * @param cursor
     * @return
     */
    public Map<String,Object> getHash(Cursor cursor){
        if(cursor.getCount()>0) {
            Map<String, Object> hash;
            String key;
            int index;
            hash = new HashMap<String, Object>();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                key = cursor.getColumnName(i);
                index = cursor.getColumnIndex(key);
                switch (DbCompat.getType(cursor, index)) {
                    case DbCompat.FIELD_TYPE_BLOB:
                        hash.put(key, cursor.getBlob(index));
                        break;
                    case DbCompat.FIELD_TYPE_FLOAT:
                        hash.put(key, cursor.getFloat(index));
                        break;
                    case DbCompat.FIELD_TYPE_INTEGER:
                        hash.put(key, cursor.getInt(index));
                        break;
                    case DbCompat.FIELD_TYPE_NULL:
                        hash.put(key, null);
                        break;
                    case DbCompat.FIELD_TYPE_STRING:
                        hash.put(key, cursor.getString(index));
                        break;
                }
            }
            return hash;
        }else{
            return null;
        }
    }

    public Object getEntity(Class<?> entityType, MapTable mapTable, Cursor cursor) {
        if(cursor.getCount()>0) {
            Object hash=null;
            String key;
            int index;
            try {
                hash = entityType.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < cursor.getColumnCount(); i++) {
                key = cursor.getColumnName(i);
                index = cursor.getColumnIndex(key);
                if(mapTable.getFieldList().containsKey(key)) {
                    java.lang.reflect.Field t=mapTable.getFieldList().get(key);
                    Method method=null;
                    try {
                        method=entityType.getDeclaredMethod("set"+t.getName(),new Class[]{t.getType()});
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    if(method!=null) {
                        if (t.getType().equals(String.class)) {
                            try {
                                String s=cursor.getString(index);
//                                if(s.equals("null")){
//                                    s=null;
//                                }
                                method.invoke(hash,new Object[]{s});
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }

                        } else if (t.getType().equals(int.class)||t.getType().equals(Integer.class)) {
                            try {
                                method.invoke(hash,new Object[]{cursor.getInt(index)});
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }

                        } else if (t.getType().equals(float.class)||t.getType().equals(Float.class)) {
                            try {
                                method.invoke(hash,new Object[]{cursor.getFloat(index)});
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        } else if (t.getType().equals(Double.class)||t.getType().equals(double.class)) {
                            try {
                                method.invoke(hash,new Object[]{cursor.getDouble(index)});
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        } else if (t.getType().equals(byte[].class)||t.getType().equals(Byte[].class)) {
                            try {
                                method.invoke(hash,new Object[]{cursor.getBlob(index)});
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        } else if (t.getType().equals(long.class)||t.getType().equals(Long.class)) {
                            try {
                                method.invoke(hash,new Object[]{cursor.getLong(index)});
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        } else if (t.getType().equals(short.class)||t.getType().equals(Short.class)) {
                            try {
                                method.invoke(hash,new Object[]{cursor.getShort(index)});
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        } else if (t.getType().equals(Date.class)) {
                            String v=cursor.getString(index);
                            if(Validator.isMatch(v,Validator.DATETIME)||Validator.isMatch(v,Validator.DATE)) {
                                try {
                                    method.invoke(hash, new Object[]{DateUtils.string2Date(v)});
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
            return hash;
        }else{
            return null;
        }
    }


    public Object getEntityValue(Class<?> entityType, Object instance, java.lang.reflect.Field f){
        Method method=null;
        try {
            method=entityType.getDeclaredMethod("get"+f.getName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if(method!=null) {
            try {
                return method.invoke(instance);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public int delete(String table, String where , String[] args){
        return db.delete(table,where,args);
    }
    public int update(String table, ContentValues columns, String where , String[] args){
        return db.update(table, columns, where, args);
    }
    public long insert(String table, ContentValues columns){
        return db.insert(table, null, columns);
    }
    public int delete(String table, String where ){
        return db.delete(table,where,null);
    }
    public int update(String table, ContentValues columns, String where){
        return db.update(table, columns, where, null);
    }


    /**
     * 得到模型数据映射
     * @param classname
     * @return
     */
    public static MapTable getModelMap(String classname){
        Class onwClass=null;
        try {
            onwClass = Class.forName(classname);
            return getModelMap(classname);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Map<Class<?>,MapTable> beanMapTable=new HashMap<Class<?>,MapTable>();

    /**
     * 得到模型数据映射
     * @param onwClass
     * @return
     */
    public static MapTable getModelMap(Class<?> onwClass){
        if(!beanMapTable.containsKey(onwClass)) {
            MapTable mapTable = new MapTable();
            mapTable.setName(onwClass.getSimpleName());
            for (Annotation ann : onwClass.getDeclaredAnnotations()) {
                if (ann.annotationType().equals(Table.class)) {
                    Table t = (Table) ann;
                    if(!t.value().equals("")) mapTable.setName(t.value());
                }
            }
            mapTable.setName(mapTable.getName().toLowerCase());

            //一定得有Column注解才会和数据库对应

            for (java.lang.reflect.Field field : onwClass.getDeclaredFields()) {
                Field f = new Field();
                //一定有Column注解才会写入到数据库中
                if(field.getDeclaredAnnotations().length>0) {
                    mapTable.getFields().add(f);
                    f.setName(field.getName());
                    f.setType(javaType2SqliteType(field.getType()) + "");
                    for (Annotation ann : field.getDeclaredAnnotations()) {
                        if (ann.annotationType().equals(Column.class)) {
                            Column t = (Column) ann;
                            if (!t.name().equals("")) f.setName(t.name());
                            if (!t.type().equals("")) f.setType(t.type());
                            f.setIsPrimary(t.isPrimary());
                            if (t.isPrimary()) mapTable.getPrimarys().add(f);
                            f.setAuto(t.auto());
                        }
                    }
                    if (!mapTable.getFieldList().containsKey(field.getName())) {
                        mapTable.getFieldList().put(f.getName(), field);
                    }
                }
            }
            beanMapTable.put(onwClass, mapTable);
        }
        return beanMapTable.get(onwClass);
    }

    /**
     * java类型转成sqlite类型
     * @param javaType
     * @return
     */
    private static String javaType2SqliteType(Class<?> javaType){
        if(javaType.equals(int.class)||javaType.equals(long.class)||javaType.equals(short.class))
            return "integer";
        else if(javaType.equals(float.class))
            return "float";
        else if(javaType.equals(double.class))
            return "double";
        else if(javaType.equals(Date.class))
            return "datetime";
        else if(javaType.equals(byte[].class))
            return "blob";
        else
            return "text";
    }

    /**
     * 得到创建表格的sql语句
     * @param onwClass
     * @return
     */
    public static String getCreateSql(Class<?> onwClass){
        MapTable mapTable= BaseSQLMap.getModelMap(onwClass);
        boolean flag=false;
        String sql="create table "+mapTable.getName()+"(";
        for (Field field:mapTable.getFields()){
            if(flag) sql+=",";
            sql+=field.getName()+" "+field.getType();
            if(field.getIsPrimary()){
                sql+=" primary key";
            }
            if(field.isAuto()){
                sql+=" autoincrement";
            }
            flag=true;
        }
        sql+=")";
        return sql;
    }

}
