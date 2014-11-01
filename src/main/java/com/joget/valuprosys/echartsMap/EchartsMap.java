package com.joget.valuprosys.echartsMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.userview.model.UserviewMenu;
import org.joget.plugin.base.PluginManager;

import com.joget.valuprosys.echartsMap.database.DaoUtils;
import com.joget.valuprosys.echartsMap.utils.MapDataFormat;
import com.joget.valuprosys.echartsMap.utils.OpenUrlUtils;
import com.joget.valuprosys.echartsMap.utils.RegionUtils;

public class EchartsMap extends UserviewMenu {

	private static final String OPEN_URL_PARAM_NAME = "openUrlParamName";
	private static final String OPEN_URL_PARAM_VALUE = "openUrlParamValue";
	private static final String OPEN_URL_PARAM_OF_MAP = "openUrlParamOfMap";
	private static final String OPEN_URL = "openUrl";
	private static final String OPEN_URL_FLAG = "openUrlFlag";
	private static final String AREA_TABLE_SHOW = "areaTableShow";
	private static final String SHOW_PROVINCE = "showProvince";
	private static final String SHOW_DATA_RANGE = "showDataRange";
	private static final String AREA_QUANTITY_COLUMN_NAME = "areaQuantityColumnName";
	private static final String AREA_SERIES_COLUMN_NAME = "areaSeriesColumnName";
	public static final String CITY = "city";
	public  static final String AREA_COLOR = "areaColor";
	private static final String SHOW_TOOL_BOX = "showToolBox";
	private static final String SHOW_LEGEND = "showLegend";
	private static final String FALSE = "false";
	private static final String AREA_SQL = "areaSql";
	private static final String AREA_CITY_COLUMN_NAME = "areaCityColumnName";
	private static final String AREA_LOCATIONS = "areaLocations";
	private static final String AREA_NAME = "areaName";
	private static final String AREA_SHOW = "areaShow";
	private static final String MARK_POINT_COLOR = "markPointColor";
	private static final String MARK_POINT_SHOW_OR_NOT = "markPointShowOrNot";
	private static final String TOOLTIP_SHOW_DETAIL = "tooltipShowDetail";
	private static final String FONT_COLOR = "fontColor";
	private static final String BACK_GROUND_COLOR = "backGroundColor";
	private static final String SERIES_COLUMN_NAME = "seriesColumnName";
	private static final String QUANTITY_COLUMN_NAME = "quantityColumnName";
	private static final String CITY_COLUMN_NAME = "cityColumnName";
	
	
	//private final String name = "EchartsMap";
	//private final String version = "1.0";

	
	
	private static final String SQL = "sql";
	private static final String LABEL = "Map Page";
	private static final String TEMPLATES_ECHARTS_MAP_FTL = "/templates/echartsMap.ftl";
	private static final String ICON = "/plugin/org.joget.apps.userview.lib.HtmlPage/images/grid_icon.gif";
	private static final List<String> PARAM = Arrays.asList("devMode","width", "height",BACK_GROUND_COLOR,FONT_COLOR, "mapLabel","mapSubLabel",
				SHOW_DATA_RANGE,SHOW_LEGEND,SHOW_TOOL_BOX,"theme","splitNumber","maxValueColor"
				,"minValueColor","dataRangeMin","dataRangeMax",SHOW_PROVINCE,TOOLTIP_SHOW_DETAIL,MARK_POINT_SHOW_OR_NOT,MARK_POINT_COLOR,
				"MultiShowSeries",SERIES_COLUMN_NAME,CITY_COLUMN_NAME,QUANTITY_COLUMN_NAME,SQL,
				AREA_SQL,AREA_CITY_COLUMN_NAME,AREA_SERIES_COLUMN_NAME,AREA_QUANTITY_COLUMN_NAME,AREA_SHOW,AREA_NAME,AREA_LOCATIONS,AREA_COLOR,AREA_TABLE_SHOW,
				OPEN_URL_FLAG,OPEN_URL,OPEN_URL_PARAM_OF_MAP,OPEN_URL_PARAM_VALUE,OPEN_URL_PARAM_VALUE);
	
	private static final String TEMPLATE_PATH = TEMPLATES_ECHARTS_MAP_FTL;
	private static final String THEME_PATH = "/templates/themes.ftl";
	private static final String DATA_RANGE_STYLE_PATH = "/templates/dataRangeStyle.ftl";
	private static final String MARK_POINT_PATH = "/templates/markPoint.ftl";
	
	private static final String VERSION = "1.0.0";
	private static final String ENTERPRISE = "Enterprise";
	private static final String DESCRIPTION = "echart map from baidu";
	
	public static final String CITY_ARRAY_NAME = "name";
	public static final String CITY_ARRAY_VALUE = "value";
	
	private PluginManager pluginManager;

	private PluginManager getPluginManager(){
		if (pluginManager==null){
			pluginManager = (PluginManager)AppUtil.getApplicationContext().getBean("pluginManager");
		}
		return pluginManager;
	}
	
	@Override
	public String getClassName() {
		return getClass().getName();
	}

	@Override
	public String getLabel() {
		return LABEL;
	}

	@Override
	public String getIcon() {
		return ICON;
	}

	@Override
	public String getRenderPage() {
		
		String baseURL = "";
		if (AppUtil.getDesignerWebBaseUrl().length() > 10 && "jwdesigner".equals(AppUtil.getDesignerWebBaseUrl().substring(AppUtil.getDesignerWebBaseUrl().length() - 10).toLowerCase()))
			baseURL = AppUtil.getDesignerWebBaseUrl().substring(0, AppUtil.getDesignerWebBaseUrl().length() - 11) + AppUtil.getRequestContextPath();
		else
			baseURL = AppUtil.getDesignerWebBaseUrl() + AppUtil.getRequestContextPath();
		String requestURL = baseURL+"/plugin/"+getClass().getName();
		
		
		
		Map<String, Object> param = setFreeMarkerTemplateParameter(requestURL);
		
		
		param.put("themeParts", generateDisplayConfig(param,THEME_PATH));
		param.put("dataRangeStyle", generateDisplayConfig(param,DATA_RANGE_STYLE_PATH));
		param.put("markPoint", generateDisplayConfig(param,MARK_POINT_PATH));
		
		//DaoUtils dao = new DaoUtils();
		
		//String htmlDataStr = DaoUtils.convertToStandardMapFormat(result, getPropertyString(SERIES_COLUMN_NAME),getPropertyString(CITY_COLUMN_NAME), getPropertyString(QUANTITY_COLUMN_NAME));
		//param.put("data",htmlDataStr);
		
		
		//存放data块内容
		String result ="";
		List<Map<String, String>> dataList;
		List<String> seriesNameList;
		if(getPropertyString(AREA_SHOW).equals(FALSE)){
			result = DaoUtils.execute(getPropertyString(SQL).trim());
			dataList = MapDataFormat.convertToStandardMapFormat(result, getPropertyString(SERIES_COLUMN_NAME).trim(),getPropertyString(CITY_COLUMN_NAME).trim(), getPropertyString(QUANTITY_COLUMN_NAME).trim());
			seriesNameList = MapDataFormat.getSeriesNamesWithQuato(getPropertyString(SERIES_COLUMN_NAME).trim(), result);
			param.put("dataVar", dataList);
		}else {
			//用大区数据替换地区数据进行显示
			
			//大区相关内容
			String areaDataSql= RegionUtils.getQuerySql(getPropertyString(AREA_SQL),getPropertyString(AREA_NAME),getPropertyString(AREA_LOCATIONS),getPropertyString(AREA_COLOR),getPropertyString(AREA_CITY_COLUMN_NAME));
			String areaData = RegionUtils.queryRegionData(areaDataSql);
			String allAreaData = areaData;
			areaData = RegionUtils.addCitesToJsonResult(areaData, getPropertyString(AREA_NAME),getPropertyString(AREA_LOCATIONS),getPropertyString(AREA_CITY_COLUMN_NAME));
			String areaColor = RegionUtils.getRegionColor(getPropertyString(AREA_LOCATIONS), getPropertyString(AREA_COLOR));
			result = areaData;
			//以CITY得到省市数据
			dataList = MapDataFormat.convertToStandardMapFormat(result, getPropertyString(AREA_SERIES_COLUMN_NAME).trim(),CITY, getPropertyString(AREA_QUANTITY_COLUMN_NAME).trim());
			//AREA_CITY_COLUMN_NAME-->areaCityColumnName地区列名得到大区数据
			List<Map<String, String>> areaTableList = MapDataFormat.convertToMapFormat(allAreaData, getPropertyString(AREA_SERIES_COLUMN_NAME).trim(),getPropertyString(AREA_CITY_COLUMN_NAME), getPropertyString(AREA_QUANTITY_COLUMN_NAME).trim());
			seriesNameList = MapDataFormat.getSeriesNamesWithQuato(getPropertyString(AREA_SERIES_COLUMN_NAME).trim(), result);
			
			String cityToRegion = RegionUtils.getCityToRegion(getPropertyString(AREA_NAME), getPropertyString(AREA_LOCATIONS));
			
			param.put("dataVar", dataList);
			param.put("areaVar", areaTableList);
			param.put("areaDataSql", areaDataSql);
			param.put("allAreaData",allAreaData);
			param.put("areaData", areaData);
			param.put("areaColor", areaColor);
			param.put("cityToRegion", cityToRegion);
		}
		//得到跳转url相关内容
		String url = getPropertyString(OPEN_URL);
		String locationParam = getPropertyString(OPEN_URL_PARAM_OF_MAP);
		String locationValue = "@LOCATION@";
		String paramNames = getPropertyString(OPEN_URL_PARAM_NAME);
		String paramValues = getPropertyString(OPEN_URL_PARAM_VALUE);
		String openUrl = OpenUrlUtils.getFullUrl(url, locationParam, locationValue, paramNames, paramValues);
		param.put("openUrl", openUrl);
		//存放序列名称数组		 
		param.put("allSeriesLabels", seriesNameList.toString());
		param.put("allSeriesLabelsList", seriesNameList);//用于legend序列单选和复选的配置
		//System.err.println((getPropertyString(SQL)));
		//System.err.println(DaoUtils.execute(getPropertyString(SQL)));
		//param.put("data",DaoUtils.execute("SELECT * FROM jwdb.app_app;"));
		//select city,quantity from jwdb.test where  series="1";
		//调试信息
		param.put("result", result);
		param.put("debug", dataList.toString());
		String content = getPluginManager().getPluginFreeMarkerTemplate(param, getClass().getName(),TEMPLATE_PATH, null);
		
    	return content;
	}

	private String generateDisplayConfig(Map<String, Object> param,String templateFile){
		String content = getPluginManager().getPluginFreeMarkerTemplate(param, getClass().getName(),templateFile, null);
		
    	return content;
	}
	
	/**
	 * 设置相关参数给模板
	 * @param requestURL
	 * @return
	 */
	private Map<String, Object> setFreeMarkerTemplateParameter(String requestURL) {
		Map<String,Object> model = new HashMap<String,Object>();
		model.put("requestUrl",requestURL);
		for(int i=0;i<PARAM.size();i++){
			model.put(PARAM.get(i),getPropertyString(PARAM.get(i)).trim());
		}
		return model;
	}

	public String getName() {
		return "Map Page Menu";
	}

	public String getVersion() {
		return VERSION;
	}

	public String getDescription() {
		return DESCRIPTION;
	}

	public String getPropertyOptions() {
		AppDefinition appDef = AppUtil.getCurrentAppDefinition();
		String appId = appDef.getId();
		String appVersion = appDef.getVersion().toString();
		Object[] arguments = new Object[] { appId, appVersion };
		String json = AppUtil.readPluginResource(getClass().getName(), "/properties/userview/MapMenu.json", arguments, true, "message/userview/formMenu");
		return json;
	}

	@Override
	public String getDecoratedMenu() {
		return null;
	}

	@Override
	public boolean isHomePageSupported() {
		return true;
	}

	@Override
	public String getCategory() {
		return ENTERPRISE;
	}

}
