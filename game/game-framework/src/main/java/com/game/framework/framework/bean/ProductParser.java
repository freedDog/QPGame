package com.game.framework.framework.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.game.framework.component.log.Log;
import com.game.framework.utils.StringUtils;


/**
 * 产品解析器
 * ProductParser.java
 * @author JiangBangMing
 * 2019年1月3日下午2:40:53
 */
public abstract class ProductParser<P> {
	protected String regexA; // 用于解析处理
	protected String regexB;

	private String regexA_; // 用于toString处理
	private String regexB_;

	public ProductParser() {
		this("@", "\\|");
	}

	public ProductParser(String regexA, String regexB) {
		this.regexA = regexA;
		this.regexB = regexB;
		regexA_ = regexA.replaceAll("\\\\", "");
		regexB_ = regexB.replaceAll("\\\\", "");
	}

	/** 加密成字符串 **/
	protected String toEncode(P product) {
		return toEncode(product, 1.0);
	}

	/** 加密成字符串 **/
	protected abstract String toEncode(P product, double scale);

	/** 从字符串中解析出数据 **/
	protected abstract P toDecode(String str);

	/** 是否是同类型的资源 **/
	public abstract boolean isSameRes(P src, P dst);

	/** 合并数据 **/
	protected abstract boolean merge(P src, P other);

	/** 复制数据 **/
	public abstract P copy(P src);

	/** 复制数据 **/
	public List<P> copy(List<P> list) {
		// 空处理
		if (list == null) {
			return null;
		}
		// 遍历处理
		List<P> out = new ArrayList<>(list.size());
		for (P p : list) {
			P c = copy(p);
			if (c == null) {
				throw new RuntimeException("复制数据失败!" + p);
			}
			out.add(c);
		}
		return out;
	}

	/** 转为字符串 **/
	public String toString(P product) {
		return toEncode(product);
	}

	/** 转为字符串 **/
	public String toString(List<P> products) {
		return toString(products, 1.0);
	}

	/** 转为字符串 **/
	public String toStringByList(List<List<P>> productList) {
		return toStringByList(productList, 1.0);
	}

	/** 转为字符串 **/
	public String toStringByList(List<List<P>> productList, double scale) {
		// 遍历处理
		StringBuilder strBdr = new StringBuilder();
		int psize = (productList != null) ? productList.size() : 0;
		for (int i = 0; i < psize; i++) {
			if (i > 0) {
				strBdr.append(regexA_);
			}
			strBdr.append(toString(productList.get(i), scale));
		}
		return strBdr.toString();
	}

	/** 转为字符串 **/
	public String toString(P[] products, double scale) {
		return toString((products != null) ? Arrays.asList(products) : null, scale);
	}

	/** 转为字符串 **/
	public String toString(List<P> products, double scale) {
		// 遍历处理
		StringBuilder strBdr = new StringBuilder();
		int psize = (products != null) ? products.size() : 0;
		for (int i = 0; i < psize; i++) {
			// 处理分割
			if (i > 0) {
				strBdr.append(regexB_);
			}
			// 处理数量变化
			P product = products.get(i);
			strBdr.append(toEncode(product, scale));
		}
		return strBdr.toString();
	}

	/** 检测格式是否符合(空也是符合的) **/
	public boolean checkString(String str) {
		try {
			// 解析没报错就代表成功
			toProducts(str);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	/** 拆分成Product, 格式: type,id,count|type,id,count **/
	public List<P> toProducts(String str) {
		return toProducts(str, true);
	}

	/** 拆分成Product, 格式: type,id,count|type,id,count **/
	public List<P> toProducts(String str, boolean merge) {
		// 空判断
		if (StringUtils.isEmpty(str)) {
			return new ArrayList<P>(); // 空就是没有
		}

		// 截取拆分
		String[] values = str.split(regexB);
		int vsize = (values != null) ? values.length : 0;
		if (vsize <= 0) {
			return new ArrayList<P>(); // 空就是没有
		}

		// 遍历生成
		List<P> ps = new ArrayList<>(vsize);
		for (int i = 0; i < vsize; i++) {
			String valueStr = values[i];
			if (StringUtils.isEmpty(valueStr)) {
				continue;
			}
			// 创建产品
			P product = toDecode(valueStr);
			if (product == null) {
				throw new RuntimeException("解析错误! str=" + str);
			}
			ps.add(product);
		}

		// 执行合并
		if (merge && vsize >= 2) {
			if (!merge(ps)) {
				throw new RuntimeException("整合数据错误!");
			}
		}
		return ps;
	}

	/** 是否存在相同类型的资源 **/
	private int getIndexBySameRes(P check, List<P> products, int offset) {
		// 检测数量
		int vsize = (products != null) ? products.size() : 0;
		if (vsize <= 0) {
			return -1;
		}
		// 遍历检测
		for (int i = offset; i < vsize; i++) {
			P p = products.get(i);
			if (!isSameRes(p, check)) {
				continue;
			}
			return i;
		}
		return -1;
	}

	/** 是否存在相同类型的资源 **/
	public boolean isSameRes(P check, List<P> products, int offset) {
		return getIndexBySameRes(check, products, offset) >= 0;
	}

	/** 整合并复制一份(如果没有可以合并的) **/
	public List<P> mergeAndCopy(List<P> products) {
		// 检测数量
		int vsize = (products != null) ? products.size() : 0;
		if (vsize <= 1) {
			return products; // 1个数据不合并
		}
		// 遍历检测是否存在重复的数据
		boolean hasSame = false;
		for (int i = 0; i < vsize; i++) {
			P product = products.get(i);
			int sameIndex = getIndexBySameRes(product, products, i + 1);
			if (sameIndex >= 0) {
				hasSame = true; // 存在相同的
				break;
			}
		}
		if (!hasSame) {
			return products; // 没有需要合并的
		}

		// 复制执行合并
		List<P> clist = copy(products);
		if (!merge(clist)) {
			return null; // 合并错误
		}
		return clist;
	}

	/** 整合相同类型的资源 **/
	public boolean merge(List<P> products) {
		// 检测数量
		int vsize = (products != null) ? products.size() : 0;
		if (vsize <= 1) {
			return true;
		}

		// 遍历检测是否存在重复的数据
		for (int i = 0; i < vsize; i++) {
			P product = products.get(i);
			// 遍历检测是否相同
			int offset = i + 1;
			while (true) {
				// 获取相同
				int sameIndex = getIndexBySameRes(product, products, offset);
				if (sameIndex < 0) {
					break; // 没有相同的内容
				}
				// 存在相同
				P check = products.get(sameIndex);
				vsize--; // 数量减少
				products.remove(sameIndex);
				offset = sameIndex; //移除后, 从当前位置开始找.

				// 相同资源
				if (!merge(product, check)) {
					return false;
				}
			}
		}
		return true;
	}

	/** 解析产品 **/
	public P toProduct(String str) {
		List<P> list = toProducts(str);
		int lsize = (list != null) ? list.size() : 0;
		return (lsize > 0) ? list.get(0) : null;
	}

	/** 解析产品 **/
	public List<List<P>> toProductList(String str) {
		// 空判断
		if (StringUtils.isEmpty(str)) {
			return new ArrayList<List<P>>();
		}
		// 截取拆分
		try {
			// 拆分字符串
			String[] strs = str.split(regexA);
			int ssize = (strs != null) ? strs.length : 0;
			// 遍历
			List<List<P>> list = new ArrayList<>(ssize);
			for (int i = 0; i < ssize; i++) {
				List<P> plist = toProducts(strs[i]);
				list.add(plist);
			}
			return list;
		} catch (Exception e) {
			Log.error("拆分字符串异常, str:" + str, e);
		}
		return null;
	}
}
