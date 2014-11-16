package com.joget.valuprosys.echartsMap.utils;

public class OpenUrlUtils {
	public static String getFullUrl(String url, String locationParam,String seriesParam,
			String locationValue, String seriesValue,String paramNames, String paramValues) {
		String fullUrl = url;
		fullUrl = fullUrl + "?" + locationParam + "=" + encode(locationValue);
		fullUrl = fullUrl + "&" + seriesParam + "=" + encode(seriesValue);
		String[] params = paramNames.split(RegionUtils.SPLIT);
		String[] values = paramValues.split(RegionUtils.SPLIT);
		for (int i = 0; i < params.length; i++) {
			if(params[i]!=null&&params[i].length()>0){
				fullUrl = fullUrl + "&" + encode(params[i]) + "="
					+ encode(values[i]);
			}
		}
		return fullUrl;
	}

	/*
	 * When encoding a String, the following rules apply:
	 * 
	 * The alphanumeric characters "a" through "z", "A" through "Z" and "0"
	 * through "9" remain the same. The special characters ".", "-", "*", and
	 * "_" remain the same. The space character " " is converted into a plus
	 * sign "+". All other characters are unsafe and are first converted into
	 * one or more bytes using some encoding scheme. Then each byte is
	 * represented by the 3-character string "%xy", where xy is the two-digit
	 * hexadecimal representation of the byte. The recommended encoding scheme
	 * to use is UTF-8. However, for compatibility reasons, if an encoding is
	 * not specified, then the default encoding of the platform is used.
	 */
	private static String encode(String str) {
		// return java.net.URLEncoder.encode(str).replace("+", "%20");
		return java.net.URLEncoder.encode(str);
	};

	public static void main(String args[]) {
		String url = "http://map.baidu.com";
		String locationParam = "location";
		String locationValue = "上 海";
		String paramNames = "a;b";
		String paramValues = "a1;测 试";
//		String openUrl = OpenUrlUtils.getFullUrl(url, locationParam,
//				locationValue, paramNames, paramValues);
//
//		System.err.println(openUrl);
//		System.err.println(java.net.URLEncoder.encode("e a"));
	}
}
