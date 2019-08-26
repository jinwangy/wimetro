package com.wimetro.qrcode.common.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;

/**
 * jwyuan on 2017/5/26 16:52.
 */

public class PreferencesManager {

    //获取Editor默认实例
    private static SharedPreferences.Editor getEditor(Context context) {
        return getSharedPreferences(context).edit();
    }

    //获取Editor实例
    private static SharedPreferences.Editor getEditor(Context context, String name) {
        return getSharedPreferences(context, name).edit();
    }

    //获取SharedPreferences默认实例
    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    //获取SharedPreferences实例
    private static SharedPreferences getSharedPreferences(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param key
     * @param object
     */
    public static void put(Context context, String key, Object object){
        SharedPreferences sp = getSharedPreferences(context);
        SharedPreferences.Editor editor = getEditor(context);
        if (object instanceof String){
            editor.putString(key, (String) object);
        } else if (object instanceof Integer){
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean){
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float){
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long){
            editor.putLong(key, (Long) object);
        } else{
            editor.putString(key, object.toString());
        }
        editor.commit();
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object get(Context context, String key, Object defaultObject){
        SharedPreferences sp = getSharedPreferences(context);
        if (defaultObject instanceof String){
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer){
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean){
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float){
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long){
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    /**
     * 移除某个key值已经对应的值
     * @param context
     * @param key
     */
    public static void remove(Context context, String key){
        SharedPreferences sp = getSharedPreferences(context);
        SharedPreferences.Editor editor = getEditor(context);
        editor.remove(key);
        editor.commit();
    }

    /**
     * 清除所有数据
     * @param context
     */
    public static void clear(Context context){
        SharedPreferences sp = getSharedPreferences(context);
        SharedPreferences.Editor editor = getEditor(context);
        editor.clear();
        editor.commit();
    }

    /**
     * 查询某个key是否已经存在
     * @param context
     * @param key
     * @return
     */
    public static boolean contains(Context context, String key){
        SharedPreferences sp = getSharedPreferences(context);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @param context
     * @return
     */
    public static Map<String, ?> getAll(Context context){
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getAll();
    }
}
