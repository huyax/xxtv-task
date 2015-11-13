package com.xxtv.web.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Model;
import com.xxtv.core.plugin.annotation.Table;

/**
 * @author huwei
 * @date 2015年11月12日
 *
 */
 @SuppressWarnings("serial")
 @Table(tableName = "lrc_song")
public class LrcSongModel extends Model<LrcSongModel>{
	 
	 private static final Logger			LOGGER	= LoggerFactory.getLogger(LrcSongModel.class);

		public static final LrcSongModel	dao		= new LrcSongModel();

}