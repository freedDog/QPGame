package com.game.framework.framework.server.http.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import com.game.framework.utils.BufferUtils;



/**
 * java自带的http客户端连接<br>
 * HttpUtils.java
 * @author JiangBangMing
 * 2019年1月3日下午2:18:30
 */
public class HttpUtils {

	/**
	 * get执行
	 * 
	 * @param url
	 * @return
	 */
	public static byte[] get(String url) {
		HttpURLConnection connection = null;
		try {
			URL url0 = new URL(url);
			connection = (HttpURLConnection) url0.openConnection();
			setDefualtProperty(connection);
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(10 * 1000);
			connection.setReadTimeout(10 * 1000);
			// 执行
			return execute(connection, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			// 关闭连接
			if (connection != null) {
				connection.disconnect();
				connection = null;
			}
		}
	}

	/** get **/
	public static <T> byte[] get(String url, Map<String, T> params) {
		return get(url, params, "UTF-8");
	}

	/** get **/
	public static <T> byte[] get(String url, Map<String, T> params, String enc) {
		String url0 = url(url, params, enc);
		return get(url0);
	}

	/** 把url和参数整理成get的url */
	public static <T> String url(String url, Map<String, T> params) {
		return url(url, params, "UTF-8");
	}

	/** 把url和参数整理成get的url */
	public static <T> String url(String url, Map<String, T> params, String enc) {
		// 转成字符串
		String ps = urlencode(params, enc);
		return url + "?" + ps;
	}

	/** url解码<br> **/
	public static String urldecode(String s, String enc) {
		if (s == null) {
			return null;
		}
		try {
			return URLDecoder.decode(s, enc);
		} catch (UnsupportedEncodingException e) {
			new RuntimeException(e);
		}
		return null;
	}

	/** url编码<br> **/
	public static String urlencode(String s, String enc) {
		if (s == null) {
			return null;
		}
		try {
			return URLEncoder.encode(s, enc);
		} catch (UnsupportedEncodingException e) {
			new RuntimeException(e);
		}
		return null;
	}

	/**
	 * url编码<br>
	 * 例:a=1&b=2
	 */
	public static <T> String urlencode(Map<String, T> params, String enc) {
		// 空判断
		if (params == null) {
			return "";
		}
		StringBuilder strBdr = new StringBuilder();
		int index = 0;
		// 遍历转化
		Iterator<Map.Entry<String, T>> iter = params.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, T> entry = iter.next();
			String key = entry.getKey();
			T value = entry.getValue();
			if (value == null) {
				continue;
			}
			// 换行符
			if (index > 0) {
				strBdr.append("&");
			}
			index++;
			// 拼接字符串
			String keyStr = key;
			String valueStr = value.toString();
			if (enc != null) {
				try {
					keyStr = URLEncoder.encode(keyStr, enc);
					valueStr = URLEncoder.encode(valueStr, enc);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			// 添加参数
			strBdr.append(keyStr);
			strBdr.append("=");
			strBdr.append(valueStr);
		}

		// 转成字符串
		return strBdr.toString();
	}

	/** post **/
	public static <T> byte[] post(String url, Map<String, T> params) {
		return post(url, params, "UTF-8");
	}

	/** post **/
	public static <T> byte[] post(String url, Map<String, T> params, String enc) {
		String data = urlencode(params, enc);
		byte[] dataBytes = toBytes(data, enc);
		return post(url, dataBytes);
	}

	/** post **/
	public static <T> byte[] post(String url, byte[] data) {
		HttpURLConnection connection = null;
		try {
			URL url0 = new URL(url);
			connection = (HttpURLConnection) url0.openConnection();
			setDefualtProperty(connection);
			connection.setConnectTimeout(10 * 1000);
			connection.setReadTimeout(10 * 1000);
			// 参数设置
			connection.setRequestMethod("POST");
			// connection.setRequestProperty("Content-type", "application/x-java-serialized-object");
			// 执行
			return execute(connection, data);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			// 关闭连接
			if (connection != null) {
				connection.disconnect();
				connection = null;
			}
		}
	}

	/** execute执行访问 **/
	protected static <T> byte[] execute(HttpURLConnection connection, byte[] writeBuf) throws Exception {
		OutputStream os = null;
		InputStream is = null;
		try {
			// 设置写入内容
			int wsize = (writeBuf != null) ? writeBuf.length : 0;
			if (wsize > 0) {
				connection.setRequestProperty("Content-length", String.valueOf(wsize));
				connection.setDoInput(true);
			}
			connection.setDoOutput(true);
			// 连接
			connection.connect();

			// 写入内容
			if (wsize > 0) {
				os = connection.getOutputStream();
				os.write(writeBuf);
				os.flush();
			}

			// 读取内容
			is = connection.getInputStream();
			byte[] readBuf = BufferUtils.readBytes(is, 1024 * 1024, 1024);
			return readBuf;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (Exception e) {
			}
		}

	}

	/**
	 * 设置默认参数<br>
	 * 参考:<br>
	 * http://blog.csdn.net/nipanlong001/article/details/52086864<br>
	 * **/
	protected static void setDefualtProperty(HttpURLConnection connection) {
		// 设置主机和端口号
		connection.setRequestProperty("ACCEPT", "*/*");// 设置可以接受的媒体（头部）
		connection.setRequestProperty("X-TT-PDMODE", " 2 ");
		// 设置连接主机超时（单位：毫秒）
		connection.setConnectTimeout(10000);
		// 设置从主机读取数据超时（单位：毫秒）
		connection.setReadTimeout(10000);
		// 请求不能使用缓存
		connection.setUseCaches(false);
		connection.setRequestProperty("Accept-Charset", "utf-8");

	}

	/** 数据转字符串 **/
	public static String toString(byte[] data) {
		return toString(data, "UTF-8");
	}

	/** 数据转字符串 **/
	public static String toString(byte[] data, String enc) {
		try {
			return (data != null) ? new String(data, enc) : "";
		} catch (UnsupportedEncodingException e) {
			System.err.println("转码字符串错误!" + Arrays.toString(data) + " enc=" + enc);
			throw new RuntimeException(e);
		}
	}

	/** 提取数据流 **/
	public static byte[] toBytes(String str, String enc) {
		try {
			return str.getBytes(enc);
		} catch (Exception e) {
			System.err.println("编码数据错误!" + str + " enc=" + enc);
			throw new RuntimeException(e);
		}
	}

	// /** http客户端(用于重复http访问, 只支持post) **/
	// public static class HttpClient {
	// private String url;
	// private HttpURLConnection connection;
	// private int timeout; // 超时
	//
	// public HttpClient(String url) {
	// this.url = url;
	// }
	//
	// /** post **/
	// public <T> byte[] post(Map<String, T> params) {
	// String data = urlencode(params, "UTF-8");
	// return post(data.getBytes());
	// }
	//
	// /** post **/
	// public byte[] post(byte[] data) {
	// try {
	// HttpURLConnection connection = getConnection();
	// setDefualtProperty(connection);
	// connection.setConnectTimeout(timeout);
	// connection.setReadTimeout(timeout);
	// connection.setUseCaches(true);// 是否缓存true|false
	// // 参数设置
	// connection.setRequestMethod("POST");
	// connection.setRequestProperty("Connection", "Keep-Alive");
	// // connection.setRequestProperty("Content-type", "application/x-java-serialized-object");
	// // 执行
	// byte[] retBuf = execute(connection, data);
	// connection.disconnect();
	// return retBuf;
	// } catch (Exception e) {
	// throw new RuntimeException(e);
	// }
	// }
	//
	// /** 获取http连接 **/
	// private HttpURLConnection getConnection() throws Exception {
	// if (connection != null) {
	// return connection;
	// }
	// URL url0 = new URL(url);
	// connection = (HttpURLConnection) url0.openConnection();// 新建连接实例
	// return connection;
	// }
	//
	// public int getTimeout() {
	// return timeout;
	// }
	//
	// public void setTimeout(int timeout) {
	// this.timeout = timeout;
	// }
	//
	// }

}
