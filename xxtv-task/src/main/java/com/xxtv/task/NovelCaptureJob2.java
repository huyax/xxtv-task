package com.xxtv.task;

import java.io.IOException;
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
* @date 创建时间：2016年7月7日 下午1:53:09 
* @parameter  
*/
public class NovelCaptureJob2  implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// mongodb版
		List<Record> records = MongoKit.findAll("novel_name");
		for (int i = 0; i < records.size(); i++) {
			final String name = records.get(i).getStr("name");
			final String url = records.get(i).getStr("detailurl");
			final int _id = records.get(i).getInt("_id");
			final int cate_id = records.get(i).getInt("catelog");
			captureNovelsList(name, url, _id,cate_id);

		}
	}

	public void captureNovelsList(String name, String url, int _id,int cate_id) {
		Document doc = null;
		try
		{
			
			doc = Jsoup.connect(url).get();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		if (doc != null ) {

			try {
				String property= doc.getElementsByClass("ti").get(0).getElementsByTag("li").get(1).getElementsByTag("label").get(0).text();
				String totalNums = doc.getElementsByClass("ti").get(0).getElementsByTag("li").get(2).getElementsByTag("label").get(0).text();
				Elements eles = doc.getElementById("readlist").getElementsByTag("a");
				
				for (int i = 0; i < eles.size(); i++)
				{
					String capName = eles.get(i).text();
					String contextUrl = eles.get(i).attr("href");
					
					int error2 = 1;
					while (error2 >= 1){
						if (error2 > 4) {
							break;
						}
					try {
						doc = Jsoup.connect("http://www.quanbenshuwu.com" + contextUrl).get();
						String text = doc.getElementById("content").getElementsByTag("p").html();
						Map<String, Object> filter = new HashMap<String, Object>();
						filter.put("name", capName);
						filter.put("bookid", _id);
						List<Record> r = MongoKit.findByCondition("novel_name",filter);
						
						Record record = new Record();
						record.set("name", capName);
						record.set("book_name", name);
						record.set("book_id", _id);
						record.set("cate_id", cate_id);
						record.set("property", property);
						record.set("totalNums", totalNums);
						record.set("text", text);
						if(r.size() < 1){
							DBCursor cursor=MongoKit.getCollection("novel_text").find().sort(new BasicDBObject("$natural",-1)).limit(1); 
							if(cursor.hasNext()){  
								record.set("_id", Integer.parseInt(cursor.next().get("_id").toString())+1);  
					        }else{  
					        	record.set("_id", 1);  
					        }
							MongoKit.save("novel_text", record);	
							System.out.println(name+":"+capName+":--完成！");
						}else{
							System.out.println(name+":"+capName+"--已插入！");
						}
						error2--;
						
					} catch (Exception e) {
						e.printStackTrace();
						error2 += 1;
						continue;
					}
				}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
