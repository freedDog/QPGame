package com.game.gate.http;

import java.io.File;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.game.base.service.config.ConfigMgr;
import com.game.base.service.config.ServerConfig;
import com.game.base.service.constant.TextTempId;
import com.game.base.service.language.LanguageSet;
import com.game.base.service.mailbox.MailBox;
import com.game.base.service.module.ModuleName;
import com.game.base.service.player.Player;
import com.game.base.service.rpc.handler.IGameService;
import com.game.base.service.server.App;
import com.game.base.utils.DataUtils;
import com.game.entity.http.bean.HttpResult;
import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.RpcCallback;
import com.game.framework.framework.server.http.netty.NettyHttpUtils;
import com.game.framework.utils.ObjectUtils;
import com.game.framework.utils.StringUtils;
import com.game.gate.client.ClientMgr;
import com.game.gate.mgr.GateChannelMgr;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;

/**
 * http服务器接口 
 * GateHttpHandler.java
 * @author JiangBangMing
 * 2019年1月7日下午6:27:21
 */
public class GateHttpHandler extends SimpleChannelInboundHandler<HttpObject> {
	protected HttpServer httpServer;
	protected FileServlet fileServlet;

	public GateHttpHandler(HttpServer httpServer) {
		this.httpServer = httpServer;

		// 文件路径
		String filePath = ConfigMgr.getConfigRootPath() + File.separator + "/gm";
		this.fileServlet = new FileServlet(filePath, "gate/gm/");
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		if (!(msg instanceof HttpContent)) {
			NettyHttpUtils.writeError(ctx);
			return;
		}

		// 消息处理
		boolean result = false;
		try {
			result = messageReceived(ctx, msg);
		} catch (Exception e) {
			Log.error("http " + ctx.channel().remoteAddress(), e);
		}

		// 失败返回
		if (!result) {
			NettyHttpUtils.writeError(ctx);
			// 关闭数据(失败才关闭, 成功要等异步发送完毕再关闭)
			ctx.close();
		}

	}

	/** 网关url拦截处理, 返回true为拦截掉. **/
	protected boolean gateHttpExec(String url, ChannelHandlerContext ctx, FullHttpRequest request) {
		// 处理关闭处理
		if (url.equals("gate/stop")) {
			App.getInstance().stop(0);
			// writeJson(ctx, request, HttpResult.create(0, "ok"));
			NettyHttpUtils.writeHtml(ctx, request, Unpooled.copiedBuffer("ok", CharsetUtil.UTF_8), true);
			return true;
		} else if (fileServlet.checkPrefix(url)) {
			// 文件处理
			fileServlet.execute(url, ctx, request);
			return true;
		} else if (url.equals("favicon.ico")) {
			// 文件处理
			// fileServlet.executeByFilePath(url, ctx, request);
			return true;
		}
		return false;
	}

	/** 根据访问参数获取连接 **/
	protected ProxyChannel getHttpChannel(ModuleName moduleName, String url, Map<String, String> params) {
		// 通过http参数获取gameZoneId和PlayerId
		long playerId = DataUtils.toLong((String) params.get("playerId"));
		long userId = DataUtils.toLong((String) params.get("userId"));
		int gameZoneId = DataUtils.toInt((String) params.get("gameZoneId"));

		// 判断如果是json内容情况下, 解析数据
		String jsonStr = params.get("data");
		if (!StringUtils.isEmpty(jsonStr)) {
			@SuppressWarnings("unchecked")
			Map<String, Object> json = JSON.parseObject(jsonStr, Map.class);
			if (playerId == 0) {
				playerId = ObjectUtils.numberValue(json.get("playerId"));
			}
			if (userId == 0) {
				userId = ObjectUtils.numberValue(json.get("userId"));
			}
			if (gameZoneId == 0) {
				gameZoneId = (int) ObjectUtils.numberValue(json.get("gameZoneId"));
			}
		}

		// 如果实在获取不出gameZoneId, 从playerId上获取
		if (gameZoneId == 0) {
			gameZoneId = Player.getGameZoneId(playerId); // 通过玩家Id获取区服
			if (gameZoneId == 0) {
				gameZoneId = Player.getGameZoneId(userId); // 通过用户Id获取区服
			}
		}

		// 如果有playerId
		ServerConfig config = null;
		if (playerId > 0) {
			config = MailBox.get(playerId, moduleName);
		}

		// 通过区服分配, 区服Id可以为0.
		if (config == null) {
			gameZoneId = 0; // 随机分配
			config = ClientMgr.getServerConfigByGameZoneId(moduleName, gameZoneId);
		}

		// 判断配置
		if (config == null) {
			Log.error("无法确定转发区服! url=" + url + " params=" + params);
			return null;
		}

		// 获取对应连接
		ProxyChannel rpcChannel = GateChannelMgr.getGameClientMgr().getChannel(config);
		return rpcChannel;
	}

	/**
	 * 获取ip地址
	 * @param request
	 * @return
	 */
	public String getIpAddr(HttpObject msg) {  
		HttpRequest request = (HttpRequest) msg;
		HttpHeaders head = request.headers();
	    String ip = head.get("X-Forwarded-For");  
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	        ip = head.get("PRoxy-Client-IP");  
	    }  
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	        ip = head.get("WL-Proxy-Client-IP");  
	    }
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	    	ip = null;
	    }
	    return ip;  
	} 
	
	/** 接受消息 **/
	protected boolean messageReceived(final ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		// 消息处理
		final FullHttpRequest request = (FullHttpRequest) msg;
		// 获取访问路径
		String url = request.getUri();
		url = url.replaceFirst("^/", "");
		url = url.split("\\?")[0];
		if (StringUtils.isEmpty(url)) {
			return false;
		}
		// Log.debug("url访问: " + url);
		String ip = getIpAddr(msg);
		if(ip == null || ip.isEmpty()){
			// 过滤 检测
			SocketChannel channel = (SocketChannel) ctx.channel();
			ip = channel.remoteAddress().getAddress().getHostAddress();
		}
		if (!httpServer.filters.checkFilter(url, ip)) {
			// 返回数据
			String errStr = LanguageSet.get(TextTempId.ID_8, "=,.=! " + ip);
			writeJson(ctx, request, HttpResult.create(0, errStr));
			return true;
		}

		// 处理关闭处理
		if (gateHttpExec(url, ctx, request)) {
			return true;
		}

		// 非开发阶段禁用get, 由于页面API的访问, 这块再考虑吧.
		// if (!ConfigMgr.isDebug() && !fileServlet.checkPrefix(url)) {
		// HttpMethod method = request.getMethod();
		// if (HttpMethod.GET == method) {
		// // 返回数据
		// String errStr = LanguageSet.get("common_error_refuse", "noget");
		// writeJson(ctx, request, HttpResult.create(0, errStr));
		// return true;
		// }
		// }

		// 根据url获取对应模块
		ModuleName moduleName = ModuleName.getModuleByUrl(url);
		if (moduleName == null) {
			Log.error("没有找到url获取对应的模块! url=" + url);
			// 返回数据
			String errStr = LanguageSet.get(TextTempId.ID_8, "NofindModule");
			writeJson(ctx, request, HttpResult.create(0, errStr));
			return true;
		}

		// 获取参数
		Map<String, String> params = NettyHttpUtils.parse(request);
		if (params == null) {
			// 返回数据
			String errStr = LanguageSet.get(TextTempId.ID_7, "ParamError");
			writeJson(ctx, request, HttpResult.create(0, errStr));
			return false;
		}

		// 根据url模块和参数获取符合条件的连接.
		ProxyChannel rpcChannel = getHttpChannel(moduleName, url, params);
		if (rpcChannel == null) {
			Log.error("找不到可连接的服务器连接! moduleName=" + moduleName + " url=" + url + " params=" + params);
			// return false;
			// 返回数据
			// String errStr = LanguageSet.get(TextTempId.ID_8, "服务尚未启动");
			String errStr = LanguageSet.get(TextTempId.ID_17);
			writeJson(ctx, request, HttpResult.create(0, errStr));
			return true;
		}

		// Log.info("onHttpRequest: url=" + url + " params=" + params);
		params.put("connectIp", ip); // 写入ip地址

		// 执行调用
		IGameService gameService = rpcChannel.createImpl(IGameService.class);
		gameService.onHttpRequest(url, params, rpcChannel, new RpcCallback() {
			@SuppressWarnings("unused")
			protected void onCallBack(int code, String result) {
				// 返回结果
				NettyHttpUtils.writeHtml(ctx, request, Unpooled.copiedBuffer(result, CharsetUtil.UTF_8), true);
			}

			@Override
			protected void onTimeOut() {
				NettyHttpUtils.writeError(ctx);
			}
		});
		return true;
	}

	/** json返回 **/
	protected static void writeJson(ChannelHandlerContext ctx, HttpRequest request, Object retObj) {
		String retStr = JSON.toJSONString(retObj);
		NettyHttpUtils.writeHtml(ctx, request, Unpooled.copiedBuffer(retStr, CharsetUtil.UTF_8), true);
	}

	// 错误触发
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		Class<?> cclass = cause.getClass();
		if (cclass == io.netty.handler.timeout.ReadTimeoutException.class //
				|| cclass == java.io.IOException.class //
		) {
			Log.warn("http caught: " + cause.toString());
		} else {
			Log.error("http error:", cause);
		}
		// 错误关闭
		ctx.channel().close();
	}

}
