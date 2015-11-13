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

import com.xxtv.web.model.PictureCatelogModel;
import com.xxtv.web.model.PictureMapModel;

/**
 * @author huwei
 * @date 2015年10月30日
 *
 */
public class PictureCaptureJob implements Job{
	
	private static final Logger	LOGGER	= LoggerFactory.getLogger(MovieCaptureJob.class);
	private static Map<String,Integer> cateMap = new HashMap<String,Integer>();
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		final List<PictureCatelogModel> cates = PictureCatelogModel.dao.find("select * from picture_catelogs where id = 9");
		for (int i = 0; i < cates.size(); i++)
		{
			cateMap.put(cates.get(i).getStr("name"), cates.get(i).getInt("id"));
		}
		for (int i = 0; i < cates.size(); i++)
		{
			final int index = i;
			LOGGER.debug(cates.get(i).getStr("name") + " 分类启动线程开始抓取 -----------------------------------");
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					capturePictureList(cates.get(index));
				}
			}).start();
			
		}
		
	}
	
	protected static void capturePictureList(PictureCatelogModel pictureCatelogModel) {
		String url = pictureCatelogModel.getStr("url");
		String suffix = ".html";
		Document doc = null;
		try
		{
			doc = Jsoup.connect(url+"1"+suffix).get();
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
						System.out.println(pictureCatelogModel.getStr("name")+"第"+(page-1)+"页开始处理:");
						LOGGER.debug(pictureCatelogModel.getStr("name") + " 第" + (page-1) + "页数据处理 -----------------------------------");
						Element a= eles.get(i).child(0);
						String href = a.attr("href");
						String title = a.attr("title");
						String text =   eles.get(i).getElementsByTag("img").get(0).attr("data-original");
						PictureMapModel model = new PictureMapModel();
						model.set("name", title);
						model.set("catelogs", pictureCatelogModel.getInt("id"));
						model.set("url","http://www.youzi4.com"+href);
						model.set("context",text);
						model.save();
						System.out.println(model.getStr("name")+"保存");
					} catch (Exception e) {
						continue;
					}
				}
				
				doc = Jsoup.connect(url + page + suffix).get();
				page ++;
			} catch (Exception e) {
				error ++;
				continue;
			}
		}
		
		LOGGER.debug(pictureCatelogModel.getStr("name") + " 任务完成  -----------------------------------");
		
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
