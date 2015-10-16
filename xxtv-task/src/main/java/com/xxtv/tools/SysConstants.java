package com.xxtv.tools;

import java.util.Arrays;
import java.util.List;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

public class SysConstants
{

	private static final Prop			prop						= PropKit.use(DevConstants.SYS_CONFIG_FILENAME);

	/**
	 * controller不限制的url(不带方法名)
	 */
	public static final List<String>	CONTROLLER_UNLIMIT_URL_LIST	= Arrays.asList(prop.get("controllerUnLimitUrl", ";").split(";"));

	/**
	 * action不限制的url(带方法名)
	 */
	public static final List<String>	ACTION_UNLIMIT_URL_LIST		= Arrays.asList(prop.get("actionUnLimitUrl", ";").split(";"));

	/**
	 * 默认用户密码
	 */
	public static final String			DEFAULT_PASSWORD			= "111111";

	/**
	 * DEBUG模式
	 */
	public static final boolean			DEBUG						= prop.getBoolean("debug", false);

	/**
	 * 是否压缩HTML代码
	 */
	public static final boolean			HTML_IS_COMPRESS			= false;

	/**
	 * session user
	 */
	public static final String			SESSION_USER				= "sessionUser";

	/**
	 * 视图默认路径
	 */
	public static final String			VIEW_BASE_PATH				= "biz-logic";
}
