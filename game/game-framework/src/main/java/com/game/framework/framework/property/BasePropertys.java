package com.game.framework.framework.property;

/**
 * 基础属性集合<br>
 * 用于模板或者简单的属性模板 BasePropertys.java
 * 
 * @author JiangBangMing 2019年1月3日下午3:07:00
 */
public class BasePropertys extends Propertys {
	protected double[] props; // 属性集合

	public BasePropertys(int typeSize) {
		// 创建属性
		props = new double[typeSize];
		for (int i = 0, psize = props.length; i < psize; i++) {
			props[i] = 0.0;
		}
	}

	public BasePropertys(BasePropertys propertys) {
		this(propertys.getTypeSize());
		// 复制属性
		if (propertys != null) {
			int psize = (propertys.props != null) ? propertys.props.length : 0;
			for (int i = 0; i < psize; i++) {
				props[i] = propertys.props[i];
			}
		}
	}

	@Override
	public double getValue(int index) {
		return props[index];
	}

	@Override
	public void setValue(int index, double value) {
		props[index] = value;
	}

	@Override
	public int getTypeSize() {
		return props.length;
	}

	/******************* 静态 ********************/

	// /** 创建一个静态对象(复制一份) **/
	// public static Propertys createConstPropertys(Propertys propertys)
	// {
	// return new ConstPropertys(propertys);
	// }
	//
	// /** 静态属性 **/
	// static class ConstPropertys extends Propertys
	// {
	// public ConstPropertys(Propertys propertys)
	// {
	// super(propertys);
	// }
	//
	// @Override
	// public boolean setValues(PropValueType type, double v)
	// {
	// throw new RuntimeException("骚年, 这个静态对象是不能改的!");
	// }
	//
	// @Override
	// public boolean setValues(PropValueType type, Propertys src)
	// {
	// throw new RuntimeException("骚年, 这个静态对象是不能改的!");
	// }
	//
	// @Override
	// public boolean changeValues(PropValueType type, Propertys src, double
	// scale)
	// {
	// throw new RuntimeException("骚年, 这个静态对象是不能改的!");
	// }
	// }
	//
	// /** 属性全输出 **/
	// public static String propertyString(Propertys propertys)
	// {
	// StringBuilder strBdr = new StringBuilder();
	// strBdr.append("Propertys [\n");
	// for (int i = 0, psize = propertys.propertyGroup.length; i < psize; i++)
	// {
	// if (i > 0)
	// {
	// strBdr.append(", \n");
	// }
	// // 数值
	// PropType propType = PropType.values()[i];
	// strBdr.append(propType.name());
	// strBdr.append("=");
	// strBdr.append(propertys.propertyGroup[i].toString());
	// }
	// strBdr.append("\n]");
	//
	// return strBdr.toString();
	// }

}
