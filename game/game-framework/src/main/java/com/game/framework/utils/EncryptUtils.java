package com.game.framework.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * 加密工具
 * EncryptUtils.java
 * @author JiangBangMing
 * 2019年1月8日下午3:08:56
 */
public class EncryptUtils {

	/** MD5加密(不可逆算法) **/
	public static String MD5(byte[] code) {
		try {
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(code);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			StringBuffer hexString = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < md.length; i++) {
				String shaHex = Integer.toHexString(md[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	/** MD5加密(不可逆算法) **/
	public static String MD5(String code, String enc) {
		try {
			return MD5(code.getBytes(enc));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/** MD5加密(不可逆算法) **/
	public static String MD5(String code) {
		return MD5(code, "UTF-8");
	}

	/**
	 * http://blog.csdn.net/happylee6688/article/details/43968549<br>
	 * HMAC，全称为“Hash Message Authentication Code”，中文名“散列消息鉴别码”，主要是利用哈希算法，以一个密钥和一个消息为输入，生成一个消息摘要作为输出。<br>
	 * 一般的，消息鉴别码用于验证传输于两个共 同享有一个密钥的单位之间的消息。HMAC 可以与任何迭代散列函数捆绑使用。MD5 和 SHA-1 就是这种散列函数。HMAC 还可以使用一个用于计算和确认消息鉴别值的密钥。
	 * **/
	public static class HMAC {

		/** HmacSHA1(不可逆) */
		public static byte[] SHA1(byte[] buffer, byte[] key) {
			try {
				Mac mac = Mac.getInstance("HmacSHA1");
				String algorithm = mac.getAlgorithm();
				SecretKeySpec secret = new SecretKeySpec(key, algorithm);
				mac.init(secret);
				return mac.doFinal(buffer);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		public static String SHA256(byte[] data, byte[] key) {
			try {
				SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA256");
				Mac mac = Mac.getInstance("HmacSHA256");
				mac.init(signingKey);
				return byte2hex(mac.doFinal(data));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			}
			return null;
		}

		/** 字节数组转换成字符串 **/
		private static String byte2hex(byte[] b) {
			StringBuilder hs = new StringBuilder();
			int bsize = (b != null) ? b.length : 0;
			// 遍历字节
			for (int n = 0; n < bsize; n++) {
				String stmp = Integer.toHexString(b[n] & 0XFF);
				if (stmp.length() == 1) {
					hs.append('0');
				}
				hs.append(stmp);
			}
			return hs.toString().toUpperCase();
		}

	}

	/**
	 * SHA加密(不可逆算法)<br>
	 * http://blog.csdn.net/happylee6688/article/details/43965609<br>
	 * SHA，全称为“Secure Hash Algorithm”，中文名“安全哈希算法”，主要适用于数字签名标准（Digital Signature Standard DSS）里面定义的数字签名算法（Digital Signature Algorithm DSA）。<br>
	 * 对于长度小于 2^64 位的消息，SHA1 会产生一个 160 位的消息摘要。<br>
	 * **/
	public static class SHA {

		public static String SHA1(String decript) {
			try {
				MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
				digest.update(decript.getBytes());
				byte messageDigest[] = digest.digest();
				// Create Hex String
				StringBuffer hexString = new StringBuffer();
				// 字节数组转换为 十六进制 数
				for (int i = 0; i < messageDigest.length; i++) {
					String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
					if (shaHex.length() < 2) {
						hexString.append(0);
					}
					hexString.append(shaHex);
				}
				return hexString.toString();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		public static String SHA0(String decript) {
			try {
				MessageDigest digest = java.security.MessageDigest.getInstance("SHA");
				digest.update(decript.getBytes());
				byte messageDigest[] = digest.digest();
				// Create Hex String
				StringBuffer hexString = new StringBuffer();
				// 字节数组转换为 十六进制 数
				for (int i = 0; i < messageDigest.length; i++) {
					String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
					if (shaHex.length() < 2) {
						hexString.append(0);
					}
					hexString.append(shaHex);
				}
				return hexString.toString();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

	}

	public static class Base64 {
		/** 加密数据 **/
		public static String encode(String src) {
			return encode(src.getBytes());
		}

		/** 解析成字符串 **/
		public static String decodeToString(String str) {
			try {
				byte[] buffer = decode(str);
				return new String(buffer);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/** 加密数据 **/
		public static String encode(byte[] data) {
			return com.game.framework.utils.encrypt.Base64.encode(data);
		}

		/** 解析数据 **/
		public static byte[] decode(String str) {
			try {
				return com.game.framework.utils.encrypt.Base64.decode(str);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	/** DES加密(密钥要8位, 超过8位后的密钥部分无效) **/
	public static class DES {

		/** 加密 **/
		public static byte[] encrypt(byte[] datasource, byte[] password) {
			try {
				SecureRandom random = new SecureRandom();
				DESKeySpec desKey = new DESKeySpec(password);
				// 创建一个密匙工厂，然后用它把DESKeySpec转换成
				SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
				SecretKey securekey = keyFactory.generateSecret(desKey);
				// Cipher对象实际完成加密操作
				Cipher cipher = Cipher.getInstance("DES");
				// 用密匙初始化Cipher对象
				cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
				// 现在，获取数据并加密
				// 正式执行加密操作
				return cipher.doFinal(datasource);
			} catch (Throwable e) {
				// e.printStackTrace();
				if (e.getClass() == InvalidKeyException.class) {
					System.err.println(" 加密密钥错误:" + Arrays.toString(password) + " (需要8位长度的密码, 超过8位后的密钥部分无效.)");
				} else {
					System.err.println(e.getLocalizedMessage() + " 加密失败:" + Arrays.toString(password) + " -> " + Arrays.toString(datasource));
				}

			}
			return null;
		}

		/** 解密 **/
		public static byte[] decrypt(byte[] src, byte[] password) {
			try {
				// DES算法要求有一个可信任的随机数源
				SecureRandom random = new SecureRandom();
				// 创建一个DESKeySpec对象
				DESKeySpec desKey = new DESKeySpec(password);
				// 创建一个密匙工厂
				SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
				// 将DESKeySpec对象转换成SecretKey对象
				SecretKey securekey = keyFactory.generateSecret(desKey);
				// Cipher对象实际完成解密操作
				Cipher cipher = Cipher.getInstance("DES");
				// 用密匙初始化Cipher对象
				cipher.init(Cipher.DECRYPT_MODE, securekey, random);
				// 真正开始解密操作
				return cipher.doFinal(src);
			} catch (Throwable e) {
				// e.printStackTrace();
				System.err.println("解码失败:" + Arrays.toString(password) + " -> " + Arrays.toString(src));
			}
			return null;
		}

		/** 加密+base64 **/
		public static String encrypt(String src, String encryptKey) {
			byte[] keys = encryptKey.getBytes();
			byte[] buffer = src.getBytes();
			buffer = encrypt(buffer, keys);
			// return BufferUtils.bytesToString(buffer);
			return EncryptUtils.Base64.encode(buffer);
		}

		/** 解密+base64 **/
		public static String decrypt(String src, String encryptKey) {
			byte[] keys = encryptKey.getBytes();
			byte[] buffer = EncryptUtils.Base64.decode(src);
			buffer = decrypt(buffer, keys);
			return new String(buffer);
		}

	}

	/** 安全ZED算法, 通过前置码兼容是否需要加密 **/
	public static class SZED {

		/** 加密数据 **/
		public static byte[] encrypt(byte[] data, byte[] key, byte[] verify) {
			byte[] buffer = ZED.encryptEnhanced(data, key);
			// 判断校验码
			int vsize = (verify != null) ? verify.length : 0;
			if (vsize <= 0) {
				return buffer; // 不需要校验, 直接返回结果
			}
			// 复制数据
			byte[] nbuffer = new byte[buffer.length + vsize];
			System.arraycopy(verify, 0, nbuffer, 0, vsize); // 复制校验码
			System.arraycopy(buffer, 0, nbuffer, vsize, buffer.length); // 复制内容
			return nbuffer;
		}

		/** 解密数据 **/
		public static byte[] decrypt(byte[] data, byte[] key, byte[] verify) {
			// 空判断
			if (data == null) {
				return null;
			}
			// 校验码判断
			if (verify != null) {
				int vsize = (verify != null) ? verify.length : 0;
				for (int i = 0; i < vsize; i++) {
					if (verify[i] != data[i]) {
						return data; // 校验不通过, 没有加密, 返回数据.
					}
				}
				// 验证通过, 可以解析
				byte[] ndata = new byte[data.length - vsize];
				System.arraycopy(data, vsize, ndata, 0, ndata.length);
				data = ndata;
			}

			// 解析数据
			return ZED.decryptEnhanced(data, key);
		}

	}

	/** 在加密算法中按规律嵌入密码作为验证 **/
	public static class ZED {
		/** 偏移:小于256 **/
		protected static final byte offsetKey = 18;
		/** 变化值:小于256 **/
		protected static final byte indexKey = 121;

		/** 加强加密+base64 **/
		public static String encryptE64(String src, String encryptKey) {
			byte[] keys = encryptKey.getBytes();
			byte[] buffer = src.getBytes();
			buffer = encryptEnhanced(buffer, keys);
			// return BufferUtils.bytesToString(buffer);
			return EncryptUtils.Base64.encode(buffer);
		}

		/** 加强解密+base64 **/
		public static String decryptE64(String src, String encryptKey) {
			byte[] keys = encryptKey.getBytes();
			byte[] buffer = EncryptUtils.Base64.decode(src);
			buffer = decryptEnhanced(buffer, keys);
			return new String(buffer);
		}

		/** 加强版算法加密 **/
		public static byte[] encryptEnhanced(byte[] buffer, byte[] keys) {
			int v = BufferUtils.getCheckDigit(keys, 0, keys.length); // 校验码
			buffer = EncryptUtils.ZED.encrypt(buffer, keys);
			buffer = EncryptUtils.inversion(buffer, 0, buffer.length, v % 2, 0xFF);
			buffer = EncryptUtils.overturn(buffer, 0, buffer.length, v % 3);
			buffer = EncryptUtils.offset(buffer, 0, buffer.length, v % 3, v % 128);
			return buffer;
		}

		/** 加强版算法解密 **/
		public static byte[] decryptEnhanced(byte[] buffer, byte[] keys) {
			int v = BufferUtils.getCheckDigit(keys, 0, keys.length); // 校验码
			buffer = EncryptUtils.offset(buffer, 0, buffer.length, v % 3, -v % 128);
			buffer = EncryptUtils.overturn(buffer, 0, buffer.length, v % 3);
			buffer = EncryptUtils.inversion(buffer, 0, buffer.length, v % 2, 0xFF);
			buffer = EncryptUtils.ZED.decrypt(buffer, keys);
			return buffer;
		}

		/** 加密 **/
		public final static byte[] encrypt(byte[] buffer, String password) {
			return encrypt(buffer, password.getBytes());
		}

		/** 加密 **/
		public final static byte[] encrypt(byte[] buffer, byte[] keys) {
			// 创建数据体
			int bufSize = buffer.length;
			byte[] out = new byte[bufSize]; // 新数据
			// 加密
			encrypt(buffer, keys, out, 0);
			return out;
		}

		/** 加密, 变化/偏移/倒置等 **/
		public static int encrypt(byte[] buffer, byte[] keys, byte[] out, int offset) {
			// 密码数据
			int bufSize = buffer.length;
			int keySize = (keys != null) ? keys.length : 0;

			// 遍历处理
			for (int i = 0; i < bufSize; i++) {
				byte b = buffer[i];
				byte index = (byte) (indexKey - (i % indexKey));
				byte c = (keySize > 0) ? keys[i % keySize] : 0;
				c += index;
				// 数据偏移
				b = (byte) ((b + (offsetKey + keySize + c) + 256) % 256);
				// 数据转化
				b = (byte) (b ^ c);
				// 设置数据
				out[i + offset] = b;
			}
			return bufSize + offset;
		}

		public static int decrypt(byte[] buffer, byte[] keys, byte[] out, int offset) {
			// 密码数据
			int bufSize = buffer.length;
			int keySize = (keys != null) ? keys.length : 0;
			// 遍历处理
			for (int i = 0; i < bufSize; i++) {
				byte b = buffer[i];
				byte index = (byte) (indexKey - (i % indexKey));
				byte c = (keySize > 0) ? keys[i % keySize] : 0;
				c += index;
				// 数据转化
				b = (byte) (b ^ c);
				// 数据偏移
				b = (byte) ((b - (offsetKey + keySize + c) + 256) % 256);
				// 设置数据
				out[i + offset] = b;
			}
			return bufSize + offset;
		}

		/** 解密 **/
		public final static byte[] decrypt(byte[] buffer, String password) {
			return decrypt(buffer, password.getBytes());
		}

		/** 解密 **/
		public final static byte[] decrypt(byte[] buffer, byte[] keys) {
			// 创建数据体
			int bufSize = buffer.length;
			byte[] out = new byte[bufSize];

			// 解密
			decrypt(buffer, keys, out, 0);
			return out;
		}
	}

	/**
	 * 数据对调, 间隔多少数据做一次对调(相对中间)
	 * 
	 * @param source
	 *            数据源
	 * @param offset
	 *            数据偏移
	 * @param size
	 *            数据大小
	 * @param interval
	 *            间隔跳过
	 * @return
	 */
	public static byte[] overturn(byte[] source, int offset, int size, int interval) {
		int count = size / 2;
		for (int i = 0; i < count; i++) {
			if (interval > 0 && i % (interval + 1) != 0) {
				continue; // 跳过
			}
			byte b = source[offset + i];
			source[offset + i] = source[offset + size - i - 1];
			source[offset + size - i - 1] = b;
		}
		return source;
	}

	/**
	 * 数组补差, 间隔多少数据进行一个^inv
	 * 
	 * @param source
	 * @param size
	 * @param interval
	 * @param inv
	 * @return
	 */
	public static byte[] inversion(byte[] source, int offset, int size, int interval, int inv) {
		int count = size;
		for (int i = 0; i < count; i++) {
			if (interval > 0 && i % (interval + 1) != 0) {
				continue; // 跳过
			}
			source[offset + i] = (byte) (source[i + offset] ^ inv);
		}
		return source;
	}

	/**
	 * 数组偏移
	 * 
	 * @param source
	 * @param start
	 * @param size
	 * @param interval
	 * @param offset0
	 * @return
	 */
	public static byte[] offset(byte[] source, int start, int size, int interval, int offset0) {
		int count = size;
		for (int i = 0; i < count; i++) {
			if (interval > 0 && i % (interval + 1) != 0) {
				continue; // 跳过
			}
			byte b = source[start + i];
			int s = (int) (0xFF) & b;
			int o = (s + offset0) % 256; // 偏移保存
			source[start + i] = (byte) o; // 转回二进制
		}
		return source;
	}

}
