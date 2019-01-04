package com.game.framework.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 数据流工具
 * BufferUtils.java
 * @author JiangBangMing
 * 2019年1月3日下午2:19:02
 */
public class BufferUtils {

	/** 获取校验码(同样的数据应该是一样的校验码) **/
	public static int getCheckDigit(byte[] buffer, int offset, int size) {
		int value = 0;
		// 遍历计算
		for (int i = 0; i < size; i++) {
			byte b = buffer[i + offset];
			// value = (value + b) % 98215125;
			// value ^= b;
			value ^= b % 5;
		}

		return value;
	}

	/**
	 * 创建并复制数组<br>
	 * System.arraycopy(buffer, intSize, datas, 0, datas.length);
	 */
	public static byte[] copy(byte[] buffer, int offset, int size) {
		byte[] outs = new byte[size];
		System.arraycopy(buffer, offset, outs, 0, size);
		return outs;
	}

	/** 数据截取 */
	public static byte[] subBytes(byte[] src, int offset, int length) {
		byte[] buf = new byte[length];
		System.arraycopy(src, offset, buf, 0, length);
		return buf;
	}

	/** 对象转数组 */
	public static byte[] objectToBytes(Object obj) {
		byte[] bytes = null;
		try {
			// 创建二进制输出流
			ByteArrayOutputStream outputStream = null;
			outputStream = new ByteArrayOutputStream();
			// 创建对象输出流
			ObjectOutputStream objectOutputStream = null;
			objectOutputStream = new ObjectOutputStream(outputStream);
			// 写入对象
			objectOutputStream.writeObject(obj);
			objectOutputStream.flush();
			// 读取出二进制数据
			bytes = outputStream.toByteArray();
			// 关闭输出流
			objectOutputStream.close();
			outputStream.close();
		} catch (IOException exc) {
			exc.printStackTrace();
		}
		return bytes;
	}

	/** 数组转对象 */
	public static Object bytesToObject(byte[] bytes) {
		Object obj = null;
		try {
			// 创建二进制输入流
			ByteArrayInputStream inputStream = null;
			inputStream = new ByteArrayInputStream(bytes);
			// 创建对象输入流
			ObjectInputStream objectInputStream = null;
			objectInputStream = new ObjectInputStream(inputStream);
			// 读取二进制对象
			obj = objectInputStream.readObject();
			// 关闭输入流
			objectInputStream.close();
			inputStream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return obj;
	}

	@SuppressWarnings("unchecked")
	public static <T> T bytesToObject(Class<T> clazz, byte[] bytes) {
		Object obj = bytesToObject(bytes);
		if (obj == null) {
			return null;
		}
		return (T) obj;
	}

	public static final int longSize = 8;

	/** 正序编码 **/
	public static int longToBytes(long value, byte[] buffer, int offset) {
		buffer[0 + offset] = (byte) ((value >> 0) & 0xff);
		buffer[1 + offset] = (byte) ((value >> 8) & 0xff);
		buffer[2 + offset] = (byte) ((value >> 16) & 0xff);
		buffer[3 + offset] = (byte) ((value >> 24) & 0xff);
		buffer[4 + offset] = (byte) ((value >> 32) & 0xff);
		buffer[5 + offset] = (byte) ((value >> 40) & 0xff);
		buffer[6 + offset] = (byte) ((value >> 48) & 0xff);
		buffer[7 + offset] = (byte) ((value >> 56) & 0xff);

		// for (int i = 0; i < intSize; i++) {
		// buffer[i + offset] = (byte) ((value >> (8 * i)) & 0xFF);
		// }
		return longSize;
	}

	/** long 转数据流 **/
	public static byte[] longToBytes(long value) {
		byte[] buffer = new byte[Long.SIZE / Byte.SIZE];
		longToBytes(value, buffer, 0);
		return buffer;
	}

	/** 正序解码 **/
	public static long bytesToLong(byte[] buffer, int offset) {
		long value = 0;
		value = value + ((long) (buffer[0 + offset] & 0xFF) << 0);
		value = value + ((long) (buffer[1 + offset] & 0xFF) << 8);
		value = value + ((long) (buffer[2 + offset] & 0xFF) << 16);
		value = value + ((long) (buffer[3 + offset] & 0xFF) << 24);
		value = value + ((long) (buffer[4 + offset] & 0xFF) << 32);
		value = value + ((long) (buffer[5 + offset] & 0xFF) << 40);
		value = value + ((long) (buffer[6 + offset] & 0xFF) << 48);
		value = value + ((long) (buffer[7 + offset] & 0xFF) << 56);

		// int value = 0;
		// for (int i = 0; i < intSize; i++) {
		// value += (buffer[i + offset] & 0xFF) << (8 * i);
		// }
		return value;
	}

	/** 反序解码 **/
	public static long bytesToLong0(byte[] buffer, int offset) {
		long value = 0;
		value = value + ((long) (buffer[0 + offset] & 0xFF) << 0);
		value = value + ((long) (buffer[1 + offset] & 0xFF) << 8);
		value = value + ((long) (buffer[2 + offset] & 0xFF) << 16);
		value = value + ((long) (buffer[3 + offset] & 0xFF) << 24);
		value = value + ((long) (buffer[4 + offset] & 0xFF) << 32);
		value = value + ((long) (buffer[5 + offset] & 0xFF) << 40);
		value = value + ((long) (buffer[6 + offset] & 0xFF) << 48);
		value = value + ((long) (buffer[7 + offset] & 0xFF) << 56);

		// int value = 0;
		// for (int i = 0; i < intSize; i++) {
		// value += (buffer[i + offset] & 0xFF) << (8 * i);
		// }
		return value;
	}

	public static final int intSize = 4;

	/**
	 * 整形转byte数组
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] intToBytes(int value) {
		byte[] bytes = new byte[intSize];
		intToBytes(value, bytes, 0);
		return bytes;
	}

	/** 不适用超过int 4位的long **/
	public static int numberToBytes(long value, int size, byte[] buffer, int offset) {
		for (int i = 0; i < size; i++) {
			buffer[i + offset] = (byte) ((value >> (i * 8)) & 0xff);
		}
		return size;
	}

	public static int numberToBytes0(long value, int size, byte[] buffer, int offset) {
		for (int i = 0; i < size; i++) {
			buffer[i + offset] = (byte) ((value >> ((size - 1 - i) * 8)) & 0xff);
		}
		return size;
	}

	public static long bytesToNumber(int size, byte[] buffer, int offset) {
		long value = 0L;
		for (int i = 0; i < size; i++) {
			value = value + ((buffer[i + offset] & 0xFF) << (i * 8));
		}
		return value;
	}

	public static long bytesToNumber0(int size, byte[] buffer, int offset) {
		long value = 0L;
		for (int i = 0; i < size; i++) {
			value = value + ((buffer[i + offset] & 0xFF) << ((size - 1 - i) * 8));
		}
		return value;
	}

	/** 正序编码 **/
	public static int intToBytes(int value, byte[] buffer, int offset) {
		buffer[0 + offset] = (byte) ((value >> 0) & 0xff);
		buffer[1 + offset] = (byte) ((value >> 8) & 0xff);
		buffer[2 + offset] = (byte) ((value >> 16) & 0xff);
		buffer[3 + offset] = (byte) ((value >> 24) & 0xff);

		// for (int i = 0; i < intSize; i++) {
		// buffer[i + offset] = (byte) ((value >> (8 * i)) & 0xFF);
		// }
		return intSize;
	}

	/** 正序解码 **/
	public static int bytesToInt(byte[] buffer, int offset) {
		int value = 0;
		value = value + ((buffer[0 + offset] & 0xFF) << 0);
		value = value + ((buffer[1 + offset] & 0xFF) << 8);
		value = value + ((buffer[2 + offset] & 0xFF) << 16);
		value = value + ((buffer[3 + offset] & 0xFF) << 24);

		// int value = 0;
		// for (int i = 0; i < intSize; i++) {
		// value += (buffer[i + offset] & 0xFF) << (8 * i);
		// }
		return value;
	}

	/** 反序编码 **/
	public static int intToBytes0(int value, byte[] buffer, int offset) {
		int intSize = 4;
		buffer[0 + offset] = (byte) ((value >> 24) & 0xff);
		buffer[1 + offset] = (byte) ((value >> 16) & 0xff);
		buffer[2 + offset] = (byte) ((value >> 8) & 0xff);
		buffer[3 + offset] = (byte) ((value >> 0) & 0xff);

		// for (int i = 0; i < intSize; i++) {
		// buffer[i + offset] = (byte) ((value >> (8 * i)) & 0xFF);
		// }
		return intSize;
	}

	/** 反序解码 **/
	public static int bytesToInt0(byte[] buffer, int offset) {
		int value = 0;
		value = value + ((buffer[0 + offset] & 0xFF) << 24);
		value = value + ((buffer[1 + offset] & 0xFF) << 16);
		value = value + ((buffer[2 + offset] & 0xFF) << 8);
		value = value + ((buffer[3 + offset] & 0xFF) << 0);

		// int value = 0;
		// for (int i = 0; i < intSize; i++) {
		// value += (buffer[i + offset] & 0xFF) << (8 * i);
		// }
		return value;
	}

	/**
	 * 把byte[]转成字符串, 与String.getBytes()并不相同.
	 * 
	 * @param buffer
	 * @param start
	 * @param size
	 * @return
	 */
	public static String bytesToString(byte[] buffer, int start, int size) {
		// 转成char数组
		char[] strBuf = new char[size];
		for (int i = 0; i < size; i++) {
			byte b = buffer[start + i];
			strBuf[i] = (char) b;
		}
		// 生成字符串
		String str = new String(strBuf);
		return str;
	}

	/**
	 * 把byte[]转成字符串, 与String.getBytes()并不相同.避免编码转换.
	 */
	public static String bytesToString(byte[] buffer) {
		return bytesToString(buffer, 0, buffer.length);
	}

	/**
	 * 把字符串转成byte[], 与String.getBytes()并不相同.避免编码转换.
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] stringToBytes(String str) {
		// 创建数组
		int size = str.length();
		// 解析读取
		byte[] buffer = new byte[size];
		for (int i = 0; i < size; i++) {
			char c = str.charAt(i);
			buffer[i] = (byte) c;
		}
		return buffer;
	}

	/** 提取数据 **/
	public static int readBytes(InputStream input, byte[] out, int tempBuffer) {
		try {
			// 提取数据
			int maxBuffer = out.length;
			int totalSize = 0;
			// 遍历读取所有数据
			byte[] temp = new byte[tempBuffer];
			while (true) {
				int size = input.read(temp);
				if (size < 0) {
					break; // 数据读取完毕
				}
				// 检测数据大小
				if (totalSize + size > maxBuffer) {
					throw new Exception("缓存数据不足");
				}
				// 写入数据
				System.arraycopy(temp, 0, out, totalSize, size);
				totalSize += size;
			}
			input.close();
			return totalSize;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 从缓存中读取数据
	 * 
	 * @param maxBuffer
	 *            最大缓存数据
	 * @param tempBuffer
	 *            每次读取数据
	 */
	public static byte[] readBytes(InputStream input, int maxBuffer, int tempBuffer) {
		try {
			// 提取数据
			maxBuffer = (maxBuffer > 0) ? maxBuffer : input.available();
			byte[] buffer = new byte[maxBuffer];
			// 执行读取
			int totalSize = readBytes(input, buffer, tempBuffer);
			if (totalSize <= 0) {
				return new byte[totalSize];
			}
			// 判断数据长度是否有出入
			if (buffer.length == totalSize) {
				return buffer;
			}
			// 长度有出入, 提取数据返回
			byte[] ret = new byte[totalSize];
			System.arraycopy(buffer, 0, ret, 0, totalSize);
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
