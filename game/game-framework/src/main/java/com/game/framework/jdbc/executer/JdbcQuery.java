package com.game.framework.jdbc.executer;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.game.framework.jdbc.executer.mapper.JdbcResultSet;
import com.game.framework.jdbc.executer.mapper.JdbcRowMapper;
import com.game.framework.jdbc.executer.param.JdbcParam;
import com.game.framework.jdbc.utils.JdbcUtils;



/**
 * Jdbc查询器
 * JdbcQuery.java
 * @author JiangBangMing
 * 2019年1月3日下午5:01:59
 */
public abstract class JdbcQuery<T> extends JdbcExecuter<T>
{
	public JdbcQuery(String sql, Class<?>[] parameterTypes, Type retType) throws Exception
	{
		super(sql, parameterTypes, retType);
	}

	@Override
	public Process process(Object... args) throws Exception
	{
		return process(this.sql, this.params, args);
	}

	@Override
	protected PreparedStatement createPreparedStatement(Connection conn, JdbcExecuter.Process process) throws Exception
	{
		final Process process0 = (Process) process;
		return createPreparedStatement(conn, process0.runSql, process0.args);
	}

	@Override
	protected List<T> doInPreparedStatement(PreparedStatement ps, JdbcExecuter.Process process) throws Exception
	{
		return doInPreparedStatement(ps, this.mapper);
	}

	/** 获取?替换字符串 **/
	protected static String getReplaceString(int size)
	{
		StringBuilder strBdr = new StringBuilder();
		for (int i = 0; i < size; i++)
		{
			if (i > 0)
			{
				strBdr.append(",");
			}
			strBdr.append("?");
		}
		return strBdr.toString();
	}

	/** 解析参数 **/
	public static Process process(String sql, List<JdbcParam> params, Object... args) throws Exception
	{
		int pszie = (params != null) ? params.size() : 0;
		if (pszie <= 0)
		{
			// 空处理
			Process process = new Process(sql, new Object[0]); // 空参数查找
			return process;
		}

		String runSql = null;
		List<Object> argList = new ArrayList<Object>();
		// 倒序处理
		for (int i = pszie - 1; i >= 0; i--)
		{
			JdbcParam param = params.get(i);
			// 获取参数
			int index = param.getArgIndex();
			Object arg = args[index];

			// 检测是否是数组
			if (arg instanceof List)
			{
				@SuppressWarnings({ "rawtypes", "unchecked" })
				List<Object> list = (List) arg;
				int paramCount = list.size();
				if (paramCount >= 2)
				{
					runSql = (runSql == null) ? new String(sql) : runSql;
					// 替换位置
					String rstr = getReplaceString(paramCount);
					StringBuilder strBdr = new StringBuilder();
					strBdr.append(runSql.substring(0, param.getSubStart(JdbcParam.SUBINDEX_RUN)));
					strBdr.append(rstr);
					strBdr.append(runSql.substring(param.getSubEnd(JdbcParam.SUBINDEX_RUN)));
					runSql = strBdr.toString();
				}

				// 遍历参数添加
				for (int j = 0; j < list.size(); j++)
				{
					Object carg = list.get(j);
					Object obj = JdbcUtils.toSqlObject(JdbcParam.getObjValue(carg, param));
					argList.add(obj);
				}
			}
			else
			{
				// 获取参数
				argList.add(JdbcUtils.toSqlObject(param.getParam(args, -1)));
			}
			// System.out.println(i + " param[" + param + "] = " + arg);
		}
		// 数组摆正
		if (argList.size() >= 2)
		{
			Collections.reverse(argList);
		}

		// 创建过程
		Object[] args0 = argList.toArray(new Object[0]);
		Process process = new Process(((runSql != null) ? runSql : sql), args0);
		return process;
	}

	/** 创建PreparedStatement **/
	public static PreparedStatement createPreparedStatement(Connection conn, String sql, Object[] args) throws Exception
	{
		// 创建PreparedStatement, 如果Process有语句则用, 否则用源生.
		PreparedStatement ps = conn.prepareStatement(sql);

		// 遍历写入单个数据
		int asize = (args != null) ? args.length : 0;
		for (int j = 0; j < asize; j++)
		{
			// 获取数据
			Object arg = args[j];
			if (arg == null)
			{
				ps.setObject(j + 1, null, java.sql.Types.NULL); // 空处理
				continue;
			}
			int sqlType = JdbcUtils.getSqlType(arg.getClass());
			try
			{
				ps.setObject(j + 1, arg, sqlType);
			}
			catch (Exception e)
			{
				System.err.println("设置参数错误! index=" + (j + 1) + " sqltype=" + sqlType + " arg=" + arg);
				throw e;
			}
		}
		return ps;
	}

	/** 处理结果回馈 **/
	public static <T> List<T> doInPreparedStatement(PreparedStatement ps, JdbcRowMapper<T> mapper) throws Exception
	{
		// 执行
		boolean result = ps.execute();
		if (!result)
		{
			// throw new Exception("sql执行失败! " + sql + " " + process);
			throw new Exception("sql执行失败! ");
		}

		// 检测有没有返回类型(处理的map)
		if (mapper == null)
		{
			return null;
		}

		// 处理返回值
		ResultSet resultSet = null;
		try
		{
			resultSet = ps.getResultSet();

			// 遍历处理
			JdbcResultSet rs = new JdbcResultSet(resultSet);
			List<T> list = new ArrayList<T>();
			int index = 0;
			while (resultSet.next())
			{
				// 反射解析对象
				T obj = mapper.mapRow(rs, index++);
				list.add(obj);
			}
			// 返回数据
			return list;
		}
		finally
		{
			close(resultSet);
		}
	}

	/** 查询过程 **/
	public static class Process implements JdbcExecuter.Process
	{
		public final String runSql;
		public final Object[] args;

		public Process(String sql, Object[] args)
		{
			this.runSql = sql;
			this.args = args;
		}

		@Override
		public String toString()
		{
			return "[" + runSql + " " + Arrays.toString(args) + "]";
		}
	}

}
