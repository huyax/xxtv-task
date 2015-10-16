package com.xxtv.web.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Model;
import com.xxtv.core.plugin.annotation.Table;

@SuppressWarnings("serial")
@Table(tableName = "movie")
public class MovieModel extends Model<MovieModel>
{
	private static final Logger			LOGGER	= LoggerFactory.getLogger(MovieModel.class);

	public static final MovieModel	dao		= new MovieModel();
}
