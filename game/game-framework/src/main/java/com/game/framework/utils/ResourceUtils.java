package com.game.framework.utils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 搜索查找所有资源文件
 * ResourceUtils.java
 * @author JiangBangMing
 * 2019年1月3日下午1:36:35
 */
public class ResourceUtils {

	/**
	 * 通过包路径获取包下类的正则表达式<br>
	 * 例子: com.changic.sg.core -> .+/com/changic/sg/core/.+.class<br>
	 * 
	 * **/
	public static String getPacketRegex(String packet) {
		String packetRegex = packet.replaceAll("\\.", "/");
		packetRegex = ".+/" + packetRegex + "/.+.class";
		return packetRegex;
	}

	/** 获取绝对路径 **/
	public static String getResouce(String path) {
		path = (path.charAt(0) != '/') ? "/" + path : path;
		URL url = ResourceUtils.class.getResource(path);
		return url.getPath();
	}

	/**
	 * 从所有资源中筛选出符合格式的资源.
	 * <p/>
	 * 例子1. 全部 : .+ or .*
	 * <p/>
	 * 例子2. 筛选包含/zyt/test/的文件 : .+/zyt/test/.+
	 * <p/>
	 * 例子3. 所有类 : .+.class or .+1.class
	 * <p/>
	 * 例子4. 所有DAO.class : (.*)[0-z]DAO.class 或者 .+DAO.class
	 * 
	 * @param regex
	 * @return
	 */
	public static List<URL> getResources(String regex) {
		regex = regex.replaceAll("\\\\", "/");
		// 整理路径
		try {
			List<URL> resources = new ArrayList<URL>();
			ClassLoader loader = ResourceUtils.class.getClassLoader();
			@SuppressWarnings("resource")
			URLClassLoader loader0 = (URLClassLoader) loader;
			getResourceByUrls(loader0.getURLs(), regex, resources);
			return resources;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 遍历各个url, 筛选出符合条件的路径. **/
	protected static boolean getResourceByUrls(URL[] urls, String regex, List<URL> out) throws Exception {
		int urlCount = (urls != null) ? urls.length : 0;
		for (int i = 0; i < urlCount; i++) {
			URL url = urls[i];
			String urlPath = url.getPath();
			// String type = url.getProtocol();
			boolean isjar = urlPath.matches(".+.jar");
			// classes文件夹
			if (!isjar) {
				// if (urlPath.endsWith("classes/") || urlPath.endsWith("bin/"))
				// {
				getResourceByFile(url, regex, out, 0);
				// }
				continue;
			}

			// String jarPath = urlPath + "!/" + name;
			getResourceByJar(url, regex, out);
		}

		return true;
	}

	/** 从jar包中筛选出符合条件的url **/
	protected static boolean getResourceByJar(URL url, String regex, List<URL> out) throws Exception {
		// System.out.println("find jar: " + url);
		File file = new File(url.toURI());
		JarFile jarFile = new JarFile(file);
		try {
			Enumeration<JarEntry> entrys = jarFile.entries();
			while (entrys.hasMoreElements()) {
				JarEntry jarEntry = entrys.nextElement();
				// 判断是否是文件
				if (jarEntry.isDirectory()) {
					continue; // 跳过
				}

				// 判断是否符合路径中
				String entryName = jarEntry.getName();
				String urlStr = "jar:file:" + url.getPath() + "!/" + entryName;
				if (matches(urlStr, regex)) {
					// String className = entryName.replace("/",
					// ".").substring(0, entryName.lastIndexOf("."));
					URL url0 = new URL(urlStr);
					out.add(url0);
					// System.out.println(entryName + "-" + urlStr);
				}

				// System.out.println(urlStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jarFile.close();
		}
		return true;
	}

	/** 在文件目录中筛选出符合条件的url **/
	protected static boolean getResourceByFile(URL url, String regex, List<URL> out, int level) throws Exception {
		URI uri = url.toURI();
		File file = new File(uri);
		// System.out.println(level + " find: " + url);

		// 是文件
		if (file.exists() && file.isFile()) {
			// 筛选
			if (matches(url, regex)) {
				URL url0 = file.toURI().toURL();
				out.add(url0);
			}
			return true;
		}

		// 获取路径子列表
		File[] childFiles = file.listFiles();
		if (childFiles == null) {
			return false; // 没有子目录
		}
		// 遍历子路径
		for (File childFile : childFiles) {
			// 是文件夹
			if (childFile.isDirectory()) {
				getResourceByFile(childFile.toURI().toURL(), regex, out, level + 1);
				continue;
			} else if (childFile.exists() && childFile.isFile()) {
				// 是文件
				if (matches(childFile.getAbsolutePath(), regex)) {
					URL url0 = childFile.toURI().toURL();
					out.add(url0);
				}
			}
		}

		return true;
	}

	protected static boolean matches(URL url, String regex) {
		String path = url.getPath();
		return matches(path, regex);
	}

	protected static boolean matches(String path, String regex) {
		if (regex.equals("*") || regex.equals("")) {
			return true;
		}
		path = path.replaceAll("\\\\", "/");
		return path.matches(regex);
	}

	public static Class<?> loadClass(URL url) throws ClassNotFoundException, URISyntaxException, IOException {
		String type = url.getProtocol();
		if (type.equals("file")) {
			return loadClassByFile(url);
		}
		return loadClassByJar(url);
	}

	/**
	 * 获取url对应的路径<br>
	 * 通过遍历当前引入的所有路径匹配.
	 * **/
	private static String getClassPath(URL url) {
		// 获取当前项目加载的所有url
		ClassLoader loader = ResourceUtils.class.getClassLoader();
		@SuppressWarnings("resource")
		URLClassLoader loader0 = (URLClassLoader) loader;
		URL[] urls = loader0.getURLs();

		// 当前url路径
		String path = url.getPath();
		path = path.replaceAll("\\\\", "/");
		// 遍历对比
		for (URL url0 : urls) {
			try {
				File file = new File(url0.toURI());
				String filePath = file.getPath();
				filePath = filePath.replaceAll("\\\\", "/");
				// 路径比对
				int r = path.indexOf(filePath);
				if (r >= 0) {
					return filePath;
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
				continue;
			}
		}
		return null;
	}

	/**
	 * 通过查找classes/类根目录
	 * 
	 * @param url
	 * @return
	 * @throws ClassNotFoundException
	 */
	protected static Class<?> loadClassByFile(URL url) throws ClassNotFoundException {
		// 获取与url路径对应的类路径
		String path0 = getClassPath(url);
		if (path0 == null) {
			return null; // 找不到类对应的路径
		}

		// 截取字符串
		String path = url.getPath();
		int l1 = path.lastIndexOf('.');
		int l0 = path.indexOf(path0) + path0.length() + 1;
		String className = path.substring(l0, l1); // 截取类名
		className = className.replaceAll("/", "."); // 把路径转成类名

		// // 加载类
		// try {
		// URL url0 = new URL("file:" + path0);
		// return loadClassByFile(url0, className);
		// } catch (MalformedURLException e) {
		// e.printStackTrace();
		// }
		// return null;

		// 直接用当前类加载, 因为出来的路径肯定是在当前引入路径中的, 所以直接这个也能加载出来的.
		ClassLoader loader = ResourceUtils.class.getClassLoader();
		return loader.loadClass(className);
	}

	/**
	 * 从文件中加载类(class文件)<br>
	 * 
	 * @param classUrl
	 *            类所在的根路径
	 * @param className
	 *            类名(包括包路径)
	 **/
	public static Class<?> loadClassByFile(URL classUrl, String className) throws ClassNotFoundException {
		// 根据jar文件创建加载器
		@SuppressWarnings("resource")
		ClassLoader loader = new URLClassLoader(new URL[] { classUrl });
		// 加载类
		Class<?> clazz = loader.loadClass(className);
		return clazz;
	}

	/**
	 * 根据url加载类
	 * <p/>
	 * URL url = new URL( "jar:file:/C:/Users/Administrator/.m2/repository/zyt/zyt-utils/1.0.1/zyt-utils-1.0.1.jar!/zyt/utils/TimeUtils.class" );
	 */
	protected static Class<?> loadClassByJar(URL url) throws IOException, ClassNotFoundException {
		// URL url = new
		// URL("jar:file:/C:/Users/Administrator/.m2/repository/zyt/zyt-utils/1.0.1/zyt-utils-1.0.1.jar!/zyt/utils/TimeUtils.class");

		JarURLConnection conn = (JarURLConnection) url.openConnection();
		// JarFile jarfile = conn.getJarFile();
		// JarEntry jarEntry = conn.getJarEntry();

		// 计算出类名
		String entryName = conn.getEntryName();
		String className = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));

		// // jar路径
		// URL url0 = conn.getJarFileURL();
		// Log.debug("loadClassByJar:" + " jar=" + url0 + " classname=" + className);
		// return loadClassByJar(url0, className);

		// 这种方法只提供筛选加载, 用原本的加载器即可
		ClassLoader loader = ResourceUtils.class.getClassLoader();
		return loader.loadClass(className);
	}

	/**
	 * 根据jar路径和类名加载外部类
	 * 
	 * @param jarUrl
	 * @param className
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Class<?> loadClassByJar(URL jarUrl, String className) throws IOException, ClassNotFoundException {
		// 根据jar文件创建加载器
		@SuppressWarnings("resource")
		ClassLoader loader = new URLClassLoader(new URL[] { jarUrl });
		// 加载类
		Class<?> clazz = loader.loadClass(className);
		return clazz;
	}

	/**
	 * 通过筛选获取继承与clazz的类<br>
	 * tsif.db 包下所有类: .+/tsif/db/.+.class <br>
	 * 所有包类: .+.class <br>
	 * 
	 * **/
	public static List<Class<?>> getClasses(String regex) {
		List<Class<?>> out = new ArrayList<>();
		// 找出当前所有类
		List<URL> urls = getResources(regex);
		for (URL url : urls) {
			Class<?> clazz0 = null;
			try {
				clazz0 = ResourceUtils.loadClass(url);
			} catch (Error e) {
				continue;
			} catch (Exception e) {
				continue;
			}
			if (clazz0 == null) {
				continue; // 加载失败
			}
			out.add(clazz0);
		}
		return out;
	}

	/**
	 * 通过筛选获取继承与clazz的类<br>
	 * tsif.db 包下所有类: .+/tsif/db/.+.class <br>
	 * 所有包类: .+.class <br>
	 * 
	 * **/
	public static List<Class<?>> getClassesByClass(Class<?> clazz, String regex) {
		List<Class<?>> out = new ArrayList<>();
		// 找出当前所有类
		List<URL> urls = getResources(regex);

		for (URL url : urls) {
			// System.out.println(url);
			Class<?> clazz0 = null;
			try {
				clazz0 = ResourceUtils.loadClass(url);
			} catch (Error e) {
				// e.printStackTrace();
				continue;
			} catch (Exception e) {
				// e.printStackTrace();
				continue;
			}
			if (clazz0 == null) {
				continue; // 加载失败
			}
			// System.out.println(clazz0 + " -> " + clazz);
			// 判断类是否继承, 相同不要.
			if (clazz == clazz0 || !clazz.isAssignableFrom(clazz0)) {
				continue;
			}
			out.add(clazz0);
		}
		return out;
	}

	/**
	 * 根据标签筛选类<br>
	 * tsif.db 包下所有类: .+/tsif/db/.+.class <br>
	 * 所有包类: .+.class <br>
	 * **/
	public static List<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotationClass, String regex) {
		List<Class<?>> out = new ArrayList<>();
		// 找出当前所有类
		List<URL> urls = getResources(regex);
		for (URL url : urls) {
			// System.out.println(url);
			Class<?> clazz0 = null;
			try {
				clazz0 = ResourceUtils.loadClass(url);
			} catch (Error e) {
				// e.printStackTrace();
				continue;
			} catch (Exception e) {
				// e.printStackTrace();
				continue;
			}
			if (clazz0 == null) {
				continue; // 加载失败
			}
			// System.out.println(clazz0 + " -> " + clazz);
			// 判断是否带有这个标签
			Annotation annotation = clazz0.getAnnotation(annotationClass);
			if (annotation == null) {
				continue;
			}
			out.add(clazz0);
		}
		return out;
	}

}
