package com.joget.valuprosys.echartsMap.database;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.joget.valuprosys.echartsMap.utils.MapDataFormat;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class DaoUtils {

	

	public static String execute(String query) {
		String result = null;
		try {
			// String query = (String) properties.get("query");
			DataSource ds = null;

			// use current datasource
			ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

			result = doQuery(ds, query);

			return result;
		} catch (Exception e) {
			LogUtil.error(DaoUtils.class.getName(), e, "Error executing plugin");
			return null;
		}
	}

	private static String doQuery(DataSource ds, String sql) throws SQLException, JSONException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs;
		try {
			con = ds.getConnection();
			stmt = con.prepareStatement(sql);
			rs = stmt.executeQuery();
			return resultSetToJson(rs);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (con != null) {
				con.close();
			}
		}
	}

	private static String resultSetToJson(ResultSet rs) throws SQLException, JSONException {
		// json数组
		JSONArray array = new JSONArray();

		// 获取列数
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();

		// 遍历ResultSet中的每条数据
		while (rs.next()) {
			JSONObject jsonObj = new JSONObject();

			// 遍历每一列
			for (int i = 1; i <= columnCount; i++) {
				String columnName = metaData.getColumnLabel(i);
				String value = rs.getString(columnName);
				jsonObj.put(columnName, value);
			}
			array.put(jsonObj);
		}

		return array.toString();
	}


	public static void test() throws IOException, TemplateException, JSONException {
		String seriesColumnName = "product";
		String data = "[{\"product\":\"冰箱\",\"price\":\"18771\",\"location\":\"内蒙古\"},{\"product\":\"冰箱\",\"price\":\"18852\",\"location\":\"四川\"},{\"product\":\"冰箱\",\"price\":\"19356\",\"location\":\"安徽\"},{\"product\":\"冰箱\",\"price\":\"17622\",\"location\":\"新疆\"},{\"product\":\"冰箱\",\"price\":\"14984\",\"location\":\"西藏\"},{\"product\":\"洗衣机\",\"price\":\"17775\",\"location\":\"内蒙古\"},{\"product\":\"洗衣机\",\"price\":\"18641\",\"location\":\"四川\"},{\"product\":\"洗衣机\",\"price\":\"18606\",\"location\":\"安徽\"},{\"product\":\"洗衣机\",\"price\":\"19109\",\"location\":\"新疆\"},{\"product\":\"洗衣机\",\"price\":\"18963\",\"location\":\"西藏\"},{\"product\":\"空调\",\"price\":\"14455\",\"location\":\"内蒙古\"},{\"product\":\"空调\",\"price\":\"17760\",\"location\":\"四川\"},{\"product\":\"空调\",\"price\":\"19346\",\"location\":\"安徽\"},{\"product\":\"空调\",\"price\":\"18940\",\"location\":\"新疆\"},{\"product\":\"空调\",\"price\":\"16887\",\"location\":\"西藏\"}]";
		List<Map<String, String>> list = MapDataFormat.convertToStandardMapFormat(data, seriesColumnName, "location", "price");
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
		cfg.setDirectoryForTemplateLoading(new File("D:\\workspace\\freemarker\\src\\main\\resource\\"));
		// 指定模板如何检索数据模型，这是一个高级的主题了…
		// 但先可以这么来用：
		cfg.setObjectWrapper(new DefaultObjectWrapper());

		// 创建根哈希表
		Map<String, Object> root = new HashMap<String, Object>();

		Template temp = cfg.getTemplate("template.ftl");

		Writer out = new OutputStreamWriter(System.out);

		root.put("dataVar", list);
		root.put("allSeriesLabels", MapDataFormat.getSeriesNamesWithQuato(seriesColumnName, data).toString());

		temp.process(root, out);
		out.flush();
	}

	public static void main(String[] agrs) {
		try {
			try {
				DaoUtils.test();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// [{"quantity":"1000","city":"北京"},{"quantity":"1010","city":"天津"}]
}
