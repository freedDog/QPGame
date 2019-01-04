package com.game.framework.jdbc.utils;

import java.sql.Blob;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import com.game.framework.utils.StringUtils;


/**
 * jdbc工具 JdbcUtils.java
 * 
 * @author JiangBangMing 2019年1月3日下午4:51:12
 */
public class JdbcUtils {
	/** 错误语句 **/
	public static final int SQLTYPE_ERROR = 0;
	/** 查询语句 **/
	public static final int SQLTYPE_READ = 1;
	/** 更新语句 **/
	public static final int SQLTYPE_WRITE = 2;

	/** SQL语句类型识别, 就2种(读写) **/
	public static int getSQLType(String sql) {
		// 参考: net.paoding.rose.jade.statement.StatementMetaData
		// 空过滤
		if (sql == null || sql.length() <= 0) {
			return SQLTYPE_ERROR;
		}

		// 查询语句检测
		if (patternFind(sql, "^\\s*SELECT\\s+", Pattern.CASE_INSENSITIVE) //
				|| patternFind(sql, "^\\s*CALL\\s+", Pattern.CASE_INSENSITIVE) //
				|| patternFind(sql, "^\\s*SHOW\\s+", Pattern.CASE_INSENSITIVE) //
				|| patternFind(sql, "^\\s*DESC\\s+", Pattern.CASE_INSENSITIVE) //
				|| patternFind(sql, "^\\s*DESCRIBE\\s+", Pattern.CASE_INSENSITIVE) //
		) {
			return SQLTYPE_READ;
		}

		// 更新语句执行
		if (patternFind(sql, "^\\s*UPDATE\\s+", Pattern.CASE_INSENSITIVE) //
				|| patternFind(sql, "^\\s*REPLACE\\s+", Pattern.CASE_INSENSITIVE) //
				|| patternFind(sql, "^\\s*INSERT\\s+", Pattern.CASE_INSENSITIVE) //
				|| patternFind(sql, "^\\s*DELETE\\s+", Pattern.CASE_INSENSITIVE) //
		) {
			return SQLTYPE_WRITE;
		}

		return SQLTYPE_ERROR;
	}

	/** 查找检测 **/
	protected static boolean patternFind(String str, String regex, int flags) {
		return Pattern.compile(regex, flags).matcher(str).find();
	}

	/** 获取类型对应的SQL类型 **/
	public static int getSqlType(Class<?> clazz) {
		if (byte.class.isAssignableFrom(clazz) || Byte.class.isAssignableFrom(clazz)) {
			return java.sql.Types.BIT;
		} else if (short.class.isAssignableFrom(clazz) || Short.class.isAssignableFrom(clazz)) {
			return java.sql.Types.INTEGER;
		} else if (int.class.isAssignableFrom(clazz) || Integer.class.isAssignableFrom(clazz)) {
			return java.sql.Types.INTEGER;
		} else if (long.class.isAssignableFrom(clazz) || Long.class.isAssignableFrom(clazz)) {
			return java.sql.Types.BIGINT;
		} else if (float.class.isAssignableFrom(clazz) || Float.class.isAssignableFrom(clazz)) {
			return java.sql.Types.FLOAT;
		} else if (double.class.isAssignableFrom(clazz) || Double.class.isAssignableFrom(clazz)) {
			return java.sql.Types.DOUBLE;
		} else if (boolean.class.isAssignableFrom(clazz) || Boolean.class.isAssignableFrom(clazz)) {
			return java.sql.Types.BOOLEAN;
		} else if (String.class.isAssignableFrom(clazz)) {
			return java.sql.Types.CHAR;
		}

		return java.sql.Types.BIT;
	}

	/** 把传入的java对应SQL对象转化 **/
	public static Object toSqlObject(Object obj) {
		// 空过滤
		if (obj == null) {
			return null;
		}
		// 根据类型处理
		Class<?> clazz = obj.getClass();
		if (clazz == java.util.Date.class || clazz == java.sql.Timestamp.class) {
			java.util.Date date = (java.util.Date) obj;
			DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return dataFormat.format(date);
		}
		return obj;
	}

	/** 转成java类 **/
	public static Class<?> toJavaClass(Class<?> sqlClass) {
		// 空过滤
		if (sqlClass == null) {
			return null;
		}

		// 时间类型转化
		if ("oracle.sql.TIMESTAMP".equals(sqlClass.getName()) //
				|| java.sql.Date.class == sqlClass //
				|| java.sql.Time.class == sqlClass //
				|| java.sql.Date.class == sqlClass //
				|| java.sql.Timestamp.class == sqlClass//
				|| java.sql.Date.class == sqlClass //
		) {
			return Date.class;
		}

		return sqlClass;
	}

	/** 把获取的SQL对象转化成java对象 **/
	public static Object toJavaObject(Object obj) throws Exception {
		// 空过滤
		if (obj == null) {
			return null;
		}
		// 处理转化
		Class<?> retType = obj.getClass(); // 使用对象的进行转化
		retType = JdbcUtils.toJavaClass(retType); // 进行类型过滤
		return toJavaObject(obj, retType);
	}

	/** 把获取的SQL对象转化成java对象 **/
	public static Object toJavaObject(Object obj, Class<?> retType) throws Exception {
		// 空过滤
		if (obj == null) {
			return null;
		}
		// 无限限制
		if (retType == null) {
			return obj; // 直接返回当前对象类型
		}

		// 相同类型判断
		if (retType.isInstance(obj)) {
			return obj;
		}

		// 根据类型转化
		if (String.class.equals(retType)) {
			return obj.toString();
		} else if (NumberUtils.isNumber(retType)) {
			return NumberUtils.toNumber(obj, retType);
		} else if (retType == byte[].class) {
			if (obj instanceof Blob) {
				Blob blob = ((Blob) obj);
				int bsize = (int) blob.length();
				return blob.getBytes(0, bsize);
			}
		} else if (retType == boolean.class || retType == Boolean.class) {
			if (obj instanceof Boolean) {
				return obj;
			} else if (obj instanceof Number) {
				return ((Number) obj).longValue() > 0;
			} else if (obj instanceof String) {
				return obj.toString().equals("true");
			}
		} else if (retType == int[].class) {
			if (obj instanceof String) {
				return StringUtils.splitToInt((String) obj, ",");
			}
		} else {
			throw new Exception("不支持转化类型: retType=" + retType + " obj=" + obj);
		}

		// return obj;
		throw new Exception("无法转化类型: retType=" + retType + " obj=" + obj);
	}
}
