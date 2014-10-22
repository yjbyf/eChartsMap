package com.joget.valuprosys.echartsMap.utils;

import com.joget.valuprosys.echartsMap.database.DaoUtils;

public class RegionUtils {

	private static final String SPACE = " ";
	private static final String UNION = "union";
	private final static String SPLIT = ";";
	private final static String COMMA = ",";
	private final static String GROUP = "group";

	public static String getQuerySql(String sql, String regionName,
			String regionDefine, String regionLocationColumnName) {
		String processedSql = sql;
		String result = "";
		if (processedSql.endsWith(SPLIT)) {
			processedSql = processedSql.substring(0, processedSql.length() - 1);
		}
		String regionNameList[] = regionName.split(SPLIT);
		String regionDefineList[] = regionDefine.split(SPLIT);
		for (int i = 0; i < regionNameList.length; i++) {
			// 得到当前地区定义的省市列表
			String quatoCitiesDefine = addQuatoToCitiesDefine(regionDefineList[i]);
			String insertBeforeGroupBy = processedSql.substring(0, processedSql
					.toLowerCase().indexOf(GROUP) - 1)
					+ SPACE
					+ " where "
					+ regionLocationColumnName
					+ " in ("
					+ quatoCitiesDefine
					+ ")"
					+ SPACE
					+ processedSql.substring(processedSql.toLowerCase()
							.indexOf(GROUP));
			result = result + " select '" + regionNameList[i] + "' as "
					+ regionLocationColumnName + ",t.* from ("
					+ insertBeforeGroupBy + ") t " + UNION;
		}
		if (result.endsWith(UNION)) {
			result = result.substring(1, result.length() - UNION.length());
		}
		return result;
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
		// TODO Auto-generated method stub
		String sql = "select product ,sum(price) quantity from oe_order_all group by product;";
		String regionName = "西北区;西南区";
		String regionDefine = "新疆,内蒙古;西藏,四川";
		System.err.println(RegionUtils.getQuerySql(sql, regionName,
				regionDefine, "location"));
	}

}
