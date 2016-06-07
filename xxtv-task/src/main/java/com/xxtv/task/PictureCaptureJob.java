package com.xxtv.task;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.chainsaw.Main;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.xxtv.core.kit.MongoKit;
import com.xxtv.web.model.PictureCatelogModel;
import com.xxtv.web.model.PictureMapModel;

/**
 * @author huwei
 * @date 2015年10月30日
 *
 */
public class PictureCaptureJob implements Job{

	private static Map<Integer,String> cateMap = new HashMap<Integer,String>();
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		List<Record> records = MongoKit.findAll("picture_catelogs");
		
		cateMap.put(0, "list_2_");
		cateMap.put(1, "list_6_");
		cateMap.put(2, "list_3_");
		cateMap.put(3, "list_4_");
		cateMap.put(4, "list_5_");
		cateMap.put(5, "list_1_");
		cateMap.put(6, "list_7_");
		cateMap.put(7, "list_12_");
		
		for (int i = 0; i < records.size(); i++)
		{
			final int index = i;
			final String name = records.get(i).getStr("name");
			final String url = records.get(i).getStr("url");
			capturePictureList(name,url,index);
//			new Thread(new Runnable()
//			{
//				@Override
//				public void run()
//				{
//					capturePictureList(name,url,index);
//				}
//			}).start();
			
		}
		
	}
	
	protected static void capturePictureList(String name,String url,int j) {
		String suffix = ".html";
		Document doc = null;
		try
		{
			doc = Jsoup.connect(url).get();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		int page = 2;
		int error = 1;
		while (doc != null) {
			if(error > 5){
				break;
			}
			try {
				Elements eles = doc.getElementsByClass("clearfix").get(5).getElementsByTag("li");
				for (int i = 0; i < eles.size(); i++)
				{
					try {
						System.out.println(j+1 +"---"+name + " 第" + (page-1) + "页数据处理 -----------------------------------");
						Element a= eles.get(i).child(0);
						String href = a.attr("href");
						String title = a.attr("title");
						String text =   eles.get(i).getElementsByTag("img").get(0).attr("data-original");						
						Map<String, Object> filter = new HashMap<String, Object>();
						filter.put("catelogs", j+1);
						filter.put("name", title);
						List<Record> r = MongoKit.findByCondition("picture_map",filter);
						
						Record record = new Record();
						record.set("name", title);
						record.set("catelogs", j+1);
						record.set("url","http://www.youzi4.com"+href);
						record.set("context",text);						
						if(r.size() < 1){
							DBCursor cursor=MongoKit.getCollection("picture_map").find().sort(new BasicDBObject("$natural",-1)).limit(1); 
							if(cursor.hasNext()){  
								record.set("_id", Integer.parseInt(cursor.next().get("_id").toString())+1);  
					        }else{  
					        	record.set("_id", 1);  
					        }
							MongoKit.save("picture_map", record);							
						}else{
							MongoKit.updateFirst("picture_map", filter, record);
						}
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				}
				
				doc = Jsoup.connect(url + cateMap.get(j)+ page + suffix).get();
				page ++;
			} catch (Exception e) {
				e.printStackTrace();
				error ++;
				continue;
			}
		}
		
		System.out.println(j+1 +"---"+name + " 任务完成  -----------------------------------");
		
	}
	
	private static void captureCatelogs() {
//		String url = "http://www.youzi4.com/xingganmeinv/list_2_1.html";
//		Document doc = null;
//		try {
//			doc = Jsoup.connect(url).get();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		Elements eles = doc.getElementsByClass("clearfix").get(5).getElementsByTag("li");
//		System.out.println(eles.size());
//		for (int i = 0; i < eles.size(); i++) {
//			Element a= eles.get(i).child(0);
//			String href = a.attr("href");
//			String title = a.attr("title");
//			String text =  eles.get(i).getElementsByTag("img").get(0).attr("data-original");
//			System.out.println(text);

//			new CatelogModel().set("url", href.trim()).set("name", text.trim())
//					.save();
//		}
	}
//	public static void main(String[] args) {
//		
//	}

}
