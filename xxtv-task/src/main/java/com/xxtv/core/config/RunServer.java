package com.xxtv.core.config;

import com.jfinal.core.JFinal;

public class RunServer
{

	/**
	 * 建议使用 JFinal 手册推荐的方式启动项目
	 * 运行此 main 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此
	 */
	public static void main(String[] args)
	{
		JFinal.start("WebRoot", 80, "/", 5);
	}
}
