package com.game.framework.framework.property;

import java.util.Arrays;

/**
 * 组合属性模板<br>
 * 默认使用0数据的模板 GroupPropertys.java
 * 
 * @author JiangBangMing 2019年1月3日下午3:07:30
 */
public class GroupPropertys extends Propertys {
	protected Propertys[] groups;

	public GroupPropertys(int groupSize, int propSize) {
		groups = new Propertys[groupSize];
		for (int i = 0; i < groupSize; i++) {
			groups[i] = createPropertys(i, propSize);
		}
	}

	/** 创建子属性集合 **/
	protected Propertys createPropertys(int groupIndex, int propSize) {
		return new BasePropertys(propSize);
	}

	@Override
	public boolean reset() {
		// 遍历
		for (int i = 0; i < groups.length; i++) {
			groups[i].reset();
		}
		return true;
	}

	/** 获取组合属性集合 **/
	public Propertys getPropertys(int groupIndex) {
		return groups[groupIndex];
	}

	/** 获取组合属性集合 **/
	public <T extends Enum<?>> Propertys getPropertys(T groupIndex) {
		return getPropertys(groupIndex.ordinal());
	}

	/** 集合数量 **/
	public int getGroupSize() {
		return (groups != null) ? groups.length : 0;
	}

	@Override
	public String toString() {
		return Arrays.toString(groups);
	}

	@Override
	public double getValue(int propType) {
		return groups[0].getValue(propType);
	}

	@Override
	public void setValue(int propType, double value) {
		groups[0].setValue(propType, value);
	}

	@Override
	public int getTypeSize() {
		return groups[0].getTypeSize();
	}

	@Override
	public boolean setValues(IPropertys src) {
		// 检测是否是同类型对象
		if (!GroupPropertys.class.isInstance(src)) {
			return super.setValues(src);
		}
		// 遍历所有集合赋值
		GroupPropertys target = (GroupPropertys) src;
		int gsize = Math.min(target.getGroupSize(), this.getGroupSize());
		for (int i = 0; i < gsize; i++) {
			this.getPropertys(i).setValues(target.getPropertys(i));
		}
		return true;
	}

	@Override
	public boolean changeValues(IPropertys src, double scale) {
		// 检测是否是同类型对象
		if (!GroupPropertys.class.isInstance(src)) {
			return super.changeValues(src, scale);
		}
		// 遍历所有集合赋值
		GroupPropertys target = (GroupPropertys) src;
		int gsize = Math.min(target.getGroupSize(), this.getGroupSize());
		for (int i = 0; i < gsize; i++) {
			this.getPropertys(i).changeValues(target.getPropertys(i), scale);
		}
		return true;
	}
}
