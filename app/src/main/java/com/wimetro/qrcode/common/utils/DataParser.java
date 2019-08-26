package com.wimetro.qrcode.common.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.shellinfo.wall.remote.ParamMap;

public class DataParser {
	/**
	 * 将Map 数组转换为json 对象
	 * 
	 * @param
	 * @return
	 */

	public static JSONObject parseMap2JSON(ParamMap param) {
		try {
			String[] keyIter = param.keys();
			String key;
			Object value;
			JSONObject json = new JSONObject();
			for (int i = 0; i < keyIter.length; i++) {
				key = keyIter[i];
				value = param.get(key);
				json.put(key, value);
			}
			return json;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将String 数组转换为jsonArr 对象
	 * 
	 * @param
	 * @return
	 */

	public static JSONArray parseMap2JSONArr(String param) {
		if (param == null || param.length() == 0) {
			return null;
		}
		try {
			JSONArray jsonArr = new JSONArray(param);
			return jsonArr;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JSONObject parseJsonObjFromJsonArr(JSONArray jsonArr, int index) {
		if (jsonArr == null || jsonArr.length() == 0
				|| jsonArr.length() <= index) {
			return null;
		}
		try {
			JSONObject jsonObj = jsonArr.getJSONObject(index);
			return jsonObj;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将json 数组转换为Map 对象
	 * 
	 * @param
	 * @return
	 */

	public static ParamMap parseJSON2Map(JSONObject jsonObject) {
		try {
			Iterator<String> keyIter = jsonObject.keys();
			String key;
			Object value;
			ParamMap valueMap = new ParamMap();
			while (keyIter.hasNext()) {
				key = (String) keyIter.next();
				value = jsonObject.get(key);
				valueMap.put(key, value);
			}
			return valueMap;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 把json 转换为ArrayList 形式
	 * 
	 * @return
	 */

	public static List<ParamMap> parseJSON2MapList(JSONArray jsonArray) {
		List<ParamMap> list = null;
		try {
			JSONObject jsonObject;
			list = new ArrayList<ParamMap>();

			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
				list.add(parseJSON2Map(jsonObject));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	
	public final static char[] CS = "0123456789ABCDEF".toCharArray();

	public static void printdatas(String name, byte[] bs) {
		StringBuilder sb = new StringBuilder(bs.length * 3 + 50);
		sb.append(name).append(':');
		for (int n : bs) {
			sb.append(CS[(n >> 4) & 0xF]);
			sb.append(CS[(n >> 0) & 0xF]);
			sb.append(' ');
		}
		System.out.println(sb);
	}
}
