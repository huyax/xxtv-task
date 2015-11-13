package com.xxtv.core.plugin.quartz;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.IPlugin;
import com.xxtv.core.kit.ReflectKit;


public class QuartzPlugin implements IPlugin
{
	private static final String	JOB		= "job";

	private static final Logger	LOGGER	= LoggerFactory.getLogger(QuartzPlugin.class);

	private SchedulerFactory	schedulerFactory;

	private Scheduler			scheduler;

	private String				config	= "quartz.properties";

	private Properties			properties;

	public QuartzPlugin(String config)
	{
		this.config = config;
	}

	public QuartzPlugin()
	{
	}

	@Override
	public boolean start()
	{
		schedulerFactory = new StdSchedulerFactory();
		try
		{
			scheduler = schedulerFactory.getScheduler();
		}
		catch (SchedulerException e)
		{
			throw new RuntimeException(e);
		}
		loadProperties();
		Enumeration<Object> enums = properties.keys();
		while (enums.hasMoreElements())
		{
			String key = enums.nextElement() + "";
			if (!key.endsWith(JOB) || !isEnableJob(enable(key)))
			{
				continue;
			}
			String jobClassName = properties.get(key) + "";
			String jobCronExp = properties.getProperty(cronKey(key)) + "";
			Class<Job> clazz = ReflectKit.on(jobClassName).get();

			JobDetail job = JobBuilder.newJob(clazz).withIdentity(jobClassName, jobClassName).build();
			CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger", jobClassName)
					.withSchedule(CronScheduleBuilder.cronSchedule(jobCronExp)).build();

			Date ft = null;
			try
			{
				ft = scheduler.scheduleJob(job, trigger);
				scheduler.start();
			}
			catch (SchedulerException e)
			{
				throw new RuntimeException(e);
			}
			LOGGER.debug(job.getKey() + " has been scheduled to run at: " + ft + " and repeat based on expression: " + trigger.getCronExpression());
		}
		return true;
	}

	private String enable(String key)
	{
		return key.substring(0, key.lastIndexOf(JOB)) + "enable";
	}

	private String cronKey(String key)
	{
		return key.substring(0, key.lastIndexOf(JOB)) + "cron";
	}

	private boolean isEnableJob(String enableKey)
	{
		Object enable = properties.get(enableKey);
		if (enable != null && "false".equalsIgnoreCase((enable + "").trim()))
		{
			return false;
		}
		return true;
	}

	private void loadProperties()
	{
		properties = new Properties();
		InputStream is = QuartzPlugin.class.getClassLoader().getResourceAsStream(config);
		try
		{
			properties.load(is);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		LOGGER.debug("------------load Propteries---------------");
		LOGGER.debug(properties.toString());
		LOGGER.debug("------------------------------------------");
	}

	@Override
	public boolean stop()
	{
		try
		{
			scheduler.shutdown();
		}
		catch (SchedulerException e)
		{
			throw new RuntimeException(e);
		}
		return true;
	}

}
