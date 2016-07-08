package com.xxtv.task;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.jfinal.plugin.activerecord.Record;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.xxtv.core.kit.MongoKit;

/** 
* @author  huwei 
* @date 创建时间：2016年7月7日 上午10:39:44 
* @parameter  
*/
public class NovelCaptureJob  implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//mongodb版
		List<Record> records = MongoKit.findAll("novel_cats");
		for (int i = 0; i < records.size(); i++)
		{
			final String name = records.get(i).getStr("name");
			final String url = records.get(i).getStr("url");
			final int _id = records.get(i).getInt("_id");						
			captureNovelsList(name,url,_id);

}
	}

	public void captureNovelsList(String name, String url, int _id) {
		String suffix = ".aspx";
		int page = 1;
		Document doc = null;
		try
		{
			
			doc = Jsoup.connect(url+page+suffix).get();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		int error = 1;
		while (doc != null && page < 43) {
			if(error > 5){
				break;
			}
			
			try {
				Elements eles= doc.getElementsByClass("storelistbt5");
				for (int i = 0; i < eles.size(); i++)
				{
					String pic = eles.get(i).getElementsByClass("storelistbt5a").get(0).getElementsByTag("img").attr("src");
					String bookName = eles.get(i).getElementsByClass("storelistbt5a").get(0).getElementsByTag("a").get(1).text();
					String author = eles.get(i).getElementsByClass("storelistbt5a").get(0).getElementsByTag("p").get(0).text();
					String desc = eles.get(i).getElementsByClass("storelistbt5a").get(0).getElementsByTag("p").get(1).text();
					String detailurl = "http://www.quanbenshuwu.com" + eles.get(i).getElementsByClass("storelistbt5b").get(0).getElementsByTag("a").get(0).attr("href");
					
					Map<String, Object> filter = new HashMap<String, Object>();
					filter.put("catelog", _id);
					filter.put("name", bookName);
					List<Record> r = MongoKit.findByCondition("novel_name",filter);
					
					Record record = new Record();
					record.set("catelog", _id);
					record.set("name", bookName);
					record.set("author", author);
					record.set("desc", desc);
					record.set("icon", pic);
					record.set("detailurl", detailurl);
					if(r.size() < 1){
						DBCursor cursor=MongoKit.getCollection("novel_name").find().sort(new BasicDBObject("$natural",-1)).limit(1); 
						if(cursor.hasNext()){  
							record.set("_id", Integer.parseInt(cursor.next().get("_id").toString())+1);  
				        }else{  
				        	record.set("_id", 1);  
				        }
						MongoKit.save("novel_name", record);	
						System.out.println(_id+":"+page+"页:"+bookName+"--完成！");
					}else{
						System.out.println(_id+":"+page+"页:"+bookName+"--已插入！");
					}
				}
				page ++;
				String nextUrl = url  + page + suffix;
				System.out.println(nextUrl);
				doc = Jsoup.connect(nextUrl).get();
			}catch(Exception e){
				e.printStackTrace();
				error++;
				continue;
			}
		}
	}

}
