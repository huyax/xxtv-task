package com.xxtv.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 并发时间工具类
 * 
 * @author zyz
 *
 */
public class DateTools
{

	public static ThreadLocal<DateFormat>	yyyy_MM_dd			= new ThreadLocal<DateFormat>()
																{
																	@Override
																	protected DateFormat initialValue()
																	{
																		return new SimpleDateFormat("yyyy-MM-dd");
																	}
																};

	public static ThreadLocal<DateFormat>	yyyy_MM_dd_HH		= new ThreadLocal<DateFormat>()
																{
																	@Override
																	protected DateFormat initialValue()
																	{
																		return new SimpleDateFormat("yyyy-MM-dd HH");
																	}
																};

	public static ThreadLocal<DateFormat>	yyyy_MM_dd_HH_mm_ss	= new ThreadLocal<DateFormat>()
																{
																	@Override
																	protected DateFormat initialValue()
																	{
																		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
																	}
																};

	public static ThreadLocal<DateFormat>	yyyyMMdd			= new ThreadLocal<DateFormat>()
																{
																	@Override
																	protected DateFormat initialValue()
																	{
																		return new SimpleDateFormat("yyyyMMdd");
																	}
																};

	public static ThreadLocal<DateFormat>	yyyyMMddHH			= new ThreadLocal<DateFormat>()
																{
																	@Override
																	protected DateFormat initialValue()
																	{
																		return new SimpleDateFormat("yyyyMMddHH");
																	}
																};

	public static ThreadLocal<DateFormat>	yyyyMMddHHmmss		= new ThreadLocal<DateFormat>()
																{
																	@Override
																	protected DateFormat initialValue()
																	{
																		return new SimpleDateFormat("yyyyMMddHHmmss");
																	}
																};

	public static ThreadLocal<DateFormat>	HH					= new ThreadLocal<DateFormat>()
																{
																	@Override
																	protected DateFormat initialValue()
																	{
																		return new SimpleDateFormat("HH");
																	}
																};

	/**
	 * 通过搜索时间 获取小时数 天数等
	 * 
	 * @param searchTime
	 * @param dateFormat
	 * @param calendarType
	 * @return
	 * @throws ParseException
	 */
	public static int getTimeBySearchTime(String searchTime, ThreadLocal<DateFormat> dateFormat, int calendarType) throws ParseException
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(parseDate(searchTime, dateFormat));
		return calendar.get(calendarType);
	}

	/**
	 * 通过搜索时间获取天
	 * 
	 * @param searchTime
	 * @param dateFormat
	 * @return
	 * @throws ParseException
	 */
	public static Date getDateBySearchTime(String searchTime, ThreadLocal<DateFormat> dateFormat) throws ParseException
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(parseDate(searchTime, dateFormat));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	/**
	   * 取得1天前的日期
	   */
	public static String get1DayBeforDate()
	{

		Calendar now = Calendar.getInstance();
		now.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH) - 1);

		return getDate(now.getTime(), "yyyy-MM-dd");
	}

	/**
	 * 取得1天前的日期
	 */
	public static String get1DayBeforDate(String format)
	{

		Calendar now = Calendar.getInstance();
		now.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH) - 1);

		return getDate(now.getTime(), format);
	}

	/**
	 * 字符串转换为日期格式
	 * 
	 * @param sDate
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(String sDate) throws ParseException
	{
		return yyyy_MM_dd.get().parse(sDate);
	}

	/**
	 * 字符串转换为日期格式
	 * 
	 * @param sDate
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDateTime(String sDate) throws ParseException
	{
		return yyyy_MM_dd_HH_mm_ss.get().parse(sDate);
	}

	/**
	 * 根据模版输出date
	 * 
	 * @Author zyz 2014年8月21日 下午3:56:43
	 * @param sDate
	 * @param dateFormat
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(String sDate, ThreadLocal<DateFormat> dateFormat) throws ParseException
	{
		return dateFormat.get().parse(sDate);
	}

	/**
	 * 格式化日期输出字符串
	 * 
	 * @param date
	 * @return yyyy-MM-dd
	 * @throws ParseException
	 */
	public static String formatDate(Date date)
	{
		return yyyy_MM_dd.get().format(date);
	}

	/**
	 * 格式化日期时间输出字符串
	 * 
	 * @param date
	 * @return yyyy-MM-dd HH:mm:ss
	 * @throws ParseException
	 */
	public static String formatDateTime(Date date)
	{
		return yyyy_MM_dd_HH_mm_ss.get().format(date);
	}

	public static ThreadLocal<DateFormat>	yyyyMMddHHmmssSSS	= new ThreadLocal<DateFormat>()
																{
																	@Override
																	protected DateFormat initialValue()
																	{
																		return new SimpleDateFormat("yyyyMMddHHmmssSSS");
																	}
																};

	/**
	 * 根据模版输出时间字符串
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static String format(Date date, ThreadLocal<DateFormat> dateFormat)
	{
		return dateFormat.get().format(date);
	}

	public static String getDate(Date date, String format)
	{

		String dtstr = "";
		if (date == null)
		{
			return dtstr;
		}

		if (format == null || "".equals(format.trim()))
		{
			format = "yyyy-MM-dd";
		}

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		dtstr = sdf.format(date);
		return (dtstr == null ? "" : dtstr);

	}

	public static String getDate(Date date)
	{
		return getDate(date, "yyyy-MM-dd");
	}

	/**
	 * 取得当前日期对象
	 * 
	 * @return 返回java.util.Date日期对象
	 */
	public static Date getNow()
	{
		return Calendar.getInstance().getTime();
	}

	/**
	 * 获取当前时间前hourNum个小时时间字符串
	 * 
	 * @param dateFormat
	 * @param hourNum
	 * @return
	 */
	public static String getHourBeforeTimeStr(ThreadLocal<DateFormat> dateFormat, int hourNum)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - hourNum);
		return dateFormat.get().format(calendar.getTime());
	}

	/**
	 * 获取当前时间前hourNum个小时时间
	 * 
	 * @param hourNum
	 * @return
	 */
	public static Date getHourBeforeTime(int hourNum)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - hourNum);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 获取当前时间前dayNum天时间字符串
	 * 
	 * @param dateFormat
	 * @param dayNum
	 * @return
	 */
	public static String get1DayBeforeTimeStr(ThreadLocal<DateFormat> dateFormat, int dayNum)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - dayNum);
		return dateFormat.get().format(calendar.getTime());
	}

	/**
	 * 获取当前时间日历对象
	 * 
	 * @return 返回java.util.Calendar日期对象
	 */
	public static Calendar getNowCalendar()
	{
		return Calendar.getInstance();
	}

	/**
	 * 计算到下一天hour的秒数（单位:秒）
	 * 
	 * @return interval int （单位:秒）
	 */
	public static int getNextDayHourSecond(int hour)
	{
		int interval = 0;
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.AM_PM, Calendar.AM);
		calendar.set(Calendar.HOUR, hour);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		// 一天秒数远小于Integer.MAX_VALUE, 不考虑溢出, 强转
		interval = (int) ((calendar.getTimeInMillis() - System.currentTimeMillis()) / (1000));
		if (interval < 0)
		{
			interval += (60 * 60 * 24);
		}
		return interval;
	}

	/**
	 * 计算到下一个整点的秒数
	 * 
	 * @return
	 */
	public static int getNextHourSecond()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		int interval = (int) ((calendar.getTimeInMillis() - System.currentTimeMillis()) / (1000));
		return interval;
	}

	/**  
	 * zyj添加
	 * 计算两个日期之间相差的天数  
	 * @param smdate 较小的时间 
	 * @param bdate  较大的时间 
	 * @return 相差天数 
	 * @throws ParseException  
	 */
	public static int daysBetween(Date smdate, Date bdate) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		smdate = sdf.parse(sdf.format(smdate));
		bdate = sdf.parse(sdf.format(bdate));
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * zyj添加
	 * 将时间转成long型
	 * @param date
	 * @return long
	 * @throws ParseException
	 */
	public static long timeToNumber(String date, String formart) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat(formart);
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(date));
		return cal.getTimeInMillis();
	}

	/**
	 * zyj添加
	 * 判断一个时间段是否在另外的时间段内
	 * HH:mm
	 * @param beginDate
	 * @param endDate
	 * @param beginDateList
	 * @param endDateList
	 * @return boolean
	 * @throws ParseException
	 */
	public static boolean insertValidateHHmm(String beginDate, String endDate, List<String> beginDateList, List<String> endDateList)
			throws ParseException
	{
		Integer begin = trimChar(beginDate);
		Integer end = trimChar(endDate);
		List<Integer> beginList = new ArrayList<Integer>();
		List<Integer> endList = new ArrayList<Integer>();
		for (int i = 0; i < beginDateList.size(); i++)
		{
			beginList.add(trimChar(beginDateList.get(i)));
			endList.add(trimChar(endDateList.get(i)));//一个开始时间对应一个结束时间
		}
		for (int i = 0; i < endList.size(); i++)
		{
			//时间有重叠有三种情况:
			//1.插入时间的开始时间小于已经存在的一段时间的开始时间，但结束时间位于开始时间和结束时间之间(重叠)
			//2.插入时间的开始时间小于已经存在的一段时间的开始时间，但结束时间大于结束时间(全包含)
			//3.插入时间的开始时间大于一段已经存在的时间的开始时间，但是却小于这段时间的结束时间,且结束时间大于这段时间的结束时间
			//画一个时间图，可以总结出：他们都是开始时间小于已经存在的一段时间的结束时间，但结束时间大于这段时间的开始时间
			if (begin < endList.get(i) && end > beginList.get(i))
			{
				return false; //存在重叠
			}
		}

		return true;
	}

	/**
	 * zyj添加
	 * 判断一个时间段是否在另外的时间段内
	 * yyyy-MM-dd HH:mm
	 * @param beginDate
	 * @param endDate
	 * @param beginDateList
	 * @param endDateList
	 * @return boolean
	 * @throws ParseException
	 */
	public static boolean insertValidate(String beginDate, String endDate, List<String> beginDateList, List<String> endDateList, String formart)
			throws ParseException
	{
		Long begin = timeToNumber(beginDate, formart);
		Long end = timeToNumber(endDate, formart);
		List<Long> beginList = new ArrayList<Long>();
		List<Long> endList = new ArrayList<Long>();
		for (int i = 0; i < beginDateList.size(); i++)
		{
			beginList.add(timeToNumber(beginDateList.get(i), formart));
			endList.add(timeToNumber(endDateList.get(i), formart));//一个开始时间对应一个结束时间
		}
		for (int i = 0; i < endList.size(); i++)
		{
			//时间有重叠有三种情况:
			//1.插入时间的开始时间小于已经存在的一段时间的开始时间，但结束时间位于开始时间和结束时间之间(重叠)
			//2.插入时间的开始时间小于已经存在的一段时间的开始时间，但结束时间大于结束时间(全包含)
			//3.插入时间的开始时间大于一段已经存在的时间的开始时间，但是却小于这段时间的结束时间,且结束时间大于这段时间的结束时间
			//画一个时间图，可以总结出：他们都是开始时间小于已经存在的一段时间的结束时间，但结束时间大于这段时间的开始时间
			if (begin < endList.get(i) && end > beginList.get(i))
			{
				return false; //存在重叠
			}
		}

		return true;
	}

	/**
	 * zyj添加
	 * 时间转换成整型
	 * @param str
	 * @return Integer
	 */
	public static Integer trimChar(String str)
	{
		int index = str.indexOf(":");
		String newString = str.substring(0, index) + str.substring(index + 1, str.length());
		return Integer.parseInt(newString);
	}

	/**
	 * 是否为本月第一天
	 */
	public static boolean isFirstDayOfMonth()
	{
		Calendar c = Calendar.getInstance();
		// 得到本月的那一天
		int today = c.get(Calendar.DAY_OF_MONTH);
		// 然后判断是不是本月的第一天
		if (today == 1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
