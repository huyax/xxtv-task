package com.xxtv.task;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxtv.tools.BaseMD5Util;
import com.xxtv.web.model.LrcLrcModel;
import com.xxtv.web.model.LrcSongModel;

/**
 * @author huwei
 * @date 2015年11月13日
 *
 */
public class LrcJob implements Job{
	private static final Logger	LOGGER	= LoggerFactory.getLogger(MovieCaptureJob.class);
	private static final String locaPath ="d://lrc";
	private static final String suffix = "lrc";
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		final List<LrcSongModel> cates = LrcSongModel.dao.find("select * from lrc_song ");
		for (int i = 0; i < cates.size(); i++)
		{
			LrcSongModel  m = cates.get(i);
			String url = m.getStr("url");
			Document doc = null;
			int error = 1;
			do {
				try {
					doc = Jsoup.connect(url).get();
					String singerAndsong = doc.getElementsByClass("f16").get(0).text();
					String text  = doc.getElementsByClass("f14").get(0).text();
					String singer = singerAndsong.split(" - ")[0].trim().substring(singerAndsong.split(" - ")[0].trim().indexOf("：")+1);
					String song = singerAndsong.split(" - ")[1].trim();
					String downUrl = "http://www.lrcgc.com/"+doc.getElementById("J_downlrc").attr("href");
					String album = m.getStr("album");
					File file =new File(locaPath);
					if(!file.exists()){
						file.mkdir();
					}
					String locaPath = downLrcFile(downUrl);
					File file2 = new File(locaPath);
					long size = file2.length();
					String md5 = BaseMD5Util.getMd5ByFile(file2);
					LrcLrcModel model = new LrcLrcModel();
					model.set("song_name", song);
					model.set("singer_name", singer);
					model.set("album_name", album);
					model.set("text", text);
					model.set("local_path", locaPath);
					model.set("suffix", suffix);
					model.set("size", size);
					model.set("MD5", md5);
					model.set("status", 0);
					model.save();
					error--;
				} catch (Exception e) {
					error =2 ;
				}
			} while (error == 2);

			System.out.println(m.getStr("song_name")+"歌曲爬取完成！");
		}
	}
	/**
	 * 
	 *@author huwei
	 * 2015年11月13日
	 * @param downUrl 
	 *
	 *@return
	 * @throws IOException 
	 */
	private static String downLrcFile(String src) throws Exception {
		  //Exctract the name of the image from the src attribute
        int indexname = src.lastIndexOf("/");

        if (indexname == src.length()) {
            src = src.substring(1, indexname);
        }

        indexname = src.lastIndexOf("/");
        String name = src.substring(indexname, src.length());

        System.out.println(name);

        //Open a URL Stream
        URL url = new URL(src);
        InputStream in = url.openStream();

        OutputStream out = new BufferedOutputStream(new FileOutputStream( locaPath+"/"+ name));

        for (int b; (b = in.read()) != -1;) {
            out.write(b);
        }
        out.close();
        in.close();
        
        return locaPath+"/"+ name;
	}
/* public static void main(String[] args) {
	 String url ="http://www.lrcgc.com/lyric-2558-157.html";
	 
		Document doc = null;
		int error = 1;
		do {
			try {
				doc = Jsoup.connect(url).get();
				String singerAndsong = doc.getElementsByClass("f16").get(0).text();
				String text  = doc.getElementsByClass("f14").get(0).text();
				String singer = singerAndsong.split(" - ")[0].trim().substring(singerAndsong.split(" - ")[0].trim().indexOf("：")+1);
				String song = singerAndsong.split(" - ")[1].trim();
				String downUrl = "http://www.lrcgc.com/"+doc.getElementById("J_downlrc").attr("href");
				File file =new File(locaPath);
				if(!file.exists()){
					file.mkdir();
				}
				String locaPath = downLrcFile(downUrl);
				File file2 = new File(locaPath);
				long size = file2.length();
				String md5 = BaseMD5Util.getMd5ByFile(file2);
				System.out.println(singer);
				System.out.println(song);
				System.out.println(text);
				System.out.println(locaPath);
				System.out.println(size);
				System.out.println(md5);
				error--;
			} catch (Exception e) {
				error =2 ;
			}
		} while (error == 2);

		System.out.println("歌曲爬取完成！");
}*/
}
