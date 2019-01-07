package com.game.base.service.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.framework.utils.StringUtils;

/**
 * http过滤器
 * HttpFilters.java
 * @author JiangBangMing
 * 2019年1月7日下午6:25:37
 */
public class HttpFilters
{
	protected Map<String, List<String>> filters;

	public HttpFilters()
	{
		filters = new HashMap<>(); // 访问过滤
	}

	/** 设置ip过滤 **/
	public boolean setFilter(String filterKey, String filterStr)
	{
		if (StringUtils.isEmpty(filterKey) || StringUtils.isEmpty(filterStr))
		{
			return false;
		}

		// 获取过滤列表
		List<String> list = filters.get(filterKey);
		if (list == null)
		{
			list = new ArrayList<>();
			filters.put(filterKey, list);
		}

		// 处理过滤
		String[] values = filterStr.split("\\|");
		for (String v : values)
		{
			// 判断是否是全部通过
			if (v.equals("*"))
			{
				list.clear();
				list.add(v); // 后面的不管了.
				break;
			}
			list.add(v);
		}
		return true;
	}

	// 检测ip过滤
	public boolean checkFilter(String url, String ip)
	{
		// 检测url是否在过滤列表中
		String findKey = null;
		for (String key : filters.keySet())
		{
			// 判断路径是否符合
			if (key.equals("*") || url.indexOf(key) == 0)
			{
				findKey = key;
				break;
			}
		}

		// 判断是否有限制
		if (findKey == null)
		{
			return true;
		}

		// 获取过滤列表
		List<String> list = filters.get(findKey);
		if (list == null || list.size() <= 0)
		{
			return false; // 没有限制
		}

		// 遍历检测
		for (String white : list)
		{
			if (white.equals("*"))
			{
				return true; // 不限制
			}
			else if (white.equals("!*"))
			{
				return false; // 都拒绝
			}
			// 判断是否符合
			if (ip.equals(white))
			{
				return true;
			}
		}
		return false;
	}
}

