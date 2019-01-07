package com.game.gate.http;

import java.util.List;

import com.game.base.service.http.HttpFilters;
import com.game.framework.component.log.Log;
import com.game.framework.framework.mgr.NettyServiceMgr;
import com.game.framework.framework.server.http.netty.NettyHttpServer;
import com.game.framework.framework.xml.XmlNode;
import com.game.framework.utils.StringUtils;

import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.HttpObject;

/**
 * http服务器
 * 
 */
public class HttpServer extends NettyHttpServer {
	protected HttpFilters filters = new HttpFilters();

	public boolean startSync(XmlNode config) {
		// 添加过滤
		List<XmlNode> filterNodes = config.getParent().getElems("HttpFilter");
		int fsize = (filterNodes != null) ? filterNodes.size() : 0;
		for (int i = 0; i < fsize; i++) {
			XmlNode filterNode = filterNodes.get(i);
			// 检测url
			String url = filterNode.getAttr("url", "");
			if (StringUtils.isEmpty(url)) {
				Log.error("错误过滤! " + filterNode);
				return false;
			}
			// 添加过滤
			String ip = filterNode.getAttr("ip", "*");
			if (!filters.setFilter(url, ip)) {
				Log.error("http过滤添加失败! " + filterNode);
				return false;
			}
		}

		try {
			// 启动配置
			int port = config.getAttr("httpport", 8080);
			return this.startSync(port);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public SimpleChannelInboundHandler<? extends HttpObject> createHttpHandler() {
		return new GateHttpHandler(this);
	}

	@Override
	protected void onStart(int port) {
		Log.info("Http服务启动完成, port=" + port);
	}

	@Override
	protected void onStop() {
	}

	@Override
	protected NioEventLoopGroup createParentGroup() {
		return NettyServiceMgr.getEventLoopGroup();
	}

	@Override
	protected NioEventLoopGroup createChildGroup() {
		return NettyServiceMgr.getEventLoopGroup();
	}

}
