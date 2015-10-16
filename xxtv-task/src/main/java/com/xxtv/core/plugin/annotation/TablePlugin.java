package com.xxtv.core.plugin.annotation;

import java.util.List;

import com.jfinal.log.Logger;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Model;
import com.xxtv.core.kit.ClassSearcherKit;

/**
 * 扫描model上的注解，绑定model和table
 * 
 */
public class TablePlugin implements IPlugin
{

	protected final Logger		log	= Logger.getLogger(getClass());

	private ActiveRecordPlugin	arp;

	public TablePlugin(ActiveRecordPlugin arp)
	{
		this.arp = arp;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public boolean start()
	{
		List<Class<? extends Model>> modelClasses = ClassSearcherKit.of(Model.class).search();// 查询所有继承BaseModel的类
		// 循环处理自动注册映射
		for (Class model : modelClasses)
		{
			// 获取注解对象
			Table tableBind = (Table) model.getAnnotation(Table.class);
			if (tableBind == null)
			{
				log.error(model.getName() + "继承了BaseModel，但是没有注解绑定表名");
				continue;
			}

			// 获取映射表
			String tableName = tableBind.tableName().trim();
			String pkName = tableBind.pkName().trim();
			if (tableName.equals("") || pkName.equals(""))
			{
				log.error(model.getName() + "注解错误，表名或者主键名为空");
				continue;
			}

			// 映射注册
			arp.addMapping(tableName, pkName, model);
		}
		return true;
	}

	@Override
	public boolean stop()
	{
		return true;
	}

}
