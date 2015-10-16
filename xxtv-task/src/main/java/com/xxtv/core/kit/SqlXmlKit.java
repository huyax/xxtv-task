package com.xxtv.core.kit;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.beetl.core.BeetlKit;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.jfinal.log.Logger;
import com.xxtv.tools.DateTools;

/**
 * 处理Sql Map
 * 
 * 说明：加载sql map中的sql到map中，并提供动态长度sql处理
 */
public class SqlXmlKit
{

	protected static final Logger				log				= Logger.getLogger(SqlXmlKit.class);

	/**
	 * xml中所有的sql语句
	 */
	private static final Map<String, String>	sqlMap			= new HashMap<String, String>();

	/**
	 * 过滤掉的sql关键字
	 */
	private static final List<String>			badKeyWordList	= new ArrayList<String>();

	/**
	 * 加载关键字到List
	 */
	static
	{
		String badStr = "'|and|exec|execute|insert|select|delete|update|count|drop|*|%|chr|mid|master|truncate|"
				+ "char|declare|sitename|net user|xp_cmdshell|;|or|-|+|,|like'|and|exec|execute|insert|create|drop|"
				+ "table|from|grant|use|group_concat|column_name|"
				+ "information_schema.columns|table_schema|union|where|select|delete|update|order|by|count|*|"
				+ "chr|mid|master|truncate|char|declare|or|;|-|--|+|,|like|//|/|%|#";
		badKeyWordList.addAll(Arrays.asList(badStr.split("\\|")));
	}

	/**
	 * sql查询关键字过滤效验
	 * 
	 * @param queryStr
	 * @return
	 */
	public static boolean keywordVali(String queryStr)
	{
		queryStr = queryStr.toLowerCase();//统一转为小写
		for (String badKeyWord : badKeyWordList)
		{
			if (queryStr.indexOf(badKeyWord) >= 0)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取SQL，固定SQL
	 * 
	 * @param sqlId
	 * @return
	 */
	public static String getSql(String sqlId)
	{
		String sql = sqlMap.get(sqlId);
		if (null == sql || sql.isEmpty())
		{
			log.error("sql语句不存在：sql id是" + sqlId);
		}

		return sql.replaceAll("[\\s]{2,}", " ");
	}

	/**
	 * 获取SQL，动态SQL
	 * 
	 * @param sqlId
	 * @param param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getSql(String sqlId, Map<String, Object> param)
	{
		String sqlTemplete = sqlMap.get(sqlId);
		if (null == sqlTemplete || sqlTemplete.isEmpty())
		{
			log.error("sql语句不存在：sql id是" + sqlId);
		}

		Map<String, Object> paramMap = new HashMap<String, Object>();
		Set<String> paramKeySet = param.keySet();
		for (String paramKey : paramKeySet)
		{
			paramMap.put(paramKey, param.get(paramKey));
		}
		String sql = BeetlKit.render(sqlTemplete, paramMap);

		Pattern pattern = Pattern.compile("#[\\w\\d\\$\\'\\%\\_]+#"); //#[\\w\\d]+#    \\$
		Pattern pattern2 = Pattern.compile("\\$[\\w\\d\\_]+\\$");

		Matcher matcher = pattern.matcher(sql);

		while (matcher.find())
		{
			String clounm = matcher.group(0); // 得到的结果形式：#'%$names$%'#

			Matcher matcher2 = pattern2.matcher(clounm);
			matcher2.find();
			String clounm2 = matcher2.group(0); // 得到的结果形式：$names$
			String clounm3 = clounm2.replace("$", "");

			if (clounm.equals("#" + clounm2 + "#"))
			{ // 数值型，可以对应处理int、long、bigdecimal、double等等
				Object data = param.get(clounm3);
				if (data != null)
				{
					if (data instanceof Object[])
					{//数组
						Object[] arrData = (Object[]) data;
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < arrData.length; i++)
						{
							sb.append(",?");
						}
						sql = sql.replace(clounm, sb.substring(1));
					}
					else if (data instanceof Map)
					{//Map
						Map<String, ?> mapData = (Map<String, ?>) data;
						Set<String> keySet = mapData.keySet();
						Iterator<String> keyIterator = keySet.iterator();
						StringBuilder sb = new StringBuilder();
						while (keyIterator.hasNext())
						{
							String key = keyIterator.next();
							sb.append("," + key + "=?");
						}
						sql = sql.replace(clounm, sb.substring(1));
					}
					else if (data instanceof Iterator)
					{//list iterator等
						Iterator<?> iterator = (Iterator<?>) data;
						StringBuilder sb = new StringBuilder();
						while (iterator.hasNext())
						{
							sb.append(",?");
						}
						sql = sql.replace(clounm, sb.substring(1));
					}
					else
					{//普通对象
						sql = sql.replace(clounm, "?");
					}
				}
				else
				{
					sql = sql.replace(clounm, "NULL");
				}
			}
			else
			{ // 字符串，主要是字符串模糊查询、日期比较的查询
				Object data = param.get(clounm3);
				if (data != null)
				{
					if (data instanceof Object[])
					{//数组
						Object[] arrData = (Object[]) data;
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < arrData.length; i++)
						{
							sb.append(",?");
							Object dt = arrData[i];
							dt = dt instanceof Date ? DateTools.formatDateTime((Date) dt) : dt;
						}
						sql = sql.replace(clounm, sb.substring(1));
					}
					else if (data instanceof Map)
					{//Map
						Map<String, ?> mapData = (Map<String, ?>) data;
						Set<String> keySet = mapData.keySet();
						Iterator<String> keyIterator = keySet.iterator();
						StringBuilder sb = new StringBuilder();
						while (keyIterator.hasNext())
						{
							String key = keyIterator.next();
							sb.append("," + key + "=?");
							Object dt = mapData.get(key);
							dt = dt instanceof Date ? DateTools.formatDateTime((Date) dt) : dt;
						}
						sql = sql.replace(clounm, sb.substring(1));
					}
					else if (data instanceof Iterator)
					{//list iterator等
						Iterator<?> iterator = (Iterator<?>) data;
						StringBuilder sb = new StringBuilder();
						while (iterator.hasNext())
						{
							Object dataItm = iterator.next();
							sb.append(",?");
							dataItm = dataItm instanceof Date ? DateTools.formatDateTime((Date) dataItm) : dataItm;
						}
						sql = sql.replace(clounm, sb.substring(1));
					}
					else
					{//普通对象
						data = data instanceof Date ? DateTools.formatDateTime((Date) data) : data;
						sql = sql.replace(clounm, "?");
					}
				}
				else
				{
					sql = sql.replace(clounm, "NULL");
				}
			}
		}

		return sql.replaceAll("[\\s]{2,}", " ");
	}

	/**
	 * 获取SQL，动态SQL
	 * 
	 * @param sqlId
	 * @param param 查询参数
	 * @param list 用于接收预处理的值
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getSql(String sqlId, Map<String, Object> param, LinkedList<Object> list)
	{
		String sqlTemplete = sqlMap.get(sqlId);
		if (null == sqlTemplete || sqlTemplete.isEmpty())
		{
			log.error("sql语句不存在：sql id是" + sqlId);
		}

		Map<String, Object> paramMap = new HashMap<String, Object>();
		Set<String> paramKeySet = param.keySet();
		for (String paramKey : paramKeySet)
		{
			paramMap.put(paramKey, param.get(paramKey));
		}
		String sql = BeetlKit.render(sqlTemplete, paramMap);

		Pattern pattern = Pattern.compile("#[\\w\\d\\$\\'\\%\\_]+#"); //#[\\w\\d]+#    \\$
		Pattern pattern2 = Pattern.compile("\\$[\\w\\d\\_]+\\$");

		Matcher matcher = pattern.matcher(sql);

		while (matcher.find())
		{
			String clounm = matcher.group(0); // 得到的结果形式：#'%$names$%'#

			Matcher matcher2 = pattern2.matcher(clounm);
			matcher2.find();
			String clounm2 = matcher2.group(0); // 得到的结果形式：$names$
			String clounm3 = clounm2.replace("$", "");

			if (clounm.equals("#" + clounm2 + "#"))
			{ // 数值型，可以对应处理int、long、bigdecimal、double等等
				Object data = param.get(clounm3);
				if (data != null)
				{
					if (data instanceof Object[])
					{//数组
						Object[] arrData = (Object[]) data;
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < arrData.length; i++)
						{
							sb.append(",?");
							Object dt = arrData[i];
							list.add(dt instanceof Date ? DateTools.formatDateTime((Date) dt) : dt);
						}
						sql = sql.replace(clounm, sb.substring(1));
					}
					else if (data instanceof Map)
					{//Map
						Map<String, ?> mapData = (Map<String, ?>) data;
						Set<String> keySet = mapData.keySet();
						Iterator<String> keyIterator = keySet.iterator();
						StringBuilder sb = new StringBuilder();
						while (keyIterator.hasNext())
						{
							String key = keyIterator.next();
							sb.append("," + key + "=?");
							Object dt = mapData.get(key);
							list.add(dt instanceof Date ? DateTools.formatDateTime((Date) dt) : dt);
						}
						sql = sql.replace(clounm, sb.substring(1));
					}
					else if (data instanceof Iterator)
					{//list iterator等
						Iterator<?> iterator = (Iterator<?>) data;
						StringBuilder sb = new StringBuilder();
						while (iterator.hasNext())
						{
							Object dataItm = iterator.next();
							sb.append(",?");
							list.add(dataItm instanceof Date ? DateTools.formatDateTime((Date) dataItm) : dataItm);
						}
						sql = sql.replace(clounm, sb.substring(1));
					}
					else
					{//普通对象
						sql = sql.replace(clounm, "?");
						list.add(data instanceof Date ? DateTools.formatDateTime((Date) data) : data);
					}
				}
				else
				{
					sql = sql.replace(clounm, "NULL");
				}
			}
			else
			{ // 字符串，主要是字符串模糊查询、日期比较的查询
				Object data = param.get(clounm3);
				if (data != null)
				{
					if (data instanceof Object[])
					{//数组
						Object[] arrData = (Object[]) data;
						StringBuilder sb = new StringBuilder();
						String clounm4 = clounm.replace("#", "").replace("'", "");
						for (int i = 0; i < arrData.length; i++)
						{
							sb.append(",?");
							Object dt = arrData[i];
							dt = dt instanceof Date ? DateTools.formatDateTime((Date) dt) : dt;
							list.add(clounm4.replace(clounm2, dt.toString()));
						}
						sql = sql.replace(clounm, sb.substring(1));
					}
					else if (data instanceof Map)
					{//Map
						Map<String, ?> mapData = (Map<String, ?>) data;
						Set<String> keySet = mapData.keySet();
						Iterator<String> keyIterator = keySet.iterator();
						StringBuilder sb = new StringBuilder();
						String clounm4 = clounm.replace("#", "").replace("'", "");
						while (keyIterator.hasNext())
						{
							String key = keyIterator.next();
							sb.append("," + key + "=?");
							Object dt = mapData.get(key);
							dt = dt instanceof Date ? DateTools.formatDateTime((Date) dt) : dt;
							list.add(clounm4.replace(clounm2, dt.toString()));
						}
						sql = sql.replace(clounm, sb.substring(1));
					}
					else if (data instanceof Iterator)
					{//list iterator等
						Iterator<?> iterator = (Iterator<?>) data;
						StringBuilder sb = new StringBuilder();
						String clounm4 = clounm.replace("#", "").replace("'", "");
						while (iterator.hasNext())
						{
							Object dataItm = iterator.next();
							sb.append(",?");
							dataItm = dataItm instanceof Date ? DateTools.formatDateTime((Date) dataItm) : dataItm;
							list.add(clounm4.replace(clounm2, dataItm.toString()));
						}
						sql = sql.replace(clounm, sb.substring(1));
					}
					else
					{//普通对象
						data = data instanceof Date ? DateTools.formatDateTime((Date) data) : data;
						String clounm4 = clounm.replace("#", "").replace("'", "").replace(clounm2, data.toString());
						list.add(clounm4);
						sql = sql.replace(clounm, "?");
					}
				}
				else
				{
					sql = sql.replace(clounm, "NULL");
				}
			}
		}

		return sql.replaceAll("[\\s]{2,}", " ");
	}

	/**
	 * 清楚加载的sql
	 */
	public static void destory()
	{
		sqlMap.clear();
	}

	/**
	 * 初始化加载sql语句到map
	 */
	@SuppressWarnings("rawtypes")
	public static void init()
	{
		File file = new File(SqlXmlKit.class.getClassLoader().getResource("").getFile());
		List<File> files = new ArrayList<File>();
		findFiles(file, files);

		SAXReader reader = new SAXReader();
		String fileName = null;
		try
		{
			for (File xmlfile : files)
			{
				fileName = xmlfile.getName();
				Document doc = reader.read(xmlfile);
				Element root = doc.getRootElement();
				String namespace = root.attributeValue("namespace");
				if (null == namespace || namespace.trim().isEmpty())
				{
					log.error("sql xml文件" + fileName + "的命名空间不能为空");
					continue;
				}

				for (Iterator iterTemp = root.elementIterator(); iterTemp.hasNext();)
				{
					Element element = (Element) iterTemp.next();
					if (element.getName().toLowerCase().equals("sql"))
					{
						String id = element.attributeValue("id");
						if (null == id || id.trim().isEmpty())
						{
							log.error("sql xml文件" + fileName + "的存在没有id的sql语句");
							continue;
						}

						String sql = element.getText();
						if (null == sql || sql.trim().isEmpty())
						{
							log.error("sql xml文件" + fileName + "的存在没有内容的sql语句");
							continue;
						}

						String key = namespace + "." + id;
						if (sqlMap.containsKey(key))
						{
							log.error("sql xml文件" + fileName + "的sql语句" + key + "的存在重复命名空间和ID");
							continue;
						}

						sql = sql.replaceAll("[\\s]{2,}", " ");
						sqlMap.put(key, sql);
						log.debug("sql文件名：" + fileName + " sql key: " + key + " sql内容：" + sql);
					}
				}
			}
		}
		catch (DocumentException e)
		{
			log.error("sql xml文件" + fileName + "解析异常");
			e.printStackTrace();
		}
	}

	/**
	 * 递归查找文件
	 * 
	 * @param baseFile
	 * @param sqlXmlFiles
	 * @return
	 */
	private static List<File> findFiles(File baseFile, List<File> sqlXmlFiles)
	{
		if (!baseFile.isDirectory())
		{
			if (baseFile.getName().endsWith(".sql.xml"))
			{
				sqlXmlFiles.add(baseFile);
			}
		}
		else
		{
			File[] fileList = baseFile.listFiles();
			for (File file : fileList)
			{
				if (file.isDirectory())
				{
					findFiles(file, sqlXmlFiles);

				}
				else
				{
					if (file.getName().endsWith(".sql.xml"))
					{
						sqlXmlFiles.add(file);
					}
				}
			}
		}
		return sqlXmlFiles;
	}

}
