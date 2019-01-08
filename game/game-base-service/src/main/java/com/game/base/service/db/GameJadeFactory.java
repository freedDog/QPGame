package com.game.base.service.db;

import java.lang.reflect.Method;
import java.util.List;

import javax.sql.DataSource;

import com.game.framework.jdbc.JadeFactory;
import com.game.framework.utils.data.UpdateData;

/**
 * game的JDBC工厂<br>
 * 在查询Info数据时, 默认把数据修改标记重置, 避免下一次保存.
 * GameJadeFactory.java
 * @author JiangBangMing
 * 2019年1月8日上午10:14:42
 */
public class GameJadeFactory extends JadeFactory {
	public GameJadeFactory(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	protected Object DAOInvoke(Object proxy, Method method, Object[] args) throws Exception {
		// 返回数据处理
		Object retObj = super.DAOInvoke(proxy, method, args);
		if (retObj == null) {
			return retObj;
		}

		// 如果数据是个UpdateData
		if (UpdateData.class.isInstance(retObj)) {
			((UpdateData) retObj).commit(); // 刚刚读取出来数据, 不用标记修改.
		} else if (List.class.isInstance(retObj)) {
			// 列表遍历
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>) retObj;
			int lsize = (list != null) ? list.size() : 0;
			for (int i = 0; i < lsize; i++) {
				Object item = list.get(i);
				if (UpdateData.class.isInstance(item)) {
					((UpdateData) item).commit(); // 标记完成
				}
			}
		}

		return retObj;
	}

}
