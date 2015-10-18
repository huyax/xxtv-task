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

import com.xxtv.tools.DateTools;
import com.xxtv.web.model.CatelogModel;
import com.xxtv.web.model.MovieModel;

public class MovieCaptureJob implements Job
{
	private static final Logger	LOGGER	= LoggerFactory.getLogger(MovieCaptureJob.class);
	private static Map<String,Integer> cateMap = new HashMap<String,Integer>();

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException
	{
		final List<CatelogModel> cates = CatelogModel.dao.find("select * from catelogs");
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
					captureMoviesList(cates.get(index));
				}
			}).start();
			
		}
	}

	protected void captureMoviesList(CatelogModel catelogModel) {
		String url = catelogModel.getStr("url");
		String suffix = ".html";
		if(9 == catelogModel.getInt("id")){  //3D
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
		while (doc != null) {
			if(error > 5){
				break;
			}
			page ++;
			try {
				Elements eles = doc.getElementsByClass("listBox").get(0).getElementsByTag("li");
				for (int i = 0; i < eles.size(); i++)
				{
					try {
						LOGGER.debug(catelogModel.getStr("name") + " 第" + (page-1) + "页数据处理 -----------------------------------");
						String detailurl = eles.get(i).child(0).child(0).attr("href");
						String icon = eles.get(i).child(0).child(0).child(0).attr("src");
						Elements listInfo = eles.get(i).child(1).getElementsByTag("p");
						String name = listInfo.get(0).text().substring(3).trim();
						Integer cate = cateMap.get(listInfo.get(1).text().substring(3).trim());
						Date date = DateTools.parseDate(listInfo.get(2).text().substring(3).trim(), DateTools.yyyy_MM_dd);
						String content = getContent(detailurl);
						MovieModel model = new MovieModel();
						model.set("icon", icon);
						model.set("detailurl", detailurl);
						model.set("icon", icon);
						model.set("name", name);
						model.set("catelog", cate);
						model.set("content", content);
						model.set("pub_date", date);
						model.set("cTime", new Date());
						model.save();
					} catch (Exception e) {
						continue;
					}
				}
				
				doc = Jsoup.connect(url + "index_" + page + suffix).get();
			} catch (Exception e) {
				error ++;
				continue;
			}
		}

		LOGGER.debug(catelogModel.getStr("name") + " 任务完成  -----------------------------------");
		
	}

	private String getContent(String detailurl) throws Exception {
		Document doc = Jsoup.connect(detailurl).get();
		Element e = doc.getElementById("text");
		return e.html();
	}

	private static void captureCatelogs() {
		String url = "http://www.66ys.tv/";
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Elements eles = doc.getElementsByClass("menutv").get(0).child(0)
				.getElementsByTag("li");
		for (int i = 0; i < eles.size(); i++) {

			String href = eles.get(i).child(0).attr("href");
			String text = eles.get(i).child(0).text();
			if ("首页".equals(text)) {
				continue;
			}

			new CatelogModel().set("url", href.trim()).set("name", text.trim())
					.save();
		}

	}

}
