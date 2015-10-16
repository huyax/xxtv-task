package com.xxtv.web.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Model;
import com.xxtv.core.plugin.annotation.Table;

@SuppressWarnings("serial")
@Table(tableName = "catelogs")
public class CatelogModel extends Model<CatelogModel>
{
	private static final Logger			LOGGER	= LoggerFactory.getLogger(CatelogModel.class);

	public static final CatelogModel	dao		= new CatelogModel();
}
