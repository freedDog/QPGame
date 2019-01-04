package com.game.framework.framework.oplog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.game.framework.component.log.Log;
import com.game.framework.utils.TimeUtils;

/**
 * 日志写入器 LogWriter.java
 * 
 * @author JiangBangMing 2019年1月3日下午3:04:29
 */
public class LogWriter {
	/** 分割符 **/
	protected static final char SEP = 0x01;
	/** 文件分割格式 **/
	protected static final ThreadLocal<SimpleDateFormat> suffixFormat = new ThreadLocal<SimpleDateFormat>() {
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd.HH00");
		}
	};
	/** 日期输出格式 **/
	protected static final ThreadLocal<SimpleDateFormat> dateValueFormat = new ThreadLocal<SimpleDateFormat>() {
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		}
	};

	protected ILogType type; // 日志类型

	protected String folderPath; // 文件保存路径
	protected String logNamePrefix; // 文件名前缀

	protected long fileTime; // 文件创建时间
	protected PrintWriter writer; // 数据流写入器

	protected List<Object[]> logCache; // 缓存列表

	/**
	 * @param folderPath
	 *            文件保存路径
	 * @param logNamePrefix
	 *            文件名前缀
	 * @param type
	 *            日志类型
	 */
	protected LogWriter(String folderPath, String logNamePrefix, ILogType type) {
		this.folderPath = folderPath;
		this.logNamePrefix = logNamePrefix;
		this.type = type;
		logCache = new ArrayList<>();
	}

	/** 写入内容 **/
	public void write(Object... params) {
		// 整理写入参数
		int paramCount = params != null ? params.length : 0;
		for (int i = 0; i < paramCount; i++) {
			Object param = params[i];
			if (param == null) {
				continue; // 允许空
			} else if (param.equals("")) {
				params[i] = null; // 不许空字符, 直接null.
			} else if (param.getClass() == String.class) {
				params[i] = ((String) param).replaceAll("\n", "\\n"); // 字符串不许换行.
			}
		}
		// 写入数据
		synchronized (logCache) {
			logCache.add(params);
		}
		// System.err.println("日志: " + folderPath + " " +
		// Arrays.toString(params));
	}

	/** 检测切割 **/
	protected void check(long nowTime) {
		try {
			// 检测处理
			if (writer == null) {
				// 创建写入流
				this.fileTime = nowTime;
				createWriter();
				return; // 执行完毕
			}
			// 检测是否写入
			if (!isCut(nowTime)) {
				return; // 不需要切割
			}
			// 执行切割
			String tempFilePath = getTempFilePath(fileTime);
			File tempFile = new File(tempFilePath);
			File cutFile = new File(getCutFilePath(nowTime));
			if (cutFile.exists()) {
				Log.error("被切割文件已存在, 跳过切割, existCutFilePath : " + cutFile.getPath());
				this.fileTime = nowTime;
				return;
			}
			// 关闭写入, 把临时文件改名成切割文件名.
			writer.close();
			if (tempFile.exists()) {
				tempFile.renameTo(cutFile); // 改名
			} else {
				Log.error("日志切割异常, 找不到临时文件, path : " + tempFilePath);
			}
			// 创建新的临时文件
			this.fileTime = nowTime;
			createWriter();
			// Log.debug("文件切割完成, cutFile : " + cutFile.getPath());
		} catch (Exception e) {
			writer = null;
			Log.error("Check log writer catch an exception !", e);
		}
	}

	/** 日志字符串转化 **/
	protected boolean writeString(Object[] params, StringBuilder strBdr) {
		int psize = (params != null) ? params.length : 0;
		for (int i = 0; i < psize; i++) {
			Object param = params[i];
			if (i > 0) {
				strBdr.append(SEP);
			}
			// 内容输出
			if (param instanceof Date) {
				strBdr.append(dateValueFormat.get().format(param)); // 日期输出
				continue;
			}
			// 常规写入
			strBdr.append(param);
		}
		return true;
	}

	/** 把内容写入 **/
	protected void flushCache() {
		if (writer == null) {
			Log.error("Writer is null,can't record log," + logNamePrefix);
			return;
		}
		// 提取写入数据
		List<Object[]> logTemps = null;
		synchronized (logCache) {
			if (logCache.isEmpty()) {
				return;
			}
			logTemps = new ArrayList<>(logCache);
			logCache.clear();
		}
		// 遍历写入
		for (Object[] params : logTemps) {
			StringBuilder strBdr = new StringBuilder();
			if (!writeString(params, strBdr)) {
				continue; // 写入失败, 过滤掉了.
			}
			// 写入内容
			writer.println(strBdr.toString());
			// 检测写入是否成功
			if (writer.checkError()) {
				try {
					createWriter();
					writer.println(strBdr.toString());
					if (writer.checkError()) {
						Log.error("日志重建异常！data:" + strBdr.toString());
					}
				} catch (Exception e) {
					Log.error("日志写入异常！" + strBdr.toString(), e);
				}
			}
		}
		// 写入完成
		writer.flush();
	}

	/** 是否已经切割 */
	protected boolean isCut(long nowTime) {
		if (CutType.DAY == type.getCutType()) {
			return !TimeUtils.isSameDay(nowTime, fileTime);
		} else if (CutType.HOUR == type.getCutType()) {
			return !TimeUtils.isSameHour(nowTime, fileTime);
		}
		return false;
	}

	/** 创建写入器 **/
	protected void createWriter() throws Exception {
		// 创建文件
		String filePath = getTempFilePath(fileTime);
		File file = new File(filePath);
		file.getParentFile().mkdirs();
		file.createNewFile();
		// 创建写入器
		writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
	}

	/** 临时文件路径 **/
	protected String getTempFilePath(long fileTime) {
		return folderPath + File.separator + logNamePrefix;
	}

	/** 切割文件路径 **/
	protected String getCutFilePath(long fileTime) {
		return folderPath + File.separator + logNamePrefix + "." + suffixFormat.get().format(new Date(fileTime));
	}

	/** 切割类型 **/
	public enum CutType {
		/** 小时切割 **/
		HOUR, //
		/** 每日切割 **/
		DAY;
	}

	/** 日志类型 **/
	public interface ILogType {
		/** 日志名称 **/
		String getName();

		/** 切割类型, 默认是按小时切割. **/
		LogWriter.CutType getCutType();
	}

}
