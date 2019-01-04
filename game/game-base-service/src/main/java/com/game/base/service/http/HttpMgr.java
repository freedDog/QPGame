package com.game.base.service.http;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.framework.component.log.Log;
import com.game.framework.utils.ResourceUtils;

/**
 * http url 映射
 * HttpMgr.java
 * @author JiangBangMing
 * 2019年1月4日下午4:22:43
 */
public class HttpMgr {
	private static Map<String, IHttpHandler> urls = new HashMap<>(); // url绑定

	/**
	 * 注册url
	 * 
	 * @param url
	 * @param handler
	 */
	public static boolean registerUrl(String url, IHttpHandler handler) {
		// 判断是否存在
		if (urls.containsKey(url)) {
			Log.error("已存在相同url: " + url + " " + handler);
			return false;
		}
		urls.put(url, handler);
		return true;
	}

	/** 根据包名自动扫描 **/
	public static boolean register(String packet, IFilter filter) {
		String packetRegex = ResourceUtils.getPacketRegex(packet);
		List<Class<?>> clazzs = ResourceUtils.getClassesByClass(IHttpHandler.class, packetRegex);
		// int count = 0;
		for (Class<?> clazz : clazzs) {
			try {
				HttpUrl url = clazz.getAnnotation(HttpUrl.class);
				if (url == null) {
					Log.error("UrlHandler 没有对应注解，将无法被访问: class=" + clazz);
					continue;
				}

				// 过滤处理
				if (filter != null) {
					if (!filter.filter(url, clazz)) {
						continue;
					}
				}

				// 创建对象
				IHttpHandler handler = (IHttpHandler) clazz.newInstance();
				if (!registerUrl(url.value(), handler)) {
					return false;
				}
				// Log.debug("映射 URL [" + url.value() + "] " + handler.getClass().getCanonicalName());
				// count++;
			} catch (Exception e) {
				Log.error("创建url对象错误: class=" + clazz, e);
				return false;
			}
		}
		// Log.info("[" + packetName + "]完成映射 " + count + "/" + clazzs.size());

		return true;
	}

	/** 获取http句柄 **/
	public static IHttpHandler getHandler(String url) {
		return urls.get(url);
	}

	// /** 执行处理 **/
	// public static String execute(String url, Map<String, Object> params,
	// RpcCallback callback)
	// {
	// // 获取接口
	// HttpHandler handler = urls.get(url);
	// if (handler == null)
	// {
	// return null;
	// }
	//
	// // 执行
	// Object result = handler.execute(params, callback);
	// if (result == null || result.equals("")) // 这是个异步的处理
	// {
	// return null;
	// }
	// return HttpHandler.getJson(result);
	// }

	// protected static String getReqInfo(HttpRequest req, String remoteAddr,
	// Map<String, Object> params)
	// {
	// StringBuilder sb = new StringBuilder();
	// sb.append("remote address : ").append(remoteAddr).append(", ");
	// sb.append("URL : ").append(req.headers().get("host") +
	// req.getUri()).append(", ");
	// sb.append("Method : ").append(req.getMethod()).append(", ");
	// sb.append("params : ");
	// sb.append(JSON.toJSONString(params));
	// return sb.toString();
	// }

	/** 过滤器 **/
	public interface IFilter {
		boolean filter(HttpUrl ha, Class<?> clazz);
	}

	@Target({ ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	@Documented
	public @interface HttpUrl {
		String value();
	}

}

