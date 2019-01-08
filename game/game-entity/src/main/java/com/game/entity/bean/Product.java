package com.game.entity.bean;

import java.util.ArrayList;
import java.util.List;

import com.game.framework.component.log.Log;
import com.game.framework.framework.bean.IProduct;
import com.game.framework.utils.StringUtils;

/**
 * 产品(商品)<br>
 * 用于表示游戏中任意的一种资源物品
 * Product.java
 * @author JiangBangMing
 * 2019年1月8日上午11:41:09
 */
public class Product implements IProduct, Cloneable {
	public static final ProductParser parser = new ProductParser();
	protected int type; // 产品类型
	protected int id; // 产品Id
	protected long count; // 产品数量

	protected Product() {
	}

	public Product(int type, int id, long count) {
		this.type = type;
		this.id = id;
		this.count = count;
	}

	/** 数据放大 **/
	public static <P extends Product> List<P> copys(List<P> products) {
		List<P> copys = new ArrayList<>();
		int psize = (products != null) ? products.size() : 0;
		for (int i = 0; i < psize; i++) {
			P product = products.get(i);
			try {
				@SuppressWarnings("unchecked")
				P copy = (P) product.clone();
				copys.add(copy);
			} catch (Exception e) {
				Log.error("复制Product错误!?", e);
			}
		}
		return copys;
	}

	/** 数据放大 **/
	public static void mult(List<Product> products, double scale) {
		int psize = (products != null) ? products.size() : 0;
		for (int i = 0; i < psize; i++) {
			Product product = products.get(i);
			product.setCount((int) (product.getCount() * scale));
		}
	}

	/** 乘法(数量放大N倍, 创建新对象) **/
	public Product mult(double scale) {
		return new Product(type, id, (int) (scale * count));
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public long getCount() {
		return count;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCount(long count) {
		this.count = count;
	}

	/** 检测格式是否符合(空也是符合的) **/
	public static boolean checkProductString(String str) {
		return parser.checkString(str);
	}

	/** 转成字符串 **/
	public static String toString(Product product) {
		return parser.toString(product);
	}

	/** 转成字符串 **/
	public static String toString(int type, int id, long count) {
		StringBuilder strBdr = new StringBuilder();
		strBdr.append(type);
		strBdr.append(",");
		strBdr.append(id);
		strBdr.append(",");
		strBdr.append(count);
		return strBdr.toString();
	}

	/** 转成字符串 **/
	public static String toString(List<Product> products) {
		return parser.toString(products);
	}

	/** 拆分成Product, 格式: type,id,count|type,id,count **/
	public static Product[] toProductArray(String str) {
		List<Product> ps = toProducts(str);
		return (ps != null) ? ps.toArray(new Product[0]) : null;
	}

	/** 拆分成Product, 格式: type,id,count|type,id,count **/
	public static List<Product> toProducts(String str) {
		return parser.toProducts(str);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) count;
		result = prime * result + id;
		result = prime * result + type;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Product other = (Product) obj;
		if (count != other.count) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return parser.toString(this);
	}

	/** 产品解析器 **/
	public static class ProductParser extends com.game.framework.framework.bean.ProductParser<Product> {

		@Override
		protected boolean merge(Product src, Product other) {
			src.setCount(src.getCount() + other.getCount());
			return true;
		}

		@Override
		public boolean isSameRes(Product src, Product dst) {
			if (src == null || dst == null) {
				return false;
			}
			return (src.getType() == dst.getType()) && (src.getId() == dst.getId());
		}

		@Override
		protected Product toDecode(String str) {
			int[] values = StringUtils.splitToInt(str, ",");
			int vsize = (values != null) ? values.length : 0;
			if (vsize < 3) {
				return null;
			}
			// 创建对象
			Product product = new Product(values[0], values[1], values[2]);
			return product;
		}

		@Override
		protected String toEncode(Product product, double scale) {
			long count = (long) (product.getCount() * scale);
			// 输出字符串
			StringBuilder strBdr = new StringBuilder();
			strBdr.append(product.getType());
			strBdr.append(",");
			strBdr.append(product.getId());
			strBdr.append(",");
			strBdr.append(count);
			return strBdr.toString();
		}

		@Override
		public Product copy(Product src) {
			Product c = new Product();
			c.setId(src.getId());
			c.setType(src.getType());
			c.setCount(src.getCount());
			return c;
		}

	}

}
