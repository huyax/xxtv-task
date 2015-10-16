package com.xxtv.tools;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

/**
 * 开发者模式判断
 * 
 * @author zyz
 *
 */
public class DevConstants
{

	private static final Prop	prop						= PropKit.use("run.properties");

	public static final String	RUN_MODE					= prop.get("runMode", "release");

	public static final String	DB_CONFIG_FILENAME			= RUN_MODE + "/" + prop.get("dbConfigFileName");

	public static final String	SYS_CONFIG_FILENAME			= RUN_MODE + "/" + prop.get("sysConfigFileName");

	/**
	 * 是否使用redis作数据库缓存
	 */
	public static final boolean	DB_CONFIG_USE_REDIS_CACHE	= prop.getBoolean("dbConfigUseRedisCache", true);
}
