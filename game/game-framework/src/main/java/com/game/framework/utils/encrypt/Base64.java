package com.game.framework.utils.encrypt;

import java.io.UnsupportedEncodingException;

/**
 * Base64双向加密工具
 * Base64.java
 * @author JiangBangMing
 * 2019年1月4日下午3:26:20
 */
public class Base64 {
	public static char[] base64EncodeChars = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b',
			'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
	private static byte[] base64DecodeChars;
	static {
		base64DecodeChars = new byte[128]; // 只需要128位即可, 只会存在0~64个数字, 而产生出来的字母只会在0~128中(实际上只有那么几个), 这里需要的是吧32个字母获取对应的数字, 用于反编译
		// 初始化数据
		for (int i = 0; i < base64DecodeChars.length; i++) {
			base64DecodeChars[i] = (byte) 0xFF;
		}
		// 反向对应
		for (int i = 0; i < base64EncodeChars.length; i++) {
			int charKey = base64EncodeChars[i];
			base64DecodeChars[charKey] = (byte) i;
		}
	}

	/**
	 * 加密<br>
	 * 数据变化<br>
	 * s 10000000|10000000|10000000 <br>
	 * b 000000|000010|001000|100000 <br>
	 * 4 3 2 1
	 * 
	 * @param data
	 *            明文的字节数组
	 * @return 密文字符串
	 */
	public static String encode(byte[] data) {
		StringBuffer sb = new StringBuffer();
		int len = data.length;
		int i = 0;
		int b1, b2, b3;
		while (i < len) {
			b1 = data[i++] & 0xff;
			if (i == len) {
				sb.append(base64EncodeChars[b1 >>> 2]);
				sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
				sb.append("==");
				break;
			}
			b2 = data[i++] & 0xff;
			if (i == len) {
				sb.append(base64EncodeChars[b1 >>> 2]);
				sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
				sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
				sb.append("=");
				break;
			}
			b3 = data[i++] & 0xff;
			sb.append(base64EncodeChars[b1 >>> 2]);
			sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
			sb.append(base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);
			sb.append(base64EncodeChars[b3 & 0x3f]);
		}
		return sb.toString();
	}

	/**
	 * 解密
	 * 
	 * @param str
	 *            密文
	 * @return 明文的字节数组
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] decode(String str) throws UnsupportedEncodingException {
		if (str == null || str.length() <= 0) {
			return null;
		}

		StringBuffer sb = new StringBuffer();
		byte[] data = str.getBytes("US-ASCII");
		int len = data.length;
		int i = 0;
		int b1, b2, b3, b4;
		while (i < len) {
			/* b1 */
			do {
				b1 = base64DecodeChars[data[i++]];
			} while (i < len && b1 == -1);
			if (b1 == -1) {
				break;
			}
			/* b2 */
			do {
				b2 = base64DecodeChars[data[i++]];
			} while (i < len && b2 == -1);
			if (b2 == -1) {
				break;
			}
			sb.append((char) ((b1 << 2) | ((b2 & 0x30) >>> 4)));
			/* b3 */
			do {
				b3 = data[i++];
				if (b3 == 61) {
					return sb.toString().getBytes("iso8859-1");
				}
				b3 = base64DecodeChars[b3];
			} while (i < len && b3 == -1);
			if (b3 == -1) {
				break;
			}
			sb.append((char) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));
			/* b4 */
			do {
				b4 = data[i++];
				if (b4 == 61) {
					return sb.toString().getBytes("iso8859-1");
				}
				b4 = base64DecodeChars[b4];
			} while (i < len && b4 == -1);
			if (b4 == -1) {
				break;
			}
			sb.append((char) (((b3 & 0x03) << 6) | b4));
		}
		return sb.toString().getBytes("iso8859-1");
	}

}
