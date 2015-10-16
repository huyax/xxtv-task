package com.xxtv.tools;

/**
 * 返回json的bean
 * 
 * @author zyz
 *
 */
public class ResultJson
{

	private int		isok;	// 1 成功 0失败

	private Object	data;

	public ResultJson()
	{

	}

	public ResultJson(int isok)
	{
		this.isok = isok;
	}

	public ResultJson(int isok, Object data)
	{
		this.isok = isok;
		this.data = data;
	}

	public int getIsok()
	{
		return isok;
	}

	public void setIsok(int isok)
	{
		this.isok = isok;
	}

	public Object getData()
	{
		return data;
	}

	public void setData(Object data)
	{
		this.data = data;
	}

	@Override
	public String toString()
	{
		return "ResultJson [isok=" + isok + ", data=" + data + "]";
	}

}
