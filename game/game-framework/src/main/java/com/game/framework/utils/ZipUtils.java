package com.game.framework.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *  压缩工具
 * ZipUtils.java
 * @author JiangBangMing
 * 2019年1月8日下午2:05:54
 */
public class ZipUtils {

	/** 用base64处理并zip解压 **/
	public static byte[] unzipAndBase64(String src) {
		byte[] data = EncryptUtils.Base64.decode(src);
		return (data != null) ? unzip(data) : null;
	}

	/** zip压缩并用base64处理 **/
	public static String zipAndBase64(byte[] src) {
		byte[] dsc = zip(src);
		return (dsc != null) ? EncryptUtils.Base64.encode(dsc) : null;
	}

	/** 用base64处理并gzip解压 **/
	public static byte[] ungzipAndBase64(String src) {
		byte[] data = EncryptUtils.Base64.decode(src);
		return (data != null) ? ungzip(data) : null;
	}

	/** gzip压缩并用base64处理 **/
	public static String gzipAndBase64(byte[] src) {
		byte[] dsc = gzip(src);
		return (dsc != null) ? EncryptUtils.Base64.encode(dsc) : null;
	}

	/** gzip解压 **/
	public static byte[] ungzip(byte[] compressed) {
		// 检测数据
		if (compressed == null) {
			return null;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(compressed);
		GZIPInputStream gzip = null;
		try {
			gzip = new GZIPInputStream(in);
			// 遍历读取
			byte[] data = new byte[1024];
			while (true) {
				// 读取数据
				int gsize = gzip.read(data);
				if (gsize <= 0) {
					break;
				}
				// 写入数据
				out.write(data, 0, gsize);
			}

			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (gzip != null) {
				try {
					gzip.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/** 使用gzip进行压缩 */
	public static byte[] gzip(byte[] src) {
		// 检测数据
		if (src == null) {
			return null;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = null;
		try {
			gzip = new GZIPOutputStream(out);
			gzip.write(src);
			gzip.finish();
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (gzip != null) {
				try {
					gzip.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/** 使用zip进行压缩 */
	public static final byte[] zip(byte[] src) {
		// 检测数据源
		if (src == null) {
			return null;
		}
		// 准备执行压缩
		ByteArrayOutputStream out = null;
		ZipOutputStream zout = null;
		try {
			out = new ByteArrayOutputStream();
			zout = new ZipOutputStream(out);
			zout.putNextEntry(new ZipEntry("0"));
			zout.write(src);
			zout.closeEntry();
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (zout != null) {
				try {
					zout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/** 使用zip进行解压缩 */
	public static final byte[] unzip(byte[] compressed) {
		// 检测源数据
		if (compressed == null) {
			return null;
		}

		ByteArrayOutputStream out = null;
		ByteArrayInputStream in = null;
		ZipInputStream zin = null;
		try {
			out = new ByteArrayOutputStream();
			in = new ByteArrayInputStream(compressed);
			zin = new ZipInputStream(in);
			zin.getNextEntry();
			byte[] buffer = new byte[1024];
			int offset = -1;
			while ((offset = zin.read(buffer)) != -1) {
				out.write(buffer, 0, offset);
			}
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (zin != null) {
				try {
					zin.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
