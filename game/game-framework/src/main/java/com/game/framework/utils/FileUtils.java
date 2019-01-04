package com.game.framework.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;

import com.game.framework.utils.SystemUtils.SystemPlatform;


/**
 * 文件工具
 * <p/>
 * 
 * @date 2015.06.06
 * 
 * @version 1.2
 */
public class FileUtils {

	/** 获取绝对路径(整理过的绝对路径) **/
	public static String getCanonicalPath(String path) {
		File file = new File(path);
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
		}
		return null;
	}

	/** 获取绝对路径 **/
	public static String getAbsolutePath(String path) {
		File file = new File(path);
		return file.getAbsolutePath();
	}

	/** 获取系统当前路径 **/
	public static String getCurrentPath() {
		File file = new File("");
		return file.getAbsolutePath();
	}

	/**
	 * 对象保存函数
	 * 
	 * @param object
	 * @param filePath
	 * @return
	 */
	public static boolean saveObjectToFile(Object object, String filePath) {
		// 文件保存路径
		filePath = filePath.replaceAll("\\\\", "/");// 完整路径
		// 保存对象到文件
		FileOutputStream outputStream = null;
		try {
			// 创建输出流
			outputStream = new FileOutputStream(filePath);
			// 创建对象输出流
			ObjectOutputStream objectOutput = null;
			objectOutput = new ObjectOutputStream(outputStream);
			// 写入对象
			objectOutput.writeObject(object); // 保存
			// 关闭数据流保存数据
			objectOutput.close();
			outputStream.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 读取对象
	 * 
	 * @param filePath
	 * @return
	 */
	public static Object loadObjectFromFile(String filePath) {
		filePath = filePath.replaceAll("\\\\", "/");// 完整路径
		// 从文件读取对象
		Object object = null;
		try {
			FileInputStream inputStream = null;
			inputStream = new FileInputStream(filePath);
			object = loadObjectFromStream(inputStream);
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return object;
	}

	@SuppressWarnings("unchecked")
	public static <T> T loadObjectFromFile(Class<T> clazz, String filePath) {
		// 加载文件
		Object obj = loadObjectFromFile(filePath);
		if (obj == null) {
			return null;
		}
		return (T) obj;
	}

	/**
	 * 从输入流中读取对象
	 * 
	 * @param inputStream
	 * @return
	 */
	public static Object loadObjectFromStream(InputStream inputStream) {
		// 从流读取对象
		Object object = null;
		try {
			// 创建对象输入流
			ObjectInputStream objectInput = null;
			objectInput = new ObjectInputStream(inputStream);
			// 读取对象
			object = objectInput.readObject();
			// 关闭输入流
			objectInput.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}

	/**
	 * 保存成文件(文本)
	 * 
	 * @param path
	 * @param data
	 * @param charsetName
	 * @param append
	 * @return
	 */
	public static boolean saveFile(String path, String data, String charsetName, boolean append) {
		path = path.replaceAll("\\\\", "/"); // 过滤字符

		try {
			// data = new String(data.getBytes("UTF-8"), "UTF-8");
			// 创建文件
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile(); // 创建新文件
			}
			// 写入文件
			FileOutputStream fos = new FileOutputStream(file, append);
			OutputStreamWriter writer = new OutputStreamWriter(fos, charsetName);
			// 写入内容
			writer.write(data);
			// 关闭输入流
			writer.close();
			fos.close();

			// System.out.println("output:" + file.getAbsolutePath());
		} catch (IOException e) {
			System.err.println("文件创建失败: path=" + path);
			return false;
		}
		return true;
	}

	/**
	 * 加载文件(文本)
	 * 
	 * @param filePath
	 * @param charsetName
	 * @return
	 */
	public static String loadFile(String filePath, String charsetName) {
		// 判断路径
		File file = new File(filePath);
		if (file == null || !file.exists()) {
			System.err.println("无法打开文件: file=" + filePath + " absolutePath=" + file.getAbsolutePath());
			return null;
		}

		InputStream in = null;
		try {
			// in = new FileInputStream(filePath);
			// in = FileUtils.class.getResourceAsStream(filePath);
			in = new FileInputStream(file);
			return loadFile(in, charsetName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				in = null;
			}
		}
		return null;
	}

	/**
	 * 加载文件(文本)
	 * 
	 * @param InputStream
	 *            输入流
	 * @param charsetName
	 * @return
	 */
	public static String loadFile(InputStream in, String charsetName) {
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(in, charsetName);

			// 遍历读取
			StringBuilder strBdr = new StringBuilder();
			do {
				char[] buffer = new char[1024];
				int size = reader.read(buffer);
				if (size <= 0) {
					break;
				}
				strBdr.append(buffer, 0, size);
			} while (true);
			return strBdr.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				reader = null;
			}
		}
		return null;
	}

	/**
	 * 加载文件
	 * 
	 * @param filePath
	 * @return
	 */
	public static byte[] loadFile(String filePath) {
		FileInputStream in = null;
		try {
			in = new FileInputStream(filePath);
			int size = in.available();
			byte[] buffer = new byte[size];
			in.read(buffer);
			in.close();
			return buffer;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 保存二进制文件
	 * 
	 * @param filePath
	 * @param buffer
	 * @param offset
	 * @param size
	 * @return
	 */
	public static boolean saveFile(String filePath, byte[] buffer, int offset, int size) {
		filePath = filePath.replaceAll("\\\\", "/"); // 过滤字符
		try {
			// 创建文件
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile(); // 创建新文件
			}
			// 写入文件
			FileOutputStream fos = new FileOutputStream(file);
			// 写入内容
			fos.write(buffer, offset, size);
			// 关闭输入流
			fos.close();
		} catch (IOException e) {
			System.err.println("文件创建失败:" + filePath);
			return false;
		}
		return true;
	}

	/**
	 * 保存二进制文件
	 * 
	 * @param filePath
	 * @param buffer
	 * @return
	 */
	public static boolean saveFile(String filePath, byte[] buffer) {
		return saveFile(filePath, buffer, 0, buffer.length);
	}

	/**
	 * 打开文件(支持文件夹或者文本)
	 * 
	 * @param path
	 */
	public static void openFile(String path) {
		if (SystemUtils.system != SystemPlatform.WINDOWS) {
			return;
		}

		try {
			// 整理路径
			int index = path.indexOf(":");
			if (index <= 0) {
				File file = new File(path);
				if (file.exists()) {

				}
				path = file.getAbsolutePath();
				path = path.replaceAll("\\\\", "/");
			}
			// 打开文件夹指令
			String[] cmd = new String[5];
			cmd[0] = "cmd";
			cmd[1] = "/c";
			cmd[2] = "start";
			cmd[3] = " ";
			cmd[4] = path;
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取路径中的文件名(包括文件后缀)
	 * 
	 * @param path
	 * @return
	 */
	public static String getFileName(String path) {
		path = path.replaceAll("\\\\", "/");
		int index = path.lastIndexOf('/');
		if (index <= 0) {
			return path;
		}

		String file = path.substring(index + 1);
		return file;
	}

	/** 读取路径中的文件名(不包括文件后缀) */
	public static String getFileNameBody(String path) {
		String filename = getFileName(path);
		int index = filename.lastIndexOf('.');
		if (index <= 0) {
			return filename;
		}
		// 截取出名字
		filename = filename.substring(0, index);
		return filename;
	}

	/** 是否是绝对路径 **/
	public static boolean isAbsolutePath(String path) {
		// if (path.startsWith("/") || path.indexOf(":") > 0) {
		// return true;
		// }

		// 通过file判断
		File file = new File(path);
		return file.isAbsolute(); // 检查此抽象路径名是否是绝对的。
	}

	/** 读取文件类型 */
	public static String getFileType(String path) {
		int index = path.lastIndexOf('.');
		if (index <= 0) {
			return "";
		}
		String file = path.substring(index + 1);
		return file;
	}

	/**
	 * 读取路径中的路径
	 * 
	 * @param path
	 * @return
	 */
	public static String getFilePath(String path) {
		path = path.replaceAll("\\\\", "/");
		int index = path.lastIndexOf('/');
		if (index <= 0) {
			return ".";
		}

		String file = path.substring(0, index + 1);
		return file;
	}

	/** 检测并且创建文件夹(可支持多层次结构, 自动过滤文件名) */
	public static boolean checkAndCreateFolder(String path) {
		// 提取出路径
		String filePath = getFilePath(path);
		// 创建File
		File file = new File(filePath);
		boolean isExists = file.exists(); // 是否存在
		boolean isDirectory = file.isDirectory(); // 是否是文件夹, 如果不存在也不算是
		if (!isExists && !isDirectory) {
			// 不存在
			try {
				file.mkdirs();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		return true;
	}

	// public static boolean createPath(String path) {
	// // 格式化路径
	// path = path.replaceAll("\\\\", "/");
	// // 根据斜杠拆分路径(最后的文件夹路径必须带个/)
	// String[] paths = path.split("/");
	// int pathCount = paths.length;
	// // 遍历各个路径
	// StringBuffer fullPath = new StringBuffer();
	// for (int i = 0; i < pathCount; i++) {
	// // 组装路径
	// fullPath.append(paths[i]).append("/");
	// // 创建file
	// File file = new File(fullPath.toString());
	// if (i >= pathCount - 1) {
	// break;
	// }
	// // System.out.println(pathCount + " " + i);
	// // 如果不存在则创建
	// if (!file.exists()) {
	// file.mkdir();
	// System.out.println("创建目录为：" + fullPath.toString());
	// // 留点时间给系统创建
	// try {
	// Thread.sleep(1500);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// // 最后验证一次
	// File file = new File(fullPath.toString());// 目录全路径
	// if (!file.exists()) {
	// return true;
	// } else {
	// return false;
	// }
	// }

	/**
	 * 文件遍历
	 * 
	 * @param inputPath
	 * @param fileType
	 * @param executor
	 * @return
	 */
	public static int actionFolder(String inputPath, String fileType, IFileExecutor executor) {
		File file = new File(inputPath);
		return actionFolder0(file, fileType, executor, 0);
	}

	// 文件遍历
	protected static int actionFolder0(File file, String fileType, IFileExecutor executor, int tier) {
		if (file == null) {
			return 0;
		}

		// 不是文件夹(是文件)
		if (!file.isDirectory()) {
			// 文件处理
			String fileName = file.getName();

			// 判断文件类型是否相符
			if (!fileType.equals("*")) {
				// 读取文件类型
				int index = fileName.lastIndexOf('.');
				if (index <= 0) {
					return 0; // 没有找到后缀, index<0 或者 index==0都不算
				}

				// 检测後綴是否完全相等
				String checkType = fileName.substring(index + 1);
				if (!fileType.equals(checkType)) {
					return 0; // 不完全相等
				}
			}

			boolean result = executor.onFile(file, fileName);
			return (result) ? 1 : 0;
		}

		// 文件夹处理
		if (tier > 0 && !executor.onFolder(file, file.getName())) {
			return 0;
		}

		// 遍历子文件
		int totalCount = 0;
		File childs[] = file.listFiles();
		int childCount = (childs != null) ? childs.length : 0;
		for (int i = 0; i < childCount; i++) {
			File child = childs[i];
			int count = actionFolder0(child, fileType, executor, tier + 1);
			if (count <= 0) {
				continue;
			}
			totalCount += count;
		}

		return totalCount;
	}

	/** 文件遍历接口 **/
	public interface IFileExecutor {
		public boolean onFile(File file, String name);

		public boolean onFolder(File file, String name);
	}

}