package com.xxtv.task;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxtv.web.model.CatelogModel;

public class MovieCaptureJob implements Job
{
	private static final Logger	LOGGER	= LoggerFactory.getLogger(MovieCaptureJob.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException
	{
		final List<CatelogModel> cates = CatelogModel.dao.find("select * from catelogs");
		for (int i = 0; i < cates.size(); i++)
		{
			final int index = i;
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						Document doc = Jsoup.connect(cates.get(index).getStr("url")).get();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}

				}
			}).start();
		}
	}

	private static void captureCatelogs()
	{
		String url = "http://www.66ys.tv/";
		Document doc = null;
		try
		{
			doc = Jsoup.connect(url).get();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		Elements eles = doc.getElementsByClass("menutv").get(0).child(0).getElementsByTag("li");
		for (int i = 0; i < eles.size(); i++)
		{

			String href = eles.get(i).child(0).attr("href");
			String text = eles.get(i).child(0).text();
			if ("首页".equals(text))
			{
				continue;
			}

			new CatelogModel().set("url", href.trim()).set("name", text.trim()).save();
		}

	}

}
