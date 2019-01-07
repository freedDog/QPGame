package com.game.gate.http;

import java.io.File;

import com.game.framework.framework.server.http.netty.NettyHttpUtils;
import com.game.framework.utils.FileUtils;
import com.game.framework.utils.StringUtils;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;

/**
 * 服务文件读取<br>
 * 把服务器上的文件读取输出到页面
 * FileServlet.java
 * @author JiangBangMing
 * 2019年1月7日下午6:28:37
 */
public class FileServlet {
	protected String rootPath; // 文件根目录
	protected String prefix; // 前缀

	public FileServlet(String rootPath, String prefix) {
		this.rootPath = rootPath;
		this.prefix = prefix;
	}

	/** 请求处理 **/
	public void execute(String url, ChannelHandlerContext ctx, HttpRequest request) {
		int pid = url.indexOf(prefix);
		String fpath = (pid >= 0) ? url.substring(pid + prefix.length()) : null;
		if (StringUtils.isEmpty(fpath)) {
			NettyHttpUtils.writeEmpty(ctx);
			return;
		}
		// 执行文件
		executeByFilePath(fpath, ctx, request);
	}

	/** 请求处理 **/
	public void executeByFilePath(String filePath, ChannelHandlerContext ctx, HttpRequest request) {
		// 检测文件是否存在
		String filePath0 = rootPath + File.separator + filePath;
		File file = new File(filePath0);
		if (!file.exists()) {
			NettyHttpUtils.writeEmpty(ctx);
			return;
		}
		// 获取文件
		String htmlStr = FileUtils.loadFile(filePath0, "UTF-8");
		NettyHttpUtils.writeHtml(ctx, request, Unpooled.copiedBuffer(htmlStr, CharsetUtil.UTF_8), true);
		// Log.info("path=" + fpath + " " + url);
	}

	public String getRootPath() {
		return rootPath;
	}

	public String getPrefix() {
		return prefix;
	}

	/** 检测url前缀是否符合 **/
	public boolean checkPrefix(String url) {
		return url.indexOf(prefix) == 0;
	}
}

