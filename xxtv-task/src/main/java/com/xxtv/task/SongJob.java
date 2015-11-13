package com.xxtv.task;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxtv.web.model.LrcSongModel;
import com.xxtv.web.model.LrclSingerModel;

/**
 * @author huwei
 * @date 2015年11月12日
 *
 */
public class SongJob implements Job{
	private static final Logger	LOGGER	= LoggerFactory.getLogger(MovieCaptureJob.class);
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		final List<LrclSingerModel> cates = LrclSingerModel.dao.find("select * from lrc_singer ");
		for (int i = 0; i < cates.size(); i++)
		{
			int index = i;
			captureSongList(cates.get(index));			
		}
		}
	
	/**
	 * 
	 *@author huwei
	 * 2015年11月12日
	 *
	 *@param lrcCatelogsModel
	 */
	protected static void captureSongList(LrclSingerModel lrclSingerModel) {
		String url = lrclSingerModel.getStr("url");
		System.out.println(lrclSingerModel.getStr("singer_name")+"-开始爬取!");
		String newUrl = url.substring(0,url.lastIndexOf('-'));
		String suffix = ".html";
		Document doc = null;
		int page = 1;
		int error = 1;
		String songUrl = "";
		String songName = "";
		String album = "";
		while (error < 10) {
			try {	
					doc = Jsoup.connect(newUrl + "-"+ page + suffix).get();
					System.out.println(" 第" + page + "页数据处理 -----------------------------------");					
					Elements eles = doc.getElementsByTag("tbody").get(0).getElementsByTag("tr");	
					if(eles.size() == 1){
						error = error+3;
					}
					for(int i = 1; i < eles.size(); i++){
						songUrl = "http://www.lrcgc.com/"+ eles.get(i).getElementsByTag("a").get(0).attr("href");
						songName = eles.get(i).getElementsByTag("a").get(0).text();
						album = eles.get(i).getElementsByTag("td").get(1).text();
						System.out.println(songName+"-----"+songUrl);
						LrcSongModel model = new LrcSongModel();
						model.set("song_name", songName);
						model.set("album", album);
						model.set("url", songUrl);
						model.save();
					}
				    page ++;										
			} catch (Exception e) {
				error ++;
				continue;
			}
		}
		System.out.println(lrclSingerModel.getStr("singer_name")+"歌曲爬取完成！");
}
/*	public static void main(String[] args) {
		String url = "http://www.lrcgc.com/songlist-106-1.html";
		String newUrl = url.substring(0,url.lastIndexOf('-'));
		String suffix = ".html";
		Document doc = null;
		int page = 1;
		int error = 1;
		String songUrl = "";
		String songName = "";
		String album = "";
		while (error < 10) {
			try {	
				doc = Jsoup.connect(newUrl + "-"+ page + suffix).get();
				System.out.println(" 第" + page + "页数据处理 -----------------------------------");					
				Elements eles = doc.getElementsByTag("tbody").get(0).getElementsByTag("tr");	
				for(int i = 1; i < eles.size(); i++){
					songUrl = "http://www.lrcgc.com/"+ eles.get(i).getElementsByTag("a").get(0).attr("href");
					album = eles.get(i).getElementsByTag("td").get(1).text();
					songName = eles.get(i).getElementsByTag("a").get(0).text();
					System.out.println(songName+"-----"+album+"-----------"+songUrl);
				}
			    page ++;	
	}catch (Exception e) {
		error ++;
		continue;
	}
}}*/
	
	}