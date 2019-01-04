package com.game.framework.framework.property;

import com.game.framework.component.ChangeCounter;

/**
 * 多种符合属性集合<br>
 * 1. 支持按照valueSize创建多种属性加成<br>
 * 2. 支持GroupPropertys的单属性符合处理<br>
 * PS: <br>
 * value列表代表不同的属性加成. 例如: 基础数值, 装备加成, 称号加成等<br>
 * group代表计算组合. 例如: 固定值, 百分比加成<br>
 * total为汇总的最终值<br>
 * MultiPropertys.java
 * 
 * @author JiangBangMing 2019年1月3日下午3:08:19
 */
public class MultiPropertys extends Propertys {
	protected Propertys total; // 汇总数据
	protected GroupPropertys[] values; // 属性加成集合

	public final ChangeCounter propertyCounter; // 属性更新器

	public MultiPropertys(int valueSize, int groupSize, int typeSize) {
		// 创建属性
		total = createPropertys(typeSize);
		values = new GroupPropertys[valueSize];
		for (int i = 0; i < valueSize; i++) {
			values[i] = createGroupPropertys(groupSize, typeSize);
		}
		// 创建计数器
		propertyCounter = new ChangeCounter() {
			@Override
			protected void onChange() {
				updateProperty();
			}
		};
	}

	/** 创建汇总集合 **/
	protected BasePropertys createPropertys(int typeSize) {
		return new BasePropertys(typeSize);
	}

	/** 创建子集合 **/
	protected ChildGroupPropertys createGroupPropertys(int groupSize, int typeSize) {
		return new ChildGroupPropertys(groupSize, typeSize);
	}

	@Override
	public int getTypeSize() {
		return total.getTypeSize();
	}

	public int getValueSize() {
		return (values != null) ? values.length : 0;
	}

	/** 汇总结果输出 **/
	@Override
	public double getValue(int index) {
		return total.getValue(index);
	}

	@Override
	public void setValue(int type, double value) {
		values[0].getPropertys(0).setValue(type, value);
	}

	/** 获取固定值加成 属性集合 **/
	public <T extends Enum<?>> GroupPropertys getPropertys(T value) {
		return getPropertys(value.ordinal());
	}

	/** 获取固定值加成 属性集合 **/
	public GroupPropertys getPropertys(int valueIndex) {
		return values[valueIndex];
	}

	/** 更新属性和战斗力 **/
	protected void updateProperty() {
		update();
	}

	/** 更新汇总数据 **/
	protected void update() {
		// 遍历属性
		for (int i = 0; i < getTypeSize(); i++) {
			double totalValue = getTotalValue(i);
			this.total.setValue(i, totalValue);
		}
		// 触发属性更新
		propertyCounter.change();
	}

	/** 获取一个汇总数据 **/
	protected double getTotalValue(int index) {
		// 计算固定值加成
		double base = 0;
		for (int i = 0; i < values.length; i++) {
			Propertys basePropertys = values[i].getPropertys(0);
			base += basePropertys.getValue(index);
		}

		// 固定值为0, 后面的百分比没计算必要
		if (base <= 0) {
			return 0;
		}

		// 百分比计算
		double rate = 0;
		for (int i = 0; i < values.length; i++) {
			Propertys ratePropertys = values[i].getPropertys(1);
			rate += ratePropertys.getValue(index);
		}
		return base + rate * base;
	}

	/** 重设属性 **/
	public boolean reset() {
		try {
			propertyCounter.beginChange();
			// 遍历
			for (int i = 0; i < values.length; i++) {
				values[i].reset();
			}
		} finally {
			propertyCounter.commitChange();
		}
		return true;
	}

	/** 子属性集合, 数据修改触发父集合变化 **/
	protected class ChildGroupPropertys extends GroupPropertys {
		public final ChangeCounter propertyCounter; // 属性更新器

		public ChildGroupPropertys(int groupSize, int propSize) {
			super(groupSize, propSize);
			propertyCounter = new ChangeCounter() {
				@Override
				protected void onChange() {
					updateProperty();
				}
			};
		}

		/** 更新属性和战斗力 **/
		protected void updateProperty() {
			MultiPropertys.this.propertyCounter.change(); // 触发汇总更新
		}

		@Override
		protected Propertys createPropertys(int groupIndex, int propSize) {
			return new ChildPropertys(propSize);
		}

		/** 子属性 **/
		protected class ChildPropertys extends BasePropertys {
			public ChildPropertys(int propSize) {
				super(propSize);
			}

			@Override
			public void setValue(int index, double value) {
				super.setValue(index, value);
				propertyCounter.change();
			}

			@Override
			public void setValues(double v) {
				try {
					propertyCounter.beginChange();
					super.setValues(v);
				} finally {
					propertyCounter.commitChange(false);
				}

			}

			@Override
			public boolean setValues(IPropertys src) {
				try {
					propertyCounter.beginChange();
					return super.setValues(src);
				} finally {
					propertyCounter.commitChange(false);
				}

			}

			@Override
			public boolean changeValues(IPropertys src, double scale) {
				try {
					propertyCounter.beginChange();
					return super.changeValues(src, scale);
				} finally {
					propertyCounter.commitChange(false);
				}

			}

			@Override
			public boolean changeValues(double value) {
				try {
					propertyCounter.beginChange();
					return super.changeValues(value);
				} finally {
					propertyCounter.commitChange(false);
				}

			}

			@Override
			public boolean reset() {
				try {
					propertyCounter.beginChange();
					return super.reset();
				} finally {
					propertyCounter.commitChange(false);
				}
			}
		}
	}
}
