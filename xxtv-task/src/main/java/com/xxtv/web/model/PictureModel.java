package com.xxtv.web.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Model;
import com.xxtv.core.plugin.annotation.Table;

/**
 * @author huwei
 * @date 2015年10月30日
 *
 */
@SuppressWarnings("serial")
@Table(tableName = "picture")
public class PictureModel   extends Model<PictureModel>{
	
	private static final Logger			LOGGER	= LoggerFactory.getLogger(PictureModel.class);

	public static final PictureModel	dao		= new PictureModel();
}

