package com.ingwill.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ingwill.utils.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by netcorner on 15/9/27.
 */
public abstract class DALBase {
    private BaseSQLMap map;

    /**
     * 取得数据映射对象
     *
     * @return
     */
    public BaseSQLMap getMap() {
        return map;
    }


    /**
     * 构造
     * @param context
     */
    public DALBase(Context context,BaseSQLMap map) {
        this.context = context;
        this.map =map;
        this.mapTable = BaseSQLMap.getModelMap(getTable());
        this.tableName=mapTable.getName();
    }

    public Context getContext() {
        return context;
    }

    private Context context;

    /**
     * 取得表格名
     *
     * @return
     */
    public abstract Class<?> getTable();

    public String getTableName() {
        return tableName;
    }

    private String tableName;
    private MapTable mapTable;

    /**
     * 指删除记录
     * @param list
     * @return
     */
    public int delete(List list){
        getMap().getDb().beginTransaction();  //手动设置开始事务
        int result=0;
        try{

            for (Object friend:list){
                delete((Entity) friend);
                result++;
            }
            getMap().getDb().setTransactionSuccessful();

        }catch(Exception e){
            result=-1;
            e.printStackTrace();
        }finally{
            getMap().getDb().endTransaction(); //处理完成

        }
        return result;
    }
    /**
     * 删除单条记录
     * @param obj
     * @return
     */
    public int delete(Entity obj) {
        //if(obj.getClass().equals(getTable())){
            List<Field> list=mapTable.getPrimarys();
            if(list.size()>1) {
                Object[] args=new Object[list.size()];
                int i=0;
                for (Field f : list) {
                    args[i]=map.getEntityValue(getTable(),obj,mapTable.getFieldList().get(f.getName()));
                }
                return deleteByID(args);
            }else{
                Object arg;
                for (Field f : list) {
                    arg=map.getEntityValue(getTable(),obj,mapTable.getFieldList().get(f.getName()));
                    return deleteByID(arg);
                }
            }
        //}
        return 1;
    }

    /**
     * 批量更新
     * @param list
     * @return
     */
    public int update(List list){
        getMap().getDb().beginTransaction();  //手动设置开始事务
        int result=0;
        try{

            for (Object friend:list){
                update((Entity) friend);
                result++;
            }
            getMap().getDb().setTransactionSuccessful();

        }catch(Exception e){
            result=-1;
            e.printStackTrace();
        }finally{
            getMap().getDb().endTransaction(); //处理完成

        }
        return result;
    }

    /**
     * 单条更新
     * @param obj
     * @return
     */
    public int update(Entity obj) {
        //对于继承的类也可以插入数据
        //if(obj.getClass().equals(getTable())) {
            List<Field> list = mapTable.getPrimarys();

            if (list.size() > 1) {
                Object[] args = new Object[list.size()];
                int i = 0;
                for (Field f : list) {
                    args[i]=map.getEntityValue(getTable(),obj,mapTable.getFieldList().get(f.getName()));
                }
                return updateByID(entity2ContentValues(obj), args);
            } else {
                Object arg;
                for (Field f : list) {
                    arg=map.getEntityValue(getTable(),obj,mapTable.getFieldList().get(f.getName()));
                    return updateByID(entity2ContentValues(obj), arg);
                }
            }
        //}
        return -1;
    }

    public long insert(Entity obj){
        //对于继承的类也可以插入数据
        //if(obj.getClass().equals(getTable())) {
            return map.getDb().insert(tableName, null, entity2ContentValues(obj));
        //}
        //return -1;
    }

    /**
     * 批量插入多条数据
     * @param list
     * @return
     */
    public int insert(List list){
        getMap().getDb().beginTransaction();  //手动设置开始事务
        int result=0;
        try{

            for (Object friend:list){
                insert((Entity) friend);
                result++;
            }
            getMap().getDb().setTransactionSuccessful();

        }catch(Exception e){
            result=-1;
            e.printStackTrace();
        }finally{
            getMap().getDb().endTransaction(); //处理完成

        }
        return result;
    }

    public Object getEntity(String sql) {
        return map.getEntity(getTable(), mapTable, sql, null);
    }
    public Object getEntity(String sql, String[] args){
        return map.getEntity(getTable(), mapTable, sql, args);
    }

    public Object getEntityByID(Object id){
        return getEntityByID(new Object[]{id});
    }

    public Object getEntityByID(Object[] ids) {
        List<Field> list = mapTable.getPrimarys();
        String where = " 1=1 ";
        String[] args = null;
        int i = 0;
        if (list.size() > 0) {
            args = new String[list.size()];
            for (Field f : list) {
                where += " and " + f.getName() + "=?";
                args[i] = ids[i] + "";
                i++;
            }
        }

        List<Field> fields = mapTable.getFields();
        String[] columns = new String[fields.size()];
        i = 0;
        for (Field f : fields) {
            columns[i] = f.getName();
            i++;
        }
        Cursor cursor = map.getDb().query(tableName, columns, where, args, null, null, null);
        Object obj=null;
        if(cursor.moveToNext()){
            obj= map.getEntity(getTable(), mapTable, cursor);
        }
        cursor.close();
        return obj;
    }

    public List getAllEntity() {
        int i = 0;
        List<Field> fields = mapTable.getFields();

        String[] columns = new String[fields.size()];
        for (Field f : fields) {
            columns[i] = f.getName();
            i++;
        }
        Cursor cursor = map.getDb().query(tableName, columns, null, null, null, null, null);
        List lst= map.getEntityList(getTable(),mapTable,cursor);
        return lst;
    }

    public List getListEntity(String where, String[] args) {
        int i = 0;
        List<Field> fields = mapTable.getFields();

        String[] columns = new String[fields.size()];
        for (Field f : fields) {
            columns[i] = f.getName();
            i++;
        }
        Cursor cursor = map.getDb().query(tableName, columns, where, args, null, null, null);
        List lst= map.getEntityList(getTable(),mapTable,cursor);
        return lst;
    }
    public List getListEntity(String where) {
        return getListEntity(where,null);
    }


    public List queryForEntityList(String sql, String[] args) {
        Cursor cursor=map.getDb().rawQuery(sql, args);
        List list= map.getEntityList(getTable(),mapTable,cursor);
        return list;

    }

    public List queryForEntityList(String sql) {
        return queryForEntityList(sql, null);
    }





    /**
     * 数据删除
     *
     * @param where
     * @param args
     * @return
     */
    public int delete(String where, String[] args) {
        return map.getDb().delete(tableName, where, args);
    }

    /**
     * 数据删除
     *
     * @param where
     * @return
     */
    public int delete(String where) {
        return map.getDb().delete(tableName, where, null);
    }

    /**
     * 数据删除
     *
     * @param id
     * @return
     */
    public int deleteByID(Object id) {
        return deleteByID(new Object[]{id});
    }

    /**
     * 数据删除
     *
     * @param ids
     * @return
     */
    public int deleteByID(Object[] ids) {
        List<Field> list = mapTable.getPrimarys();
        String where = " 1=1 ";
        String[] args = null;
        if (list.size() > 0) {
            args = new String[list.size()];
            int i = 0;
            for (Field f : list) {
                where += " and " + f.getName() + "=?";
                args[i] = ids[i] + "";
                i++;
            }
        }
        return map.getDb().delete(tableName, where, args);
    }

    /**
     * 数据删除
     * @param columns
     * @return
     */
    public int delete(Map<String, Object> columns) {
        List<Field> list=mapTable.getPrimarys();
        if(list.size()>1) {
            Object[] args=new Object[list.size()];
            int i=0;
            for (Field f : list) {
                args[i]=columns.get(f.getName());
            }
            return deleteByID(args);
        }else{
            for (Field f : list) {
                return deleteByID(columns.get(f.getName()));
            }
        }
        return -1;
    }


    /**
     * 删除所有数据
     *
     * @return
     */
    public int deleteAll() {
        return map.getDb().delete(tableName, "", null);
    }


    /**
     * 数据更新
     *
     * @param columns
     * @param where
     * @param args
     * @return
     */
    public int update(ContentValues columns, String where, String[] args) {
        return map.getDb().update(tableName, columns, where, args);
    }

    /**
     * 数据更新
     *
     * @param columns
     * @param where
     * @param args
     * @return
     */
    public int update(Map<String, Object> columns, String where, String[] args) {
        return map.getDb().update(tableName, map2ContentValues(columns), where, args);
    }

    /**
     * 数据更新
     *
     * @param columns
     * @param id
     * @return
     */
    public int updateByID(ContentValues columns, Object id) {
        return updateByID(columns, new Object[]{id});
    }

    /**
     * 数据更新
     *
     * @param columns
     * @param id
     * @return
     */
    public int updateByID(Map<String, Object> columns, Object id) {
        return updateByID(map2ContentValues(columns), new Object[]{id});
    }

    /**
     * 数据更新
     *
     * @param columns
     * @param ids
     * @return
     */
    public int updateByID(ContentValues columns, Object[] ids) {
        List<Field> list = mapTable.getPrimarys();
        String where = " 1=1 ";
        String[] args = null;
        if (list.size() > 0) {
            args = new String[list.size()];
            int i = 0;
            for (Field f : list) {
                where += " and " + f.getName() + "=?";
                args[i] = ids[i] + "";
                i++;
            }
        }
        return map.getDb().update(tableName, columns, where, args);
    }

    /**
     * 数据更新
     *
     * @param columns
     * @param ids
     * @return
     */
    public int updateByID(Map<String, Object> columns, Object[] ids) {
        return updateByID(map2ContentValues(columns), ids);
    }

    /**
     * 数据更新
     * @param columns
     * @return
     */
    public int update(Map<String, Object> columns) {
        List<Field> list=mapTable.getPrimarys();
        if(list.size()>1) {
            Object[] args=new Object[list.size()];
            int i=0;
            for (Field f : list) {
                args[i]=columns.get(f.getName());
            }
            return updateByID(map2ContentValues(columns), args);
        }else{
            for (Field f : list) {
                return updateByID(map2ContentValues(columns), columns.get(f.getName()));
            }
        }
        return -1;
    }

    /**
     * 数据插入
     *
     * @param columns
     * @return
     */
    public long insert(ContentValues columns) {
        return map.getDb().insert(tableName, null, columns);
    }



    /**
     * 数据插入
     *
     * @param columns
     * @return
     */
    public long insert(Map<String, Object> columns) {
        return map.getDb().insert(tableName, null, map2ContentValues(columns));
    }

    /**
     * contentvalues转map
     *
     * @param columns
     * @return
     */
    public Map<String, Object> contentValues2Map(ContentValues columns) {
        Map<String, Object> hash = new HashMap<String, Object>();
        for (Map.Entry<String, Object> obj : columns.valueSet()) {
            hash.put(obj.getKey(), obj.getValue());
        }
        return hash;
    }

    /**
     * map转contentvalues
     *
     * @param columns
     * @return
     */
    public ContentValues map2ContentValues(Map<String, Object> columns) {
        return map2ContentValues(columns, tableName);
    }

    /**
     * map转contentvalues
     *
     * @param columns
     * @return
     */
    public ContentValues map2ContentValues(Map<String, Object> columns, String table) {
        ContentValues hash = new ContentValues();
        for (Map.Entry<String, Object> obj : columns.entrySet()) {
            if (obj.getValue() instanceof List) {

            } else if (obj.getValue() instanceof Map) {

            } else {
                if (mapTable.getFieldList().containsKey(obj.getKey())) {
                    if(obj.getValue()!=null) {
                        hash.put(obj.getKey(), obj.getValue() + "");
                    }else{
                        hash.put(obj.getKey(), "");
                    }
                }
            }
        }
        return hash;
    }

    public ContentValues entity2ContentValues(Entity obj) {
        return entity2ContentValues(obj,getTableName());
    }

    public ContentValues entity2ContentValues(Entity obj, String table) {
        ContentValues hash = new ContentValues();
        for(Field f:mapTable.getFields()){
            Object o=map.getEntityValue(getTable(), obj, mapTable.getFieldList().get(f.getName()));
            if(o==null) {
                hash.put(f.getName(),"");
            }else {
                if (o instanceof Date) {
                    o = DateUtils.getDate((Date) o, "yyyy-MM-dd HH:mm:ss");
                }
                hash.put(f.getName(), o + "");
            }
        }
        return hash;
    }






    /**
     * json转contentvalues
     *
     * @param columns
     * @return
     */
    public ContentValues json2ContentValues(JSONObject columns) {
        ContentValues hash = new ContentValues();
        Iterator it = columns.keys();
        String tmp;
        while (it.hasNext()) {
            String key = (String) it.next();
            Object obj = null;
            try {
                obj = columns.get(key);
                if (obj instanceof JSONArray) {

                } else if (obj instanceof JSONObject) {

                } else if(obj==null) {

                }
                else
                {


                    if (mapTable.getFieldList().containsKey(key)) {
                        tmp=obj+"";
                        if(!tmp.equals("null")) {
                            hash.put(key, obj + "");
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return hash;
    }

    /**
     * 通过id得到单条数据
     *
     * @param id
     * @return
     */
    public Map<String, Object> getByID(Object id) {
        return getByID(new Object[]{id});
    }

    /**
     * 通过id得到单条数据
     *
     * @param ids
     * @return
     */
    public Map<String, Object> getByID(Object[] ids) {
        List<Field> list = mapTable.getPrimarys();
        String where = " 1=1 ";
        String[] args = null;
        int i = 0;
        if (list.size() > 0) {
            args = new String[list.size()];
            for (Field f : list) {
                where += " and " + f.getName() + "=?";
                args[i] = ids[i] + "";
                i++;
            }
        }

        List<Field> fields = mapTable.getFields();
        String[] columns = new String[fields.size()];
        i = 0;
        for (Field f : fields) {
            columns[i] = f.getName();
            i++;
        }
        Cursor cursor = map.getDb().query(tableName, columns, where, args, null, null, null);
        Map<String, Object> obj=null;
        if(cursor.moveToNext()) {
            obj= map.getHash(cursor);
        }
        cursor.close();
        return obj;
    }



    /**
     * 得到表中的所有记录
     *
     * @return
     */
    public List<Map<String, Object>> getAll() {
        List<Field> list = mapTable.getPrimarys();
        int i = 0;
        List<Field> fields = mapTable.getFields();

        String[] columns = new String[fields.size()];
        for (Field f : fields) {
            columns[i] = f.getName();
            i++;
        }
        Cursor cursor = map.getDb().query(tableName, columns, null, null, null, null, null);
        List<Map<String, Object>> lst= map.getList(cursor);
        return lst;
    }











    /**
     * 取单条数据
     *
     * @param sql
     * @return
     */
    public Map<String, Object> get(String sql) {
        return map.get(sql, null);
    }

    /**
     * 取单条数据
     *
     * @param sql
     * @param args
     * @return
     */
    public Map<String, Object> get(String sql, String[] args) {
        return map.get(sql, args);
    }

    /**
     * 取多条数据
     *
     * @param sql
     * @param args
     * @return
     */
    public List<Map<String, Object>> queryForList(String sql, String[] args) {
        return map.queryForList(sql, args);
    }

    /**
     * 取多条数据
     *
     * @param sql
     * @return
     */
    public List<Map<String, Object>> queryForList(String sql) {
        return map.queryForList(sql, null);
    }

    /**
     * 设置添加子节点
     *
     * @param parent
     * @param parentPrimaryKey
     * @param children
     * @param childrenForeignKey
     */
    public void setChildren(String childrenName, Map<String, Object> parent, String parentPrimaryKey, List<Map<String, Object>> children, String childrenForeignKey) {
        if(parent==null) return;
        if (parent.containsKey(parentPrimaryKey)) {
            String id = parent.get(parentPrimaryKey) + "";
            if (children != null) {
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                for (Map<String, Object> obj : children) {
                    if (obj.containsKey(childrenForeignKey)) {
                        if ((obj.get(childrenForeignKey) + "").equals(id)) {
                            list.add(obj);
                        }
                    }
                }
                if (list.size() > 0) {
                    parent.put(childrenName, list);
                }
            }
        }
    }

    /**
     * 设置添加子节点
     * @param childrenName
     * @param parents
     * @param parentPrimaryKey
     * @param children
     * @param childrenForeignKey
     */
    public void setChildren(String childrenName, List<Map<String, Object>> parents, String parentPrimaryKey, List<Map<String, Object>> children, String childrenForeignKey) {
        if(parents==null) return;
        for (Map<String, Object> parent : parents) {
            setChildren(childrenName, parent, parentPrimaryKey, children, childrenForeignKey);
        }
    }
}
