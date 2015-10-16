package com.xxtv.base.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pager
{

	private int					begRow;			// 起始索引

	private int					endRow;			// 结束索引

	private int					pageNo;			// 当前页码

	private int					pageSize;		// 没有记录数

	private int					totalPage;		// 总页数

	private int					totalRecord;	// 总记录数

	private List<?>				result;			// 对应的当前页记录

	private boolean				isPage	= true;	// 是否分页

	private Map<String, Object>	params;			// 查询参数

	public Pager()
	{
		this.getMap();
	}

	public Pager(int pageSize, int pageNo)
	{
		this.pageSize = pageSize;
		this.pageNo = pageNo;
		if (pageSize > 0 && pageNo > 0)
		{
			this.begRow = (pageNo - 1) * pageSize;
			this.endRow = pageNo * pageSize;
		}
		this.getMap();
	}

	public int getBegRow()
	{
		return begRow;
	}

	public void setBegRow(int begRow)
	{
		this.begRow = begRow;
	}

	public int getEndRow()
	{
		return endRow;
	}

	public void setEndRow(int endRow)
	{
		this.endRow = endRow;
	}

	public int getPageSize()
	{
		return pageSize;
	}

	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}

	public int getPageNo()
	{
		return pageNo;
	}

	public void setPageNo(int pageNo)
	{
		this.pageNo = pageNo;
	}

	public int getTotalRecord()
	{
		return totalRecord;
	}

	public void setTotalRecord(int totalRecord)
	{
		this.totalRecord = totalRecord;
		int totalPage = totalRecord % pageSize == 0 ? totalRecord / pageSize : totalRecord / pageSize + 1;
		this.setTotalPage(totalPage);
	}

	public List<?> getResult()
	{
		return result;
	}

	public void setResult(List<?> result)
	{
		this.result = result;
	}

	public int getTotalPage()
	{
		return totalPage;
	}

	public void setTotalPage(int totalPage)
	{
		this.totalPage = totalPage;
	}

	public boolean isPage()
	{
		return isPage;
	}

	public void setPage(boolean isPage)
	{
		this.isPage = isPage;
	}

	public Map<String, Object> getParamsMap()
	{
		getMap().put("begRow", begRow);
		getMap().put("pageSize", pageSize);
		return params;
	}

	private Map<String, Object> getMap()
	{
		if (params == null)
		{
			synchronized (Pager.class)
			{
				params = new HashMap<String, Object>();
			}
		}
		return params;
	}

	public void addParam(String key, Object value)
	{
		getMap().put(key, value);
	}

	public Object getParam(String key)
	{
		return getMap().get(key);
	}

	public void delParam(String key)
	{
		if (getMap().containsKey(key))
		{
			getMap().remove(key);
		}
	}

	public void clearParam()
	{
		getMap().clear();
	}

	@Override
	public String toString()
	{
		return "Pager [pageSize=" + pageSize + ", pageNo=" + pageNo + ", totalRecord=" + totalRecord + ", totalPage=" + totalPage + ", begRow="
				+ begRow + ", endRow=" + endRow + ", result=" + result + ", params=" + params + "]";
	}

}
