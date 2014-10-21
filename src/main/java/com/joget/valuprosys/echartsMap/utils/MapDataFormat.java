package com.joget.valuprosys.echartsMap.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.joget.valuprosys.echartsMap.EchartsMap;

public class MapDataFormat {
	private static final String QUATO = "'";
	
	/*
	 * public static String convertToStandardMapFormat(String data, String
	 * cityColumnName, String quantityColumnName) { JSONArray myJsonArray; try {
	 * myJsonArray = new JSONArray(data); JSONArray newJsonArray = new
	 * JSONArray(); for (int i = 0; i < myJsonArray.length(); i++) { JSONObject
	 * myjObject = myJsonArray.getJSONObject(i);
	 * 
	 * JSONObject newObject = new JSONObject();
	 * newObject.put(EchartsMap.CITY_ARRAY_NAME, myjObject.get(cityColumnName));
	 * newObject.put(EchartsMap.CITY_ARRAY_VALUE,
	 * myjObject.get(quantityColumnName)); newJsonArray.put(newObject); } return
	 * newJsonArray.toString(); } catch (JSONException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); return "[]"; }
	 * 
	 * // myJsonObject.toJSONArray(arg0)
	 * 
	 * }
	 */
	
	private static String convertToStandardMapFormatWithSingleSeries(String data, String seriesName, String seriesColumnName, String cityColumnName, String quantityColumnName) {
		JSONArray myJsonArray;
		try {
			myJsonArray = new JSONArray(data);
			JSONArray newJsonArray = new JSONArray();
			for (int i = 0; i < myJsonArray.length(); i++) {
				JSONObject myjObject = myJsonArray.getJSONObject(i);
				if (seriesName.equals(myjObject.get(seriesColumnName))) {
					JSONObject newObject = new JSONObject();
					newObject.put(EchartsMap.CITY_ARRAY_NAME, myjObject.get(cityColumnName));
					//数值部分不需要双引号，否则页面没法计算总和
					String quantity = myjObject.get(quantityColumnName).toString();
					if (quantity==null){
						quantity="0";
					}
					//newObject.put(EchartsMap.CITY_ARRAY_VALUE, quantity);
					newObject.put(EchartsMap.CITY_ARRAY_VALUE, Double.parseDouble(quantity));
					newJsonArray.put(newObject);
				}
			}
			return newJsonArray.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "[]";
		}

		// myJsonObject.toJSONArray(arg0)

	}
	private static List<String> getSeriesNames(String seriesColumnName, String data) throws JSONException {
		JSONArray myJsonArray = new JSONArray(data);
		List<String> seriesList = new ArrayList<String>();
		for (int i = 0; i < myJsonArray.length(); i++) {
			JSONObject myjObject = myJsonArray.getJSONObject(i);
			String series = myjObject.get(seriesColumnName).toString();
			// System.err.println(series);
			if (seriesList.indexOf(series) < 0) {
				// add to list
				seriesList.add(series);
			}
		}
		// System.err.println(seriesList.toString());
		return seriesList;
	}
	
	public static List<String> getSeriesNamesWithQuato(String seriesColumnName, String data) {
		List<String> seriesList = new ArrayList<String>();
		try {
			for (String series : getSeriesNames(seriesColumnName, data)) {
				seriesList.add(QUATO + series + QUATO);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;
		return seriesList;
	}
	
	/**
	 * 返回map格式的jsonstr的数组
	 * 
	 * @param data
	 * @param seriesColumnName
	 * @param cityColumnName
	 * @param quantityColumnName
	 * @return
	 */
	public static List<Map<String, String>> convertToStandardMapFormat(String data, String seriesColumnName, String cityColumnName, String quantityColumnName) {
//		System.err.println(data);
//		System.err.println(seriesColumnName);
//		System.err.println(cityColumnName);
//		System.err.println(quantityColumnName);
		// map中置放两个key，1. productName 2.转换格式后的json数据
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		// 得到序列清单
		JSONArray myJsonArray;
		try {
			myJsonArray = new JSONArray(data);
			List<String> seriesList = getSeriesNames(seriesColumnName, data);
			// 根据序列清单，得到相关符合格式的数据
			for (String seriesLabel : seriesList) {
				String jsonDataStr = convertToStandardMapFormatWithSingleSeries(data, seriesLabel, seriesColumnName, cityColumnName, quantityColumnName);
				Map<String, String> map = new HashMap<String, String>();
				map.put("seriesLabel", seriesLabel);
				map.put("data", jsonDataStr);
				result.add(map);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
