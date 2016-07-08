package com.xxtv.task;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.xxtv.core.kit.MongoKit;
import com.xxtv.tools.DateTools;
import com.xxtv.web.model.CatelogModel;

public class MovieCaptureJob implements Job
{
	private static final Logger	LOGGER	= LoggerFactory.getLogger(MovieCaptureJob.class);
	private static Map<String,Integer> cateMap = new HashMap<String,Integer>();

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException
	{	
		//mongodb版
		List<Record> records = MongoKit.findAll("catelogs");
		for (int i = 0; i < records.size(); i++)
			{
				cateMap.put(records.get(i).getStr("name"), records.get(i).getInt("_id"));
			}

		for (int i = 0; i < records.size(); i++)
			{
				final String name = records.get(i).getStr("name");
				final String url = records.get(i).getStr("url");
				final int _id = records.get(i).getInt("_id");			
				LOGGER.debug(records.get(i).getStr("name") + " 分类启动线程开始抓取 -----------------------------------");
				
				captureMoviesList(name,url,_id);
//				new Thread(new Runnable()
//				{
//					@Override
//					public void run()
//					{	
//						captureMoviesList(name,url,_id);
//					}
//				}).start();
//				
//			}
	}
	}
	protected void captureMoviesList(String name,String url,int _id) {
		
		String suffix = ".html";
		if(7 < _id ){  
			suffix = ".htm";
		}
		Document doc = null;
		try
		{
			
			doc = Jsoup.connect(url).get();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		int page = 1;
		int error = 1;
		while (doc != null && page < 2) {
			if(_id == 16){
				System.out.println("动画片");
			}
			if(error > 5){
				break;
			}
			page ++;
			try {
				Elements eles = doc.getElementsByClass("listBox").get(0).getElementsByTag("li");
				for (int i = 0; i < eles.size(); i++)
				{
					try {
						System.out.println(_id+"-----"+name + " 第" + (page-1) + "页数据处理 -----------------------------------");
						String detailurl = eles.get(i).child(0).child(0).attr("href");
						String icon = eles.get(i).child(0).child(0).child(0).attr("src");
						Elements listInfo = eles.get(i).child(1).getElementsByTag("p");
						String name2 = listInfo.get(0).text().substring(3).trim();
						Integer cate = cateMap.get(listInfo.get(1).text().substring(3).trim());
						Date date = DateTools.parseDate(listInfo.get(2).text().substring(3).trim(), DateTools.yyyy_MM_dd);
						String content = getContent(detailurl);
						content = content.replace("#ffffbb", "#0f0f0f");
						Map<String, Object> filter = new HashMap<String, Object>();
						filter.put("catelog", cate);
						filter.put("name", name2);
						List<Record> r = MongoKit.findByCondition("movie",filter);
						
						Record record = new Record();
						record.set("icon", icon);
						record.set("detailurl", detailurl);
						record.set("name", name2);
						record.set("catelog", cate == null?9:cate);
						record.set("content", content);
						record.set("pub_date", date);
						record.set("cTime", new Date());						
						if(r.size() < 1){
							DBCursor cursor=MongoKit.getCollection("movie").find().sort(new BasicDBObject("$natural",-1)).limit(1); 
							if(cursor.hasNext()){  
								record.set("_id", Integer.parseInt(cursor.next().get("_id").toString())+1);  
					        }else{  
					        	record.set("_id", 1);  
					        }
							MongoKit.save("movie", record);							
						}else{
							MongoKit.updateFirst("movie", filter, record);
						}
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				}
				
				doc = Jsoup.connect(url + "index_" + page + suffix).get();
			} catch (Exception e) {
				error ++;
				continue;
			}
		}

		System.out.println(name + " 任务完成  -----------------------------------");
		
	}

	private String getContent(String detailurl) throws Exception {
		Document doc = Jsoup.connect(detailurl).get();
		Element e = doc.getElementById("text");
		return e.html();
	}


}
