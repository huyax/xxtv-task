package com.xxtv.task;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xxtv.core.kit.MongoKit;
import com.xxtv.web.model.PictureMapModel;
import com.xxtv.web.model.PictureModel;

public class ImageCaptureJob implements Job{

	
	private static final Logger	LOGGER	= LoggerFactory.getLogger(MovieCaptureJob.class);
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		final List<PictureMapModel> cates = PictureMapModel.dao.find("select * from picture_map  ");
		for (int i = 0; i < cates.size(); i++)
		{
			int index = i;
			capturePictureList(cates.get(index));			
		}
		}

	protected void capturePictureList(PictureMapModel pictureMapModel) {
		String url = pictureMapModel.getStr("url");
		String newUrl = url.substring(0,url.lastIndexOf('.'));
		String suffix = ".html";
		Document doc = null;
		int page = 1;
		int error = 1;
		while (error < 10) {
			try {	
				if(page == 1){
					doc = Jsoup.connect(newUrl+suffix).get();
				}else{
					doc = Jsoup.connect(newUrl + "_"+ page + suffix).get();
				}
				
												
						Element name = doc.getElementsByClass("picinfo").get(0).getElementsByTag("h2").get(0);
						Element nowPage = doc.getElementsByClass("nowpage").get(0);
//						Element totalpage = doc.getElementsByClass("totalpage").get(0);
						Element img = doc.getElementsByClass("imgbox").get(0).getElementsByTag("img").get(0);
						//保存图片
						PictureModel model = new PictureModel();
						model.set("name", name.text());
						model.set("seat", nowPage.text());
						model.set("url",img.attr("data-original"));	
						model.set("map_id",pictureMapModel.getInt("id"));
						long count = Db.queryLong("select count(0) from picture where map_id=? and name=?",model.getInt("map_id"),model.getStr("name"));
						if(count < 1){
						model.save();
						Record record = new Record();
						record.setColumns(model);
						MongoKit.save("picture", record);
						
						System.out.println(pictureMapModel.getStr("name") + " 第" + page + "页数据处理 -----------------------------------");
						}
						
				       page ++;	
			} catch (Exception e) {
				error ++;
				continue;
			}
		}
		System.out.println(pictureMapModel.getStr("name")+"套图爬取完成！");
		LOGGER.debug(pictureMapModel.getStr("name") + " 任务完成  -----------------------------------");
	}
		
	private static void captureCatelogs() {
		String url = "http://www.youzi4.com/xingganmeinv/72301111.html";
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Element ele = doc.getElementsByClass("picinfo").get(0).getElementsByTag("h2").get(0);
		Element nowPage = doc.getElementsByClass("nowpage").get(0);
		Element totalpage = doc.getElementsByClass("totalpage").get(0);
		Element img = doc.getElementsByClass("imgbox").get(0).getElementsByTag("img").get(0);
		
		System.out.println(ele.text());
		System.out.println(nowPage.text());
		System.out.println(totalpage.text());
		System.out.println(img.attr("data-original"));
		
//		System.out.println(eles.size());
//		for (int i = 0; i < eles.size(); i++) {
//			Element a= eles.get(i).child(0);
//			String href = a.attr("href");
//			String title = a.attr("title");
//			String text =  eles.get(i).child(0).html();
//			System.out.println(text);
//
//		}
	}


}
