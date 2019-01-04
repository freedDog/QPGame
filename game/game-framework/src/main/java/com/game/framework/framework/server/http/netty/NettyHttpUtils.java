package com.game.framework.framework.server.http.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;

public class NettyHttpUtils {
	/**
	 * 默认错误返回并断开
	 * 
	 * @param ctx
	 */
	public static void writeError(ChannelHandlerContext ctx) {
		writeError(ctx, HttpResponseStatus.NOT_FOUND, true);
	}

	/**
	 * 写入错误消息,并且断开
	 * 
	 * @param ctx
	 * @param status
	 */
	public static void writeError(ChannelHandlerContext context, HttpResponseStatus status, boolean close) {
		HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
		httpResponse.headers().set(HttpHeaders.Names.CONTENT_TYPE, MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType("application/octet-stream"));
		// 写入数据
		HttpHeaders.setContentLength(httpResponse, 0);
		context.channel().write(httpResponse);
		ChannelFuture lastContentFuture = context.channel().writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
		// 执行关闭事件
		if (close) {
			lastContentFuture.addListener(ChannelFutureListener.CLOSE);
			// ctx.close();
			// close(context);
		}
	}

	public static boolean writeEmpty(ChannelHandlerContext ctx) {
		if (ctx == null) {
			// System.err.println("no http context");
			return false;
		}

		// 消息回调
		HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		String contentType = "text/html; charset=utf-8";
		// httpResponse.headers().set(HttpHeaders.Names.CONTENT_TYPE,
		// MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType("application/octet-stream"));
		httpResponse.headers().set(HttpHeaders.Names.CONTENT_TYPE, MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(contentType));
		// 检测是否需要保持连接
		boolean keepAlive = false;
		if (keepAlive) {
			httpResponse.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		}
		// 写入消息长度
		int size = 0;
		HttpHeaders.setContentLength(httpResponse, size);
		// 写入消息数据
		ctx.channel().write(httpResponse);
		// 发送数据
		ChannelFuture lastContentFuture = ctx.channel().writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
		// 根据请求情况确定是否继续保持连接
		if (!keepAlive) {
			lastContentFuture.addListener(ChannelFutureListener.CLOSE);
		}
		return true;
	}

	/**
	 * 写入消息
	 * 
	 * @param ctx
	 * @param byteBuf
	 */
	public static void write(ChannelHandlerContext ctx, HttpRequest request, ByteBuf byteBuf, boolean close) {
		HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		httpResponse.headers().set(HttpHeaders.Names.CONTENT_TYPE, MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType("application/octet-stream"));
		// 检测是否需要保持连接
		boolean keepAlive = (request != null) ? HttpHeaders.isKeepAlive(request) : false;
		if (keepAlive) {
			httpResponse.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		}
		// 写入消息长度
		int size = byteBuf.writerIndex();
		HttpHeaders.setContentLength(httpResponse, size);
		// 写入消息数据
		ctx.channel().write(httpResponse);
		ctx.channel().write(byteBuf);
		// 发送数据
		ChannelFuture lastContentFuture = ctx.channel().writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
		// 根据请求情况确定是否继续保持连接
		if (!keepAlive || close) {
			lastContentFuture.addListener(ChannelFutureListener.CLOSE);
		}
	}

	public static void write(ChannelHandlerContext ctx, HttpRequest request, byte[] buffer, boolean close) {
		int size = buffer.length;
		ByteBuf byteBuf = Unpooled.buffer(size);
		byteBuf.writerIndex(0); // 归位
		byteBuf.writeBytes(buffer);

		// 写入数据
		write(ctx, request, byteBuf, close);
	}

	public static void writeHtml(ChannelHandlerContext ctx, HttpRequest request, byte[] buffer, boolean close) {
		int size = buffer.length;
		ByteBuf byteBuf = Unpooled.buffer(size);
		byteBuf.writerIndex(0); // 归位
		byteBuf.writeBytes(buffer);

		// 写入数据
		writeHtml(ctx, request, byteBuf, close);
	}

	public static void writeHtml(ChannelHandlerContext ctx, HttpRequest request, ByteBuf byteBuf, boolean close) {
		HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		// String contentType =
		// MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType("text/html");
		String contentType = "text/html; charset=utf-8";
		httpResponse.headers().set(HttpHeaders.Names.CONTENT_TYPE, contentType);
		// 检测是否需要保持连接
		boolean keepAlive = false;
		if (!close) {
			keepAlive = (request != null) ? HttpHeaders.isKeepAlive(request) : false;
			if (keepAlive) {
				httpResponse.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
			}
		}
		// 写入消息长度
		int size = byteBuf.writerIndex();
		HttpHeaders.setContentLength(httpResponse, size);
		// 写入消息数据
		ctx.channel().write(httpResponse);
		ctx.channel().write(byteBuf);
		// 发送数据
		ChannelFuture lastContentFuture = ctx.channel().writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
		// 根据请求情况确定是否继续保持连接
		if (close || !keepAlive) {
			lastContentFuture.addListener(ChannelFutureListener.CLOSE);
		}
	}

	/** 解析参数(支持post和get) **/
	public static Map<String, String> parse(FullHttpRequest request) throws Exception {
		Map<String, String> parmMap = new HashMap<>();

		HttpMethod method = request.getMethod();
		if (HttpMethod.GET == method) {
			// 是GET请求
			QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
			for (Map.Entry<String, List<String>> entry : decoder.parameters().entrySet()) {
				parmMap.put(entry.getKey(), entry.getValue().get(0));
			}
		} else if (HttpMethod.POST == method) {
			// 是POST请求
			HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
			decoder.offer(request);

			// 遍历解析参数
			List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
			for (InterfaceHttpData parm : parmList) {
				Attribute data = (Attribute) parm;
				parmMap.put(data.getName(), data.getValue());
			}
		} else {
			// 不支持其它方法
			throw new Exception("未知调用方式: " + method);
		}

		return parmMap;
	}

}
