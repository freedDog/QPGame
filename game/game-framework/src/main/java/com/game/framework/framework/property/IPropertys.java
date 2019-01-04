package com.game.framework.framework.property;

/**
 * 属性集合接口 IPropertys.java
 * 
 * @author JiangBangMing 2019年1月3日下午3:05:46
 */
public interface IPropertys {
	/** 获取属性数值 **/
	<T extends Enum<?>> double getValue(T propType);

	/** 设置属性数值 **/
	<T extends Enum<?>> void setValue(T propType, double value);

	/** 设置属性数值 **/
	<T extends Enum<?>> void changeValue(T propType, double value);

	/** 获取属性数值 **/
	double getValue(int propType);

	/** 设置属性数值 **/
	void setValue(int propType, double value);

	/** 修改数值 **/
	void changeValue(int propType, double value);

	/** 批量设置属性 **/
	void setValues(double v);

	/** 批量属性修改 **/
	boolean changeValues(double value);

	/** 属性设置 **/
	boolean setValues(IPropertys src, double scale);

	/** 属性修改 **/
	boolean changeValues(IPropertys src, double scale);

	/** 重设属性 **/
	public boolean reset();

	/** 获取属性类型总数 **/
	int getTypeSize();
}
