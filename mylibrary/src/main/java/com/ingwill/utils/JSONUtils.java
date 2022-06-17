package com.ingwill.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by netcorner on 15/9/23.
 */
public class JSONUtils<TYPE> {
    public static Object getObjectByKey(JSONObject json, String key){
        if(json==null) return json;
        JSONObject jsonObject=json;
        Object obj;
        String[] arr=key.split("\\.");
        int index=0,len=arr.length-1;
        for(String k:arr){
            if(jsonObject.has(k)){
                try {
                    obj=jsonObject.get(k);
                    if(obj instanceof JSONObject){
                        jsonObject=(JSONObject)obj;
                    }else{
                        if(index<len){
                            return null;
                        }else{
                            return obj;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            index++;
        }
        return jsonObject;
    }


    /**
     * 对象转换成json字符串
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        return gson.toJson(obj);
    }

    /**
     * json字符串转成对象
     * @param str
     * @param type
     * @return
     */
    public static <T> T fromJson(String str, Type type) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        return gson.fromJson(str, type);
    }

    /**
     * 字符转hashmap
     * @param json
     * @return
     */
    public static Map<String,Object> fromJson(String json) {


        return gson.fromJson(json, typeToken);

//        Gson gson=new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").registerTypeAdapter(Map.class, new JsonDeserializer<Map<String,Object>>() {
//            public Map<String,Object>  deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
//                    throws JsonParseException {
//                Map<String,Object> resultMap=new HashMap<>();
//                JsonObject jsonObject = json.getAsJsonObject();
//                Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
//                for (Map.Entry<String, JsonElement> entry : entrySet) {
//                    resultMap.put(entry.getKey(), entry.getValue());
//                }
//                return resultMap;
//            }
//        }).create();
//        return gson.fromJson(str, Map.class);
    }



    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(
                    new TypeToken<TreeMap<String, Object>>(){}.getType(),
                    new JsonDeserializer<TreeMap<String, Object>>() {
                        @Override
                        public TreeMap<String, Object> deserialize(
                                JsonElement json, Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException {

                            TreeMap<String, Object> treeMap = new TreeMap<>();
                            JsonObject jsonObject = json.getAsJsonObject();
                            Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
                            for (Map.Entry<String, JsonElement> entry : entrySet) {
                                Object ot = entry.getValue();
                                if(ot instanceof JsonPrimitive){
                                    treeMap.put(entry.getKey(), ((JsonPrimitive) ot).getAsString());
                                }else{
                                    treeMap.put(entry.getKey(), ot);
                                }
                            }
                            return treeMap;
                        }
                    }).create();
    private static Type typeToken = new TypeToken<TreeMap<String, Object>>(){}.getType();



    /**
     * json字符串转成对象
     * @param str
     * @param type
     * @return
     */
    public static <T> T fromJson(String str, Class<T> type) {
        Gson gson =   new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

        return gson.fromJson(str, type);
    }


    /**
     * 得到对像
     * @param context
     * @param classType
     * @param <T>
     * @return
     */
    public static <T> T getObject(Context context, Class<T> classType, String key) {
        SharedPreferences pref = context.getSharedPreferences(
                key, 0);
        String response= pref.getString(key, "");
        if(response.equals("")) {
            return null;
        }
        else{
            return fromJson(response, classType);
        }
    }
    public static <T> T getObject(Context context, Class<T> classType) {
        return getObject(context,classType,classType.getName());
    }
    /**
     * 保存对象
     * @param context
     * @param key
     * @param val
     * @return
     */
    public static boolean saveObject(Context context, String key, String val) {
        SharedPreferences pref = context.getSharedPreferences(
                key, 0);
        return pref.edit().putString(key, val).commit();
    }
    public static boolean saveObject(Context context, Class<?> classType, String val){
        return saveObject(context,classType.getName(),val);
    }
    public static boolean saveObject(Context context, Class<?> classType, Object val){
        return saveObject(context,classType.getName(),toJson(val));
    }
    public static boolean saveObject(Context context, String key, Object val){
        return saveObject(context,key,toJson(val));
    }
    public static <T> void saveObject(Context ctx, T obj){
        String key=obj.getClass().getName();
        saveObject(ctx,key,obj);
    }

    public static <T> void clearGsonData(Context ctx, Class<?> classType){
        clearGsonData(ctx,classType.getName());
    }
    public static <T> void clearGsonData(Context ctx, String key){
        SharedPreferences pref = ctx.getSharedPreferences(key, 0);
        pref.edit().putString(key, "").commit();
    }

    /**
     * 读取资源文件并转成 json 格式
     * @param context
     * @param fileName
     * @return
     */
    public static  <T> T getAssetFile2Json(Context context,String fileName, Type type) {
        String str=  AssetUtils.getAssetFile2String(context,fileName);
        return fromJson(str,type);
    }
}
