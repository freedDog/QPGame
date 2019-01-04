package com.game.framework.jdbc.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 数值处理工具
 * NumberUtils.java
 * @author JiangBangMing
 * 2019年1月3日下午4:51:46
 */
public class NumberUtils
{
	/** 转化成数值类型 **/
	public static <T> T toNumber(Object obj, Class<T> clazz)
	{
		// 字符串转化
		if (obj == null)
		{
			return null;
		}
		// 检测对象是否是数值, 是的话做数值转化.
		if (isNumber(obj.getClass()))
		{
			return convertNumber((Number) obj, clazz);
		}

		return parseNumber(obj, clazz);
	}

	/** 转化数值(1种类型转成另一种类型) **/
	@SuppressWarnings("unchecked")
	public static <T> T convertNumber(Number number, Class<T> targetClass)
	{
		// 检测类型
		if (targetClass.isInstance(number))
		{
			return (T) number; // 类型符合
		}
		else if (targetClass == byte.class || targetClass.equals(Byte.class))
		{
			long value = number.longValue();
			if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE)
			{
				throw new RuntimeException("数值类型转化失败, 超过范围! " + value + " -> " + targetClass);
			}
			return (T) new Byte(number.byteValue());
		}
		else if (targetClass == short.class || targetClass.equals(Short.class))
		{
			long value = number.longValue();
			if (value < Short.MIN_VALUE || value > Short.MAX_VALUE)
			{
				throw new RuntimeException("数值类型转化失败, 超过范围! " + value + " -> " + targetClass);
			}
			return (T) new Short(number.shortValue());
		}
		else if (targetClass == int.class || targetClass.equals(Integer.class))
		{
			long value = number.longValue();
			if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE)
			{
				throw new RuntimeException("数值类型转化失败, 超过范围! " + value + " -> " + targetClass);
			}
			return (T) new Integer(number.intValue());
		}
		else if (targetClass == long.class || targetClass.equals(Long.class))
		{
			return (T) new Long(number.longValue());
		}
		else if (targetClass.equals(BigInteger.class))
		{
			if (number instanceof BigDecimal)
			{
				return (T) ((BigDecimal) number).toBigInteger();
			}
			return (T) BigInteger.valueOf(number.longValue());
		}
		else if (targetClass == float.class || targetClass.equals(Float.class))
		{
			return (T) new Float(number.floatValue());
		}
		else if (targetClass == double.class || targetClass.equals(Double.class))
		{
			return (T) new Double(number.doubleValue());
		}
		else if (targetClass.equals(BigDecimal.class))
		{
			return (T) new BigDecimal(number.toString());
		}
		return null;
	}

	/** 解析数值, 转成字符串再处理. **/
	@SuppressWarnings("unchecked")
	public static <T> T parseNumber(Object obj, Class<T> clazz)
	{
		// 字符串转化
		if (obj == null)
		{
			return null;
		}
		String text = obj.toString().trim();
		if (text == null || text.length() <= 0)
		{
			return null;
		}

		// 类型转化
		if (clazz == byte.class || clazz == Byte.class)
		{
			return (T) Byte.valueOf(text);
		}
		else if (clazz == short.class || clazz == Short.class)
		{
			return (T) Short.valueOf(text);
		}
		else if (clazz == int.class || clazz == Integer.class)
		{
			return (T) Integer.valueOf(text);
		}
		else if (clazz == long.class || clazz == Long.class)
		{
			return (T) Long.valueOf(text);
		}
		else if (clazz == float.class || clazz == Float.class)
		{
			return (T) Float.valueOf(text);
		}
		else if (clazz == double.class || clazz == Double.class)
		{
			return (T) Double.valueOf(text);
		}
		else if (clazz == BigInteger.class)
		{
			return (T) new BigInteger(text);
		}
		else if (clazz.equals(BigDecimal.class) || clazz.equals(Number.class))
		{
			return (T) new BigDecimal(text);
		}
		return null;
	}

	/** 检测数值类型 **/
	public static boolean isNumber(Class<?> clazz)
	{
		if (Number.class.isAssignableFrom(clazz) //
				|| clazz == byte.class //
				|| clazz == short.class //
				|| clazz == int.class //
				|| clazz == long.class //
				|| clazz == float.class //
				|| clazz == double.class //
		)
		{
			return true;
		}
		return false;
	}
}
