package com.game.framework.jdbc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.game.framework.jdbc.executer.JdbcUpdate;


/**
 * 批量更新过程
 * BatchProcess.java
 * @author JiangBangMing
 * 2019年1月3日下午5:07:10
 */
public class BatchProcess extends JdbcUpdate.Process {
	public final List<Object[]> batchBaseArgs;

	public BatchProcess()
	{
		batchBaseArgs = new ArrayList<Object[]>();
	}

	/**
	 * 合并处理过程
	 * 
	 * @param process
	 * @return
	 */
	public synchronized boolean combine(BatchProcess process)
	{
		if (process == null)
		{
			return false;
		}

		// 检测数量
		int count = (process.batchArgs != null) ? process.batchArgs.size() : 0;
		if (count < 0)
		{
			return true;
		}

		// 添加
		this.batchArgs.addAll(process.batchArgs);
		this.batchBaseArgs.addAll(process.batchBaseArgs);

		return true;
	}

	/**
	 * 拆分处理过程
	 * 
	 * @param count
	 * @return
	 */
	public synchronized BatchProcess extract(int count)
	{
		count = Math.max(count, 1); // 至少1个
		// 检测数量
		int count0 = (this.batchArgs != null) ? this.batchArgs.size() : 0;
		if (count0 < 0)
		{
			return null; // 没有了
		}

		// 创建新过程
		BatchProcess process0 = new BatchProcess();
		// 如果数量不够, 返回全部
		if (count0 < count)
		{
			process0.batchArgs.addAll(this.batchArgs); // 全部添加
			process0.batchBaseArgs.addAll(this.batchBaseArgs); // 全部添加
			this.batchArgs.clear(); // 清除自身全部
			this.batchBaseArgs.clear();
			return process0;
		}

		// 提取固定数量
		int getcount = 0;
		Iterator<Object[]> iter = this.batchArgs.iterator();
		Iterator<Object[]> iter0 = this.batchBaseArgs.iterator();
		while (iter.hasNext())
		{
			Object[] args = iter.next();
			Object[] args0 = iter0.next();
			process0.batchArgs.add(args);
			process0.batchBaseArgs.add(args0);
			iter.remove();
			iter0.remove();
			// 检测数量
			getcount++;
			if (getcount >= count)
			{
				break;
			}
		}
		return process0;
	}

	/**
	 * 获取批量更新数量
	 * 
	 * @return
	 */
	public synchronized int count()
	{
		return (this.batchArgs != null) ? this.batchArgs.size() : 0;
	}

	@Override
	public synchronized String toString()
	{
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("(");
		strBuf.append(this.count());
		strBuf.append(")");
		strBuf.append("[");
		// for (Object[] args : batchArgs) {
		// strBuf.append(Arrays.toString(args));
		// strBuf.append(",");
		// }
		for (Object[] args : batchBaseArgs)
		{
			strBuf.append(Arrays.toString(args));
			strBuf.append(",");
		}
		strBuf.append("]");
		return strBuf.toString();
	}
}
