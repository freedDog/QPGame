package com.game.framework.utils.encrypt;

/**
 * 动态处理baseN算法<br>
 * 不要处理进制(没法搞)
 * BaseN.java
 * @author JiangBangMing
 * 2019年1月4日下午3:26:58
 */
public class BaseN {
	protected final int n; // n代表单个字母的数量
	protected final char[] chars; // 对应的字母表, 长度必须是n(大于可行);
	protected final byte[] tables; // 解析表128(通常字母就只有128)
	protected final int nq; // base n n为多少个2的次方
	protected final int s; // 多个n进行一次编译
	protected final int d; // 多少8位字母进行一次编码
	protected final byte bitmax; // 满格值(nq位上全满的数字)

	public BaseN(int nq, char[] chars) {
		this.nq = nq;
		// 判断是否能生成这种编码格式(不能少于1, 因为产生不了识别, 不能大于8(没意义))
		if (nq <= 1 || nq >= 8) {
			throw new Error("不能创建base N为:" + nq + "的对象");
		}

		// 判断字符是否符合
		this.n = (int) Math.pow(2, nq);
		if (chars.length < n) {
			throw new Error("字符数不足, 至少需要" + n + "个.");
		}
		// 满格数据
		byte bitmax = 0;
		for (int i = 0; i < nq; i++) {
			// bitmax = (byte) Math.pow(2, nq);
			bitmax = (byte) (bitmax + (0x1 << i));
		}
		this.bitmax = bitmax;

		// 赋值对应表
		this.chars = chars;
		// 判断字符最大大小
		int maxtable = 0;
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			maxtable = Math.max(maxtable, (int) c);
		}
		tables = new byte[maxtable + 1];
		// 初始化数据
		for (int i = 0; i < tables.length; i++) {
			tables[i] = (byte) 0xFF; // 清除
		}
		// 赋值
		for (int i = 0; i < chars.length; i++) {
			int b = chars[i]; // 这个字母的值
			tables[b] = (byte) i; // 赋值对应的ID
		}

		// 匹配最新数据
		int maxs = 10;
		int maxd = 8; // 用long处理8位
		int s = 0;
		int d = 0;
		int minof = Integer.MAX_VALUE; // 最小偏差
		for (int i = 1; i < maxs; i++) {
			for (int j = 1; j < maxd; j++) {
				int sn = nq * i;
				int dn = j * 8;
				if (sn >= dn) {
					int offset = sn - dn; // 计算这个比例的偏差
					if (offset < minof) {
						minof = offset; // 记录偏差值
						s = i; // 记录s
						d = j; // 记录d
					}
				}
			}
		}
		this.s = s;
		this.d = d;

	}

	/** 加密 **/
	public String encode(byte[] data) {
		// 先判断生成多大的长度数据
		int dsize = data.length;
		// int mb = datasize * 8; // 总共位数
		// double e = mb / (double) d; // 能分成多少个数字, 向上取整.
		int csize = (int) Math.ceil((dsize / (double) d) * s); // 向上取整

		// 创建数据缓存
		char[] chars = new char[csize];

		// 遍历处理
		long value = 0L;
		int vi = 0; // value 中存在多少位数据
		int offset = 0; // 输出数据偏移值
		// int index = 0; // 第几个数据体
		for (int i = 0; i < dsize; i++) {
			byte b = data[i];
			// 给value做赋值整合
			value = ((long) (b & 0xFF) << vi) + value; // 数据往高提交, 标记增加
			vi += 8;

			// 写入数据
			while (vi >= nq) {
				int b0 = (int) (value & bitmax);
				char c = this.chars[b0];
				chars[offset++] = c; // 无转化

				// 高位转换
				// int index0 = index++;
				// int is = (int) (index0 / s) * s + ((s - 1) - (index0) % s);
				// chars[is] = c;
				// System.out.println(index0 + "->" + is);

				// 提取一位出来
				value = value >>> nq; // (>>>无符号位移)
				vi -= nq;
			}

			// vi = 0;
			// value = 0L;
		}

		// 处理剩余数据
		if (vi > 0) {
			// int of = nq - vi;
			// value = value << of; // 补齐偏移

			int b0 = (int) (value & bitmax);
			char c = this.chars[b0];
			chars[offset++] = c;
		}

		return new String(chars);
	}

	public byte[] decode(String str) {
		return decode(str.toCharArray());
	}

	/** 解密 **/
	public byte[] decode(char[] chars) {
		int csize = chars.length;
		int dsize = (int) (csize / (double) s * d);
		// 创建数组
		byte[] buffer = new byte[dsize];

		// 遍历数据
		long value = 0L;
		int vi = 0;
		int offset = 0;
		for (int i = 0; i < csize; i++) {
			char c = chars[i]; // 无转化
			// char c = chars[(int) (i / s) * s + ((s - 1) - (i) % s)]; // 高位转化
			byte b = tables[(int) c];
			// 给value做赋值整合
			value = ((long) (b & bitmax) << vi) + value; // 数据往高提交, 标记增加
			vi += nq;
			// 提取数据
			while (vi >= 8) {
				int b0 = (int) (value & 0xFF);
				buffer[offset++] = (byte) b0;
				// 提取一位出来
				value = value >>> 8; // 降阶段(>>>无符号位移)
				vi -= 8;
			}
		}

		return buffer;
	}

	/** 把base n数据转成数据(非解密) **/
	public static byte[] baseNBuffer(String str, char[] chars) {
		char[] strchars = str.toCharArray();
		int strsize = strchars.length;

		byte[] buffer = new byte[strsize];
		for (int i = 0; i < strsize; i++) {
			char c = strchars[i];
			int b = -2;
			for (int j = 0; j < chars.length; j++) {
				if (chars[j] == c) {
					b = j;
					break;
				}
			}
			buffer[i] = (byte) b;
		}

		return buffer;
	}

	/** 输出二进制数据 **/
	public static String baseNString(byte[] buffer, int bitsize) {
		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < buffer.length; i++) {
			byte bit = buffer[i];
			for (int j = 0; j < bitsize; j++) {
				int v = (bit & 0x1);
				bit = (byte) (bit >>> 1);
				strBuf.append(v);
			}
			strBuf.append('|');
		}
		return strBuf.toString();
	}

	// private static final char[] ALPHABET = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'M', 'N', 'O', 'P', 'Q', 'S', 'T', 'U', 'V', 'W', 'X', 'Z', '1', '2', '3', '4', '5', '6', '7',
	// '8',
	// '9', '0' };
	// private static char[] base64EncodeChars = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a',
	// 'b',
	// 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };

	// public static void main(String[] args) {
	// // test01(args);
	//
	// int count = 3;
	// byte[] buffer = new byte[count];
	// for (int i = 0; i < count; i++) {
	// buffer[i] = (byte) (i + 1);
	// // buffer[i] = (byte) 0x01;
	// // buffer[i] = (byte) 0xFF;
	// // buffer[i] = (byte) (Math.random() * 255);
	// }
	//
	// // String bufStr = "123";
	// // buffer = bufStr.getBytes();
	// System.out.println(Arrays.toString(buffer));
	//
	// // // base32 n
	// // BaseN basen = new BaseN(5, ALPHABET);
	// // String str = basen.encode(buffer);
	// // byte[] dr = basen.decode(str);
	// // System.out.println(str + " " + str.length() +" " + check(buffer, dr) + "\n" + Arrays.toString(dr));
	// //
	// // // base32
	// // String str0 = Base32.encode(buffer);
	// // byte[] dr0 = Base32.decode(str0);
	// // System.out.println(str0 + " " + str0.length() +" " + check(buffer, dr0) + "\n" + Arrays.toString(dr0));
	//
	// // // base64 n
	// // BaseN basen = new BaseN(6, base64EncodeChars);
	// // String str = basen.encode(buffer);
	// // byte[] dr = basen.decode(str);
	// // String ds = new String(dr);
	// // System.out.println(str + " " + str.length() + " " + check(buffer, dr) + "\n" + Arrays.toString(dr) + "\n" + ds);
	// //
	// // // base64
	// // String str0 = EncryptUtils.Base64.encode(buffer);
	// // byte[] dr0 = EncryptUtils.Base64.decode(str0);
	// // String ds0 = new String(dr0);
	// // System.out.println(str0 + " " + str0.length() + " " + check(buffer, dr0) + "\n" + Arrays.toString(dr0) + "\n" + ds0);
	// //
	// // // 输出对应表
	// // byte[] buf = baseNBuffer(str, base64EncodeChars);
	// // byte[] buf0 = baseNBuffer(str0, base64EncodeChars);
	// // System.out.println("s " + baseNString(buffer, 8));
	// // System.out.println("n " + baseNString(buf, 6));
	// // System.out.println("b " + baseNString(buf0, 6));
	//
	// // base n 16
	// BaseN basen = new BaseN(2, base64EncodeChars);
	// String str = basen.encode(buffer);
	// byte[] dr = basen.decode(str);
	// String ds = new String(dr);
	// System.out.println(str + " " + str.length() + " " + check(buffer, dr) + "\n" + Arrays.toString(dr) + "\n" + ds);
	// byte[] buf = baseNBuffer(str, base64EncodeChars);
	// System.out.println("s " + baseNString(buffer, 8));
	// System.out.println("n " + baseNString(buf, 2));
	// }
	//
	// public static boolean check(byte[] src, byte[] dst) {
	// int size = src.length;
	// if (size != dst.length) {
	// return false;
	// }
	// // 遍历比较
	// for (int i = 0; i < size; i++) {
	// byte d = dst[i];
	// if (d != src[i]) {
	// return false;
	// }
	// }
	// return true;
	// }
	//
	// public static void test01(String[] args) {
	// // 给value做赋值整合
	// long value = 0L;
	// int vi = 0;
	// int nq = 6;
	// int bitmax = 63;
	// // int nq = 8;
	// // int bitmax = 255;
	//
	// for (int i = 0; i < 11; i++) {
	// int b = 0xFF;
	// value = ((long) (b & 0xFF) << vi) + value; // 数据往高提交, 标记增加
	// vi += 8;
	//
	// // 检测数据溺出(64)
	// if (vi >= 64) {
	// // 写入数据
	// while (vi > nq) {
	// int b0 = (int) (value & bitmax);
	// System.out.println("str:" + b0);
	// // 提取一位出来
	// value = (value >>> nq); // (>>>无符号位移)
	// vi -= nq;
	// }
	// }
	// }
	// }
}
