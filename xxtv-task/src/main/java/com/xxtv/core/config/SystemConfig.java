package com.xxtv.core.config;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.ViewType;
import com.xxtv.core.plugin.annotation.TablePlugin;
import com.xxtv.core.plugin.mongodb.MongodbPlugin;
import com.xxtv.core.plugin.quartz.QuartzPlugin;
import com.xxtv.core.plugin.sqlxml.SqlXmlPlugin;
import com.xxtv.tools.DevConstants;
import com.xxtv.tools.SysConstants;

/**
 * API引导式配置
 */
public class SystemConfig extends JFinalConfig
{

	/**
	 * 配置常量
	 */
	@Override
	public void configConstant(Constants me)
	{
		// 加载少量必要配置，随后可用getProperty(...)获取值
		loadPropertyFile(DevConstants.DB_CONFIG_FILENAME);
		me.setViewType(ViewType.JSP);
		me.setDevMode(SysConstants.DEBUG);
	}

	/**
	 * 配置路由
	 */
	@Override
	public void configRoute(Routes me)
	{

	}

	/**
	 * 配置插件
	 */
	@Override
	public void configPlugin(Plugins me)
	{
		// 配置定时器
		me.add(new QuartzPlugin());

		// 加载sqlxml
		me.add(new SqlXmlPlugin());

		// 配置缓存
		me.add(new EhCachePlugin());
		
		//mongodb
		me.add(new MongodbPlugin(getProperty("mongodbUrl"),Integer.parseInt(getProperty("mongodbPort")),getProperty("mongodbName")));
		// 配置druid数据库连接池插件
//		DruidPlugin druidPlugin = new DruidPlugin(getProperty("jdbcUrl"), getProperty("user"), getProperty("password"));
//		druidPlugin.setInitialSize(getPropertyToInt("initialSize", 10));
//		druidPlugin.setMinIdle(getPropertyToInt("minIdle", 10));
//		druidPlugin.setMaxActive(getPropertyToInt("maxActive", 100));
//		me.add(druidPlugin);
//
//		// 配置ActiveRecord插件
//		ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
//		arp.setShowSql(SysConstants.DEBUG);
//		me.add(arp);
//		// 自动扫描bean和数据库表映射
//		new TablePlugin(arp).start();

	}

	/**
	 * 配置全局拦截器
	 */
	@Override
	public void configInterceptor(Interceptors me)
	{

	}

	/**
	 * 配置处理器
	 */
	@Override
	public void configHandler(Handlers me)
	{

	}

}
