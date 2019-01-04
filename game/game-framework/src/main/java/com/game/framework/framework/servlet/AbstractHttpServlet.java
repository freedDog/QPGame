package com.game.framework.framework.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import com.alibaba.fastjson.JSON;
import com.game.framework.component.log.Log;
import com.game.framework.utils.struct.result.Result;

/**
 * Servlet基类
 * AbstractHttpServlet.java
 * @author JiangBangMing
 * 2019年1月3日下午4:04:29
 */
@SuppressWarnings("serial")
public abstract class AbstractHttpServlet extends HttpServlet {

	/** 触发通用调用 **/
	private void onService(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 执行消息
		try {
			req.setCharacterEncoding("utf-8"); // 设置编码为UTF-8
			// 执行
			Object result = execute(req, resp);
			// 返回结果
			if (result != null) {
				// 字符串输出
				if (String.class.isInstance(result)) {
					write(resp, (String) result);
					return;
				}
				// 对象输出
				writeJson(resp, result);
			}
		} catch (Exception e) {
			exceptionCaught(req, resp, e);
		}
	}

	/** 触发错误 **/
	protected void exceptionCaught(HttpServletRequest req, HttpServletResponse resp, Exception e) {
		// 错误日志
		Log.error(getClass().getName() + " has an exception. From ip " + req.getRemoteAddr(), e);
		// 输出错误
		writeJson(resp, Result.error("server error! " + e.getLocalizedMessage()));
	}

	/** 执行处理 **/
	protected abstract Object execute(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse) throws Exception;

	/** 返回json格式 **/
	protected void writeJson(HttpServletResponse resp, Object result) {
		write(resp, JSON.toJSONString(result));
	}

	/** 返回输出数据 */
	protected void write(HttpServletResponse resp, String result, boolean utf8, boolean close) {
		// 字符串写入
		// PrintWriter writer = resp.getWriter();
		// writer.write(result);
		// writer.flush();
		// // 关闭
		// if (close) {
		// writer.close();
		// }

		try {
			// 数据流获取
			byte[] bytes = null;
			if (utf8) {
				bytes = result.getBytes("UTF-8");
			} else {
				bytes = result.getBytes();
			}
			// 数据流写入
			ServletOutputStream out = resp.getOutputStream();
			out.write(bytes);
			out.flush();
			// 关闭
			if (close) {
				out.close();
			}
		} catch (Exception e) {
			Log.error("输出数据错误! result=" + result, e);
		}
	}

	/** 返回输出数据 */
	protected void write(HttpServletResponse resp, String result) {
		write(resp, result, true, true);
	}

	@Override
	protected final void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// request.setCharacterEncoding("utf-8"); // 设置编码为UTF-8
		super.service(request, response); // 不许继承修改
	}

	@Override
	protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		onService(req, resp);
	}

	@Override
	protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		onService(req, resp);
	}
}
