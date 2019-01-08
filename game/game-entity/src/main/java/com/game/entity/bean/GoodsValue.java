package com.game.entity.bean;

import com.game.framework.utils.StringUtils;

/**
 * 实物对象
 * GoodsValue.java
 * @author JiangBangMing
 * 2019年1月8日下午3:30:41
 */
public class GoodsValue {
	/** 解析器 **/
	public static final GoodsParser parser = new GoodsParser();

	protected int tempId;
	protected int count;

	public GoodsValue(int tempId, int count) {
		this.tempId = tempId;
		this.count = count;
	}

	public GoodsValue() {
	}

	public int getTempId() {
		return tempId;
	}

	public void setTempId(int tempId) {
		this.tempId = tempId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return parser.toString(this);
	}

	/** 解析器 **/
	public static class GoodsParser extends com.game.framework.framework.bean.ProductParser<GoodsValue> {
		@Override
		protected boolean merge(GoodsValue src, GoodsValue other) {
			src.setCount(src.getCount() + other.getCount());
			return true;
		}

		@Override
		public boolean isSameRes(GoodsValue src, GoodsValue dst) {
			if (src == null || dst == null) {
				return false;
			}
			return src.getTempId() == dst.getTempId();
		}

		@Override
		protected GoodsValue toDecode(String str) {
			int[] values = StringUtils.splitToInt(str, ",");
			int vsize = (values != null) ? values.length : 0;
			if (vsize != 2) {
				return null;
			}
			// 创建对象
			GoodsValue product = new GoodsValue(values[0], values[1]);
			return product;
		}

		@Override
		protected String toEncode(GoodsValue product, double scale) {
			long count = (long) (product.getCount() * scale);
			// 输出字符串
			StringBuilder strBdr = new StringBuilder();
			strBdr.append(product.getTempId());
			strBdr.append(",");
			strBdr.append(count);
			return strBdr.toString();
		}

		@Override
		public GoodsValue copy(GoodsValue src) {
			return new GoodsValue(src.tempId, src.count);
		}

	}
}
