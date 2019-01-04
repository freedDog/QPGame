package com.game.proto.msg;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.util.List;

import com.game.framework.component.log.Log;

/**
 * 协议数据流处理工具
 * ByteBufferHelper.java
 * @author JiangBangMing
 * 2019年1月4日下午3:00:03
 */
public class ByteBufferHelper {
	public static void putStringArray(ByteBuf buffer, List<String> data) {
		buffer.writeShort((short) data.size());
		for (String child : data) {
			putString(buffer, child);
		}
	}

	public static void putObjectArray(ByteBuf buffer, List<? extends RpMessage> data) {
		buffer.writeShort((short) data.size());
		for (RpMessage child : data) {
			child.serialize(buffer);
		}
	}

	public static void putIntArray(ByteBuf buffer, List<Integer> data) {
		buffer.writeShort((short) data.size());
		for (Integer child : data) {
			buffer.writeInt(child);
		}
	}

	public static void putShortArray(ByteBuf buffer, List<Short> data) {
		buffer.writeShort((short) data.size());
		for (Short child : data) {
			buffer.writeShort(child);
		}
	}

	public static void putLongArray(ByteBuf buffer, List<Long> data) {
		buffer.writeShort((short) data.size());
		for (Long child : data) {
			buffer.writeLong(child);
		}
	}

	public static void putBooleanArray(ByteBuf buffer, List<Boolean> data) {
		buffer.writeShort((short) data.size());
		for (Boolean child : data) {
			buffer.writeBoolean(child);
		}
	}

	public static void putCharArray(ByteBuf buffer, List<Character> data) {
		buffer.writeShort((short) data.size());
		for (Character child : data) {
			buffer.writeChar(child);
		}
	}

	public static void putFloatArray(ByteBuf buffer, List<Float> data) {
		buffer.writeShort((short) data.size());
		for (Float child : data) {
			buffer.writeFloat(child);
		}
	}

	public static void putDoubleArray(ByteBuf buffer, List<Double> data) {
		buffer.writeShort((short) data.size());
		for (Double child : data) {
			buffer.writeDouble(child);
		}
	}

	public static void putByteArray(ByteBuf buffer, List<Byte> data) {
		buffer.writeShort((short) data.size());
		for (Byte child : data) {
			buffer.writeByte(child);
		}
	}

	public static void putByteArray(ByteBuf buffer, byte[] data) {
		short size = (data != null) ? (short) data.length : 0;
		buffer.writeShort(size);
		if (size > 0) {
			buffer.writeBytes(data);
		}
	}

	public static void putInt(ByteBuf buffer, int data) {
		buffer.writeInt(data);
	}

	public static void putShort(ByteBuf buffer, short data) {
		buffer.writeShort(data);
	}

	public static void putLong(ByteBuf buffer, long data) {
		buffer.writeLong(data);
	}

	public static void putChar(ByteBuf buffer, char data) {
		buffer.writeChar(data);
	}

	public static void putByte(ByteBuf buffer, byte data) {
		buffer.writeByte(data);
	}

	public static void putBoolean(ByteBuf buffer, boolean data) {
		buffer.writeBoolean(data);
	}

	public static void putFloat(ByteBuf buffer, float data) {
		buffer.writeFloat(data);
	}

	public static void putDouble(ByteBuf buffer, double data) {
		buffer.writeDouble(data);
	}

	public static void putString(ByteBuf buffer, String data) {
		try {
			if (data != null) {
				byte[] strBytes = data.getBytes("UTF-8");
				buffer.writeShort((short) strBytes.length);
				buffer.writeBytes(strBytes);
			} else {
				buffer.writeShort((short) 0);
			}
		} catch (Exception e) {
			buffer.writeShort((short) 0);
			Log.error("String serialize has exception!", e);
		}
	}

	public static void putObject(ByteBuf buffer, RpMessage data) {
		if (data != null) {
			buffer.writeShort((short) 1);
			data.serialize(buffer);
		} else {
			buffer.writeShort((short) 0);
		}
	}

	public static int calcStringLength(String data) {
		int length = 2;
		if (data != null) {
			try {
				length += data.getBytes("UTF-8").length;
			} catch (Exception e) {
				Log.error("catch an exception when calc string length!", e);
			}
		}
		return length;
	}

	public static int calcObjectLength(RpMessage data) {
		int length = 2;
		if (data != null) {
			length += data.calcLength();
		}
		return length;
	}

	public static int calcStringArrayLength(List<String> data) {
		int length = 2;
		for (String child : data) {
			length += calcStringLength(child);
		}
		return length;
	}

	public static int calcObjectArrayLength(List<? extends RpMessage> data) {
		int length = 2;
		for (RpMessage child : data) {
			length += child.calcLength();
		}
		return length;
	}

	public static void readShortArray(ByteBuffer buffer, List<Short> data) {
		int size = buffer.getShort();
		for (int i = 0; i < size; i++) {
			data.add(buffer.getShort());
		}
	}

	public static void readIntArray(ByteBuffer buffer, List<Integer> data) {
		int size = buffer.getShort();
		for (int i = 0; i < size; i++) {
			data.add(buffer.getInt());
		}
	}

	public static void readLongArray(ByteBuffer buffer, List<Long> data) {
		int size = buffer.getShort();
		for (int i = 0; i < size; i++) {
			data.add(buffer.getLong());
		}
	}

	public static void readFloatArray(ByteBuffer buffer, List<Float> data) {
		int size = buffer.getShort();
		for (int i = 0; i < size; i++) {
			data.add(buffer.getFloat());
		}
	}

	public static void readDoubleArray(ByteBuffer buffer, List<Double> data) {
		int size = buffer.getShort();
		for (int i = 0; i < size; i++) {
			data.add(buffer.getDouble());
		}
	}

	public static void readCharArray(ByteBuffer buffer, List<Character> data) {
		int size = buffer.getShort();
		for (int i = 0; i < size; i++) {
			data.add(buffer.getChar());
		}
	}

	public static void readByteArray(ByteBuffer buffer, List<Byte> data) {
		int size = buffer.getShort();
		for (int i = 0; i < size; i++) {
			data.add(buffer.get());
		}
	}

	public static byte[] readByteArray(ByteBuffer buffer) {
		int size = buffer.getShort();
		if (size <= 0) {
			return null;
		}
		byte[] out = new byte[size];
		buffer.get(out, 0, size);
		return out;
	}

	public static void readBooleanArray(ByteBuffer buffer, List<Boolean> data) {
		int size = buffer.getShort();
		for (int i = 0; i < size; i++) {
			data.add(buffer.getInt() == 0 ? false : true);
		}
	}

	public static void readStringArray(ByteBuffer buffer, List<String> data) {
		int size = buffer.getShort();
		for (int i = 0; i < size; i++) {
			data.add(getString(buffer));
		}
	}

	public static short getShort(ByteBuffer buffer) {
		return buffer.getShort();
	}

	public static int getInt(ByteBuffer buffer) {
		return buffer.getInt();
	}

	public static long getLong(ByteBuffer buffer) {
		return buffer.getLong();
	}

	public static float getFloat(ByteBuffer buffer) {
		return buffer.getFloat();
	}

	public static double getDouble(ByteBuffer buffer) {
		return buffer.getDouble();
	}

	public static char getChar(ByteBuffer buffer) {
		return buffer.getChar();
	}

	public static boolean getBoolean(ByteBuffer buffer) {
		return buffer.get() > 0;
	}

	public static byte getByte(ByteBuffer buffer) {
		return buffer.get();
	}

	public static String getString(ByteBuffer buffer) {
		int length = buffer.getShort();
		byte[] bytes = new byte[length];
		buffer.get(bytes);
		try {
			return new String(bytes, "UTF-8");
		} catch (Exception e) {
			Log.error("String deserialize has an exception!", e);
			return "";
		}
	}

}
