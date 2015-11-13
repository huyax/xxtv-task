package com.xxtv.task;
import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxtv.web.model.LrcCatelogsModel;
import com.xxtv.web.model.LrclSingerModel;

public class SingerJob   implements Job{

	private static final Logger	LOGGER	= LoggerFactory.getLogger(MovieCaptureJob.class);


	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		final List<LrcCatelogsModel> cates = LrcCatelogsModel.dao.find("select * from lrc_catelogs ");
		for (int i = 0; i < cates.size(); i++)
		{
			int index = i;
			captureSingerList(cates.get(index));			
		}
		}
	
	/**
	 * 
	 *@author huwei
	 * 2015年11月12日
	 *
	 *@param lrcCatelogsModel
	 */
	protected static void captureSingerList(LrcCatelogsModel lrcCatelogsModel) {
		String url = lrcCatelogsModel.getStr("url");
		System.out.println(lrcCatelogsModel.getStr("name")+"-开始爬取!");
		String singerUrl = "";
		String singerName = "";
		try {
			Document doc = Jsoup.connect(url).get();
			Elements eles = doc.getElementsByClass("namelist");
			for(int i = 1; i < eles.size() ;i++){
				Elements eles2 = eles.get(i).getElementsByTag("a");
				for(Element el : eles2){
					singerUrl = el.attr("href");
					singerName  = el.text();
					LrclSingerModel model = new LrclSingerModel();
					System.out.println(singerName+":"+singerUrl);
					model.set("url", "http://www.lrcgc.com/"+singerUrl);
					model.set("singer_name", singerName);
					model.save();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOGGER.debug(lrcCatelogsModel.getStr("name") + " 任务完成  -----------------------------------");
	}

}