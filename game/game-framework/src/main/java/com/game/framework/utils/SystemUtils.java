package com.game.framework.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 系统工具<br>
 * SystemUtils.java
 * @author JiangBangMing
 * 2019年1月3日下午3:51:20
 */
public class SystemUtils {
	/** 系统类型 **/
	public static final SystemPlatform system;
	/** 网卡地址储存 **/
	private static String mac = null;

	static {
		system = getSystemPlatform();
	}

	/** 设置系统路径, java 版本过低时对File不生效 **/
	@Deprecated
	public static void setSystemPath(String path) {
		System.setProperty("user.dir", path);
	}

	/** 获取系统程序 **/
	public static String getSystemPath() {
		try {
			// 通过文件路径处理
			File directory = new File("");
			String path = directory.getCanonicalPath();
			return path;
		} catch (IOException e) {
		}

		// 配置读取
		return System.getProperty("user.dir"); // user.dir指定了当前的路径
	}

	/** 获取本机内网Ip **/
	public static String getLocalIp() {
		if (system == SystemPlatform.WINDOWS) {
			// return LinuxSystemUtils.getLocalIp();
			return WindowsSystemUtils.getLocalIp();
		} else if (system == SystemPlatform.LINUX) {
			return LinuxSystemUtils.getLocalIp();
		}
		return "127.0.0.1";
	}

	/** 获取网卡mac(物理地址) **/
	public static String getMAC() {
		if (mac != null) {
			return mac;
		}

		// 同步, 避免多线程多次获取
		synchronized (SystemUtils.class) {
			// 阻塞了, 判断是否有其他线程已经获取到了.
			if (mac == null) {
				if (system == SystemPlatform.WINDOWS) {
					mac = WindowsSystemUtils.getMAC();
				}
			}
		}
		return mac;
	}

	/** 操作系统 **/
	public enum SystemPlatform {
		/** 未知系统 **/
		NONE, //
		WINDOWS, //
		LINUX, //
		MAC, ;

		@Override
		public String toString() {
			return super.name();
		}
	}

	/** 获取操作系统类型 **/
	private static SystemPlatform getSystemPlatform() {
		String osname = System.getProperty("os.name").toLowerCase(Locale.US);
		if (osname.indexOf("windows") >= 0) {
			return SystemPlatform.WINDOWS;
		} else if (osname.indexOf("linux") >= 0) {
			return SystemPlatform.LINUX;
		} else if (osname.indexOf("mac") >= 0 && osname.indexOf("os") > 0) {
			return SystemPlatform.MAC;
		}
		return SystemPlatform.NONE;
	}

	/** linux相关操作 **/
	private static class LinuxSystemUtils {

		/** 获取本机内网Ip **/
		public static String getLocalIp() {
			String ip = "127.0.0.1";
			try {
				for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					String name = intf.getName();
					if (!name.contains("docker") && !name.contains("lo")) {
						for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
							InetAddress inetAddress = enumIpAddr.nextElement();
							if (!inetAddress.isLoopbackAddress()) {
								String ipaddress = inetAddress.getHostAddress().toString();
								if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
									ip = ipaddress;
								}
							}
						}
					}
				}
			} catch (SocketException ex) {
				System.err.println("获取ip地址异常");
				ex.printStackTrace();
			}
			return ip;
		}
	}

	/** window相关操作 **/
	private static class WindowsSystemUtils {

		/** 获取本机内网Ip **/
		public static String getLocalIp() {
			try {
				InetAddress ia = InetAddress.getLocalHost();
				return ia.getHostAddress();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			return "127.0.0.1";
		}

		/** windows下 获取网卡mac(物理地址) **/
		public static String getMAC() {
			String mac = null;
			try {
				// 字段名称可能根据系统语言不同而变化
				final String[] keys = new String[] { "Physical Address", "物理地址" };
				final int keyCount = (keys != null) ? keys.length : 0;
				// 执行查询
				Process pro = Runtime.getRuntime().exec("cmd.exe /c ipconfig/all");
				InputStream is = pro.getInputStream();
				// 获取输出
				BufferedReader br = new BufferedReader(new InputStreamReader(is, "GB2312"));
				while (true) {
					String message = br.readLine();
					if (message == null) {
						break;
					}
					// 遍历查看是否是网卡物理地址字段
					for (int i = 0; i < keyCount; i++) {
						String key = keys[i];
						int index = message.indexOf(key);
						// 找到属性
						if (index > 0) {
							index = message.indexOf(": ");
							if (index > 0) {
								mac = message.substring(index + 1).trim();
								break;
							}
						}
					}
					// System.out.println(message);
					if (mac != null) {
						break;// 已经找到了
					}
				}
				// System.out.println(mac);
				br.close();
				pro.destroy();
			} catch (IOException e) {
				System.out.println("Can't get mac address!");
				return null;
			}
			return mac;
		}
	}

	/** main函数的string[] args 参数解析工具 **/
	public static class ArgUtils {

		protected static String findKey(String str) {
			int index = str.indexOf('=');
			if (index <= 0) {
				return str; // 全字段
			}
			String key = str.substring(0, index);
			return key;
		}

		protected static String findValue(String str) {
			int index = str.indexOf('=');
			if (index <= 0) {
				return ""; // 空
			}
			String key = str.substring(index + 1);
			return key;
		}

		/**
		 * 处理系统参数<br>
		 * 例子:<br>
		 * -out=asfd -in=124
		 * 
		 * @param args
		 * @param executor
		 */
		public static Map<String, String> systemArgs(String[] args) {
			int asize = (args != null) ? args.length : 0;
			Map<String, String> argMap = new HashMap<String, String>(asize);
			// 遍历解析
			for (int i = 0; i < asize; i++) {
				String arg = args[i];
				if (arg == null) {
					continue;
				}

				// 解析
				String key = findKey(arg);
				if (key == null || key.length() <= 0) {
					continue;
				}
				key = key.trim(); // 去除空格

				// 解析参数
				String value = findValue(arg);
				if (value == null || value.length() <= 0) {
					continue;
				}
				value = value.trim(); // 去除空格

				// 插入数据
				argMap.put(key, value);
			}

			return argMap;
		}

		/**
		 * 解析参数<br>
		 * 例子: a=2,b=3
		 * 
		 * @param args
		 * @return
		 */
		public static Map<String, String> getArgs(String args0) {
			final String key = ",";
			String[] args = args0.split(key);
			return systemArgs(args);
		}

		public static <T> T getArg(String value, T defualt) {
			if (value == null || value.length() <= 0) {
				return defualt;
			}
			// 读取基础值
			@SuppressWarnings("unchecked")
			T obj = (T) ObjectUtils.toValue(value, defualt.getClass());
			if (obj == null) {
				return defualt;
			}
			return obj;
		}

	}

	/** 指令工具 **/
	public static class CmdUtils {

		public static String findFirst(String cmd) {
			int index = cmd.indexOf(' ');
			if (index <= 0) {
				return cmd;
			}
			// 截取头部
			return cmd.substring(0, index);
		}

		/**
		 * 指令截取
		 * 
		 * @param cmd
		 * @return
		 */
		public static String[] cmdSplit(String cmd) {
			String[] args = cmd.split(" ");
			return args;
		}

		/**
		 * 从字符串读取整型
		 * 
		 * @param arg
		 * @return
		 */
		public static int intGet(String arg) {
			try {
				Integer i = Integer.valueOf(arg);
				return (i != null) ? i : 0;
			} catch (Exception e) {
			}
			return 0;
		}

	}

	/** 控制台程序输入控制 **/
	public static class InputUtils {

		/**
		 * 监听系统输入(阻塞sync)
		 * 
		 * @param listener
		 *            {@link IInputListener}
		 */
		public static void startSync(IInputListener listener) {
			// 设置输入流
			InputStreamReader in = new InputStreamReader(System.in);
			BufferedReader sin = new BufferedReader(in);
			try {
				do {
					ThreadUtils.sleep(300);
					// 检测输入
					String inputStr = sin.readLine();
					// 检测是否空
					if (inputStr == null || inputStr.length() <= 0) {
						continue;
					}
					// 过滤处理
					if (!listener.filter(inputStr)) {
						break;
					}
					// 执行处理
					if (!listener.input(inputStr)) {
						break; // 中断返回
					}
				} while (true);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * 监听系统输入(非阻塞asyn)
		 * 
		 * @param listener
		 *            {@link IInputListener}
		 */
		public static void startAsyn(final IInputListener listener) {
			Thread t = new Thread("system input") {
				@Override
				public void run() {
					startSync(listener);
				}
			};
			t.start();
		}

		/** 输入接口 **/
		public interface IInputListener {
			/**
			 * 处理输入字符串
			 * 
			 * @param inputStr
			 * @return 处理结果返回, true继续, false停止侦听
			 */
			public boolean input(String inputStr);

			/**
			 * 过滤输入字符串
			 * 
			 * @param inputStr
			 * @return 返回是否过滤,是则通过到input处理,否则无视.
			 */
			public boolean filter(String inputStr);
		}

	}

}
