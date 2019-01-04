package com.game.framework.framework.property;

/**
 * 基础属性集合基类 Propertys.java
 * 
 * @author JiangBangMing 2019年1月3日下午3:06:34
 */
public abstract class Propertys implements IPropertys {
	@Override
	public <T extends Enum<?>> void setValue(T propType, double value) {
		this.setValue(propType.ordinal(), value);
	}

	@Override
	public <T extends Enum<?>> void changeValue(T propType, double value) {
		this.changeValue(propType.ordinal(), value);
	}

	@Override
	public <T extends Enum<?>> double getValue(T propType) {
		return getValue(propType.ordinal());
	}

	@Override
	public void changeValue(int index, double value) {
		setValue(index, getValue(index) + value);
	}

	@Override
	public void setValues(double v) {
		// 遍历获取属性
		for (int i = 0; i < getTypeSize(); i++) {
			this.setValue(i, v);
		}
	}

	@Override
	public boolean changeValues(double value) {
		// 遍历获取属性
		for (int i = 0; i < getTypeSize(); i++) {
			changeValue(i, value);
		}
		return true;
	}

	/** 属性设置 **/
	@Override
	public boolean setValues(IPropertys src, double scale) {
		// 遍历获取属性
		for (int i = 0; i < getTypeSize(); i++) {
			this.setValue(i, src.getValue(i) * scale);
		}
		return true;
	}

	/** 属性修改 **/
	@Override
	public boolean changeValues(IPropertys src, double scale) {
		// 遍历获取属性
		for (int i = 0; i < getTypeSize(); i++) {
			changeValue(i, src.getValue(i));
		}
		return true;
	}

	/** 属性设置 **/
	public boolean setValues(IPropertys src) {
		return setValues(src, 1.0);
	}

	/** 属性修改 **/
	public boolean changeValues(IPropertys src) {
		return changeValues(src, 1.0);
	}

	/** 增加属性 **/
	public boolean incValues(IPropertys src) {
		return changeValues(src, 1.0);
	}

	/** 减少属性 **/
	public boolean decValues(IPropertys src) {
		return changeValues(src, -1.0);
	}

	@Override
	public boolean reset() {
		// 遍历
		for (int i = 0; i < getTypeSize(); i++) {
			this.setValue(i, 0);
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append("Propertys [");
		for (int i = 0, psize = getTypeSize(); i < psize; i++) {
			if (i > 0) {
				strBdr.append(", ");
			}
			// 数值
			double total = this.getValue(i);
			strBdr.append(i);
			strBdr.append("=");
			strBdr.append(total);
		}
		strBdr.append("]");

		return strBdr.toString();
	}
}
