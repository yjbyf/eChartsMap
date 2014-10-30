package com.joget.valuprosys.echartsMap.utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.joget.valuprosys.echartsMap.EchartsMap;
import com.joget.valuprosys.echartsMap.database.DaoUtils;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class RegionUtils {

	private static final String SPACE = " ";
	private static final String UNION = "union";
	private final static String SPLIT = ";";
	private final static String COMMA = ",";
	private final static String GROUP = "group";

	public static String getQuerySql(String sql, String regionName, String regionDefine, String colorDefine, String regionLocationColumnName) {
		String processedSql = sql;
		String result = "";
		if (processedSql.endsWith(SPLIT)) {
			processedSql = processedSql.substring(0, processedSql.length() - 1);
		}
		String regionNameList[] = regionName.split(SPLIT);
		String regionDefineList[] = regionDefine.split(SPLIT);
		String colorDefineList[] = colorDefine.split(SPLIT);
		for (int i = 0; i < regionNameList.length; i++) {
			// 得到当前地区定义的省市列表
			String quatoCitiesDefine = addQuatoToCitiesDefine(regionDefineList[i]);
			String insertBeforeGroupBy = processedSql.substring(0, processedSql.toLowerCase().indexOf(GROUP) - 1) + SPACE + " and " + regionLocationColumnName + " in (" + quatoCitiesDefine + ")"
					+ SPACE + processedSql.substring(processedSql.toLowerCase().indexOf(GROUP));
			result = result + " select '" + regionNameList[i] + "' as " + regionLocationColumnName + "," + "'" + colorDefineList[i] + "' as " + EchartsMap.AREA_COLOR + ",t.* from ("
					+ insertBeforeGroupBy + ") t " + UNION;
		}
		if (result.endsWith(UNION)) {
			result = result.substring(1, result.length() - UNION.length());
		}
		return result;
	}

	public static String getRegionColor(String regionDefine, String definedColor) {
		String regionDefineList[] = regionDefine.split(SPLIT);
		String definedColorList[] = definedColor.split(SPLIT);
		JSONArray array = new JSONArray();
		for (int i = 0; i < regionDefineList.length; i++) {
			String cities = regionDefineList[i];
			String citiesList[] = cities.split(COMMA);
			for (int j = 0; j < citiesList.length; j++) {
				try {
					JSONObject jsonObj = new JSONObject();
					// System.err.println(j+citiesList[j]);
					jsonObj.put(citiesList[j], definedColorList[i]);
					array.put(jsonObj);
					// System.err.println(array.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return array.toString();
	}

	/**
	 * 城市列表加上单引号
	 * 
	 * @param citiesDefine
	 * @return
	 */
	public static String addQuatoToCitiesDefine(String citiesDefine) {
		String regionCities[] = citiesDefine.split(COMMA);
		String processedCities[] = new String[regionCities.length];
		for (int j = 0; j < regionCities.length; j++) {
			String processedCity = "'" + regionCities[j] + "',";
			processedCities[j] = processedCity;
		}
		String result = convertStringArrayToString(processedCities);
		if (result.endsWith(COMMA)) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	public static String queryRegionData(String sql) {
		String result = DaoUtils.execute(sql);
		return result;
	}

	private static String convertStringArrayToString(String[] strArr) {
		StringBuilder sb = new StringBuilder();
		for (String str : strArr)
			sb.append(str);
		return sb.toString();
	}

	public static void main(String[] args) {
		String sql = "select product ,sum(price) quantity from oe_order_all where 1=1 group by product;";
		String regionName = "西北区;西南区";
		String regionDefine = "新疆,内蒙古;西藏,四川";
		String colorDefine = "red;yellow";
		System.err.println(RegionUtils.getQuerySql(sql, regionName, regionDefine, colorDefine, "location"));

		String color = "red;yellow";
		System.err.println(RegionUtils.getRegionColor(regionDefine, color));

		System.err.println("-------------testAddCityToResult-------------------------");
		RegionUtils.testAddCityToResult();
		
		System.err.println("-------------testTemplate-------------------------");
		try {
			RegionUtils.test();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * try { RegionUtils.test(); } catch (IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch
		 * (TemplateException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (JSONException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
	}
	
	

	public static String addCitesToJsonResult(String jsonResult, String regions, String cities, String regionColumnName) {
		String result = "";
		String[] citiesList = cities.split(SPLIT);
		String[] regionsList = regions.split(SPLIT);

		try {
			JSONArray resultJsonArray = new JSONArray();
			JSONArray jsonArray = new JSONArray(jsonResult);
			for (int j = 0; j < jsonArray.length(); j++) {// 遍历结果数据
				JSONObject jsonObject = jsonArray.getJSONObject(j);
				String region = jsonObject.getString(regionColumnName);
				for (int i = 0; i < regionsList.length; i++) {// 遍历大区定义，加上城市
					if (regionsList[i].equals(region)) {// 找到匹配大区
						// 加入大区下的所有城市
						String allCities = citiesList[i];
						String[] allCitiesList = allCities.split(COMMA);
						for (int k = 0; k < allCitiesList.length; k++) {
							JSONObject newObject = new JSONObject(jsonObject, JSONObject.getNames(jsonObject));// 复制原有json对象
							newObject.put(EchartsMap.CITY, allCitiesList[k]);// 加入城市信息
							resultJsonArray.put(newObject);// 加入数组
						}

					}
				}
			}
			return resultJsonArray.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static void testAddCityToResult() {
		String testStr = "[{\"product\":\"冰箱\",\"areaColor\":\"red\",\"location\":\"西北区\",\"quantity\":\"36393\"},{\"product\":\"洗衣机\",\"areaColor\":\"red\",\"location\":\"西北区\",\"quantity\":\"36884\"},{\"product\":\"空调\",\"areaColor\":\"red\",\"location\":\"西北区\",\"quantity\":\"33395\"},{\"product\":\"冰箱\",\"areaColor\":\"blue\",\"location\":\"西南区\",\"quantity\":\"33836\"},{\"product\":\"洗衣机\",\"areaColor\":\"blue\",\"location\":\"西南区\",\"quantity\":\"37604\"},{\"product\":\"空调\",\"areaColor\":\"blue\",\"location\":\"西南区\",\"quantity\":\"34647\"}]";
		String cities = "新疆,内蒙古;西藏,四川";
		String regions = "西北区;西南区";
		System.err.println(addCitesToJsonResult(testStr, regions, cities, "location"));
	}
	

	public static void test() throws IOException, TemplateException, JSONException {
		String seriesColumnName = "product";
		String data = "[{\"product\":\"冰箱\",\"areaColor\":\"red\",\"location\":\"西北区\",\"quantity\":\"36393\",\"city\":\"新疆\"},{\"product\":\"冰箱\",\"areaColor\":\"red\",\"location\":\"西北区\",\"quantity\":\"36393\",\"city\":\"内蒙古\"},{\"product\":\"洗衣机\",\"areaColor\":\"red\",\"location\":\"西北区\",\"quantity\":\"36884\",\"city\":\"新疆\"},{\"product\":\"洗衣机\",\"areaColor\":\"red\",\"location\":\"西北区\",\"quantity\":\"36884\",\"city\":\"内蒙古\"},{\"product\":\"空调\",\"areaColor\":\"red\",\"location\":\"西北区\",\"quantity\":\"33395\",\"city\":\"新疆\"},{\"product\":\"空调\",\"areaColor\":\"red\",\"location\":\"西北区\",\"quantity\":\"33395\",\"city\":\"内蒙古\"},{\"product\":\"冰箱\",\"areaColor\":\"blue\",\"location\":\"西南区\",\"quantity\":\"33836\",\"city\":\"西藏\"},{\"product\":\"冰箱\",\"areaColor\":\"blue\",\"location\":\"西南区\",\"quantity\":\"33836\",\"city\":\"四川\"},{\"product\":\"洗衣机\",\"areaColor\":\"blue\",\"location\":\"西南区\",\"quantity\":\"37604\",\"city\":\"西藏\"},{\"product\":\"洗衣机\",\"areaColor\":\"blue\",\"location\":\"西南区\",\"quantity\":\"37604\",\"city\":\"四川\"},{\"product\":\"空调\",\"areaColor\":\"blue\",\"location\":\"西南区\",\"quantity\":\"34647\",\"city\":\"西藏\"},{\"product\":\"空调\",\"areaColor\":\"blue\",\"location\":\"西南区\",\"quantity\":\"34647\",\"city\":\"四川\"}]";

		List<Map<String, String>> list = MapDataFormat.convertToStandardMapFormat(data, seriesColumnName, "city", "quantity");

		System.err.println(list.toString());
		System.err.println("------------------------------------------");
		for (Map<String, String> map : list) {
			for (String k : map.keySet()) {
				System.err.print(k + ":");
				System.err.println(map.get(k));
			}
		}

		Configuration cfg = new Configuration();
		// 指定模板文件从何处加载的数据源，这里设置成一个文件目录。
		cfg.setDirectoryForTemplateLoading(new File("D:\\workspace\\eChartsMap\\src\\main\\resources\\templates"));
		// 指定模板如何检索数据模型，这是一个高级的主题了…
		// 但先可以这么来用：
		cfg.setObjectWrapper(new DefaultObjectWrapper());

		// 创建根哈希表
		Map<String, Object> root = new HashMap<String, Object>();

		Template temp = cfg.getTemplate("regionData.ftl");

		Writer out = new OutputStreamWriter(System.out);

		root.put("regionList", list);
		root.put("allSeriesLabels", MapDataFormat.getSeriesNamesWithQuato(seriesColumnName, data).toString());

		//temp.process(root, out);
		out.flush();
	}

}
