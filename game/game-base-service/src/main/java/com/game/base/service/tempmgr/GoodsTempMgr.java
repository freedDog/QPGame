package com.game.base.service.tempmgr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.game.base.service.db.DaoMgr;
import com.game.base.service.db.RedisMgr;
import com.game.base.service.mgr.SharedDataMgr;
import com.game.entity.bean.GoodsValue;
import com.game.entity.dao.GoodsTempDAO;
import com.game.entity.dao.GoodsUtilsDAO;
import com.game.entity.entity.GoodsTempInfo;
import com.game.framework.component.log.Log;
import com.game.framework.utils.struct.result.Result;
import com.game.proto.rp.goods.GoodsTempMsg;
import com.game.utils.DataUtils;

import redis.clients.jedis.ShardedJedis;

/**
 * 商品管理器<br>
 * GoodsTempMgr.java
 * @author JiangBangMing
 * 2019年1月8日下午3:25:08
 */
public class GoodsTempMgr extends SharedDataMgr {

	private static final String redisKey = "goodsTemps";
	private static final String redisKey_count = "goodsCount";

	private static final SharedDataMgr.IRedisHandler<Integer, GoodsTempInfo> handler = new SharedDataMgr.RedisHandler<Integer, GoodsTempInfo>() {
		@Override
		public Class<GoodsTempInfo> getDataClass() {
			return GoodsTempInfo.class;
		}

		@Override
		public Integer toKey(byte[] kdata) {
			String kstr = new String(kdata);
			return Integer.valueOf(kstr);
		}

		@Override
		public byte[] toKeyByte(Integer key) {
			return key.toString().getBytes();
		}
	};

	/** 初始化 **/
	protected static boolean init() {
		return reload();
	}

	/** 重载数据 **/
	public static boolean reload() {
		SharedDataMgr.clearAll(redisKey);
		ShardedJedis jedis = null;
		try {
			jedis = RedisMgr.getJedis();
			// 重新加载数据
			GoodsTempDAO dao = DaoMgr.getInstance().getDao(GoodsTempDAO.class);
			List<GoodsTempInfo> goodsList = dao.getAll();

			for (GoodsTempInfo tempInfo : goodsList) {
				int tempId = tempInfo.getTemplateId();
				// 设置数量
				jedis.hset(redisKey_count, String.valueOf(tempId), String.valueOf(tempInfo.getCount()));
				// 设置模板
				set(redisKey, tempId, tempInfo, handler);
			}

		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		return true;
	}

	/** 对库存进行增值或减值 **/
	public static boolean changeGoodsCount(int templateId, int count) {
		if (count == 0) {
			return true;
		}
		ShardedJedis jedis = RedisMgr.getJedis();
		boolean result = true;
		// 记录剩余量
		long num = 0;
		try {
			// 检测库存数量s
			num = jedis.hincrBy(GoodsTempMgr.getRedisKey(), String.valueOf(templateId), count);
			// num = DataUtils.toLong(jedis.hget(GoodsTempMgr.getRedisKey(), String.valueOf(templateId)));
			// Log.debug(num);
			if (num < 0) {
				result = false;
				jedis.hincrBy(GoodsTempMgr.getRedisKey(), String.valueOf(templateId), -1 * count);
			}
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		// 执行保存
		if (result) {
			GoodsUtilsDAO goodsUtilsDAO = DaoMgr.getInstance().getDao(GoodsUtilsDAO.class);
			// 保存库存到数据库中
			goodsUtilsDAO.changeGoodsCount(templateId, (int) num);
		}
		return result;
	}

	/** 批量对商品库存进行修扣除 **/
	public static boolean changeGoodsCountList(List<GoodsValue> goodsValues, double scale) {
		int gsize = (goodsValues != null) ? goodsValues.size() : 0;
		// 遍历批量修改的库存
		for (int i = 0; i < gsize; i++) {
			GoodsValue goodsValue = goodsValues.get(i);
			if (goodsValue == null) {
				continue;
			}
			int templateId = goodsValue.getTempId();
			int count = (int) (scale * goodsValue.getCount());
			if (count == 0 || templateId <= 0) {
				continue;
			}
			// 判断是否扣除成功
			if (changeGoodsCount(templateId, count)) {
				continue;
			}
			// 当前扣除失败,将上级几个扣除的库存进行返回
			for (int j = i - 1; j >= 0; j--) {
				GoodsValue goodsValue2 = goodsValues.get(j);
				int count0 = (int) (scale * goodsValue.getCount());
				changeGoodsCount(goodsValue2.getTempId(), -count0);
			}
			return false;
		}

		return true;
	}

	/** 获取模板 获取Redis库存 **/
	public static GoodsTempInfo getTempInfo(int templateId) {
		GoodsTempInfo info = new GoodsTempInfo();

		info = get(redisKey, templateId, handler);
		if (info == null) {
			return null;
		}
		info.setCount(hGet(info.getTemplateId()));
		return info;
	}

	/** 获取全部模板 **/
	public static Collection<GoodsTempInfo> getTempInfos() {
		Map<Integer, GoodsTempInfo> all = getAll(redisKey, handler);
		if (all == null) {
			return new ArrayList<>(0);
		}
		// 遍历处理
		for (GoodsTempInfo info : all.values()) {
			int num = hGet(info.getTemplateId());
			info.setCount(num);
		}

		return (all != null) ? all.values() : null;
	}

	/** 分类获取模板 **/
	public static Collection<GoodsTempInfo> getTypeInfo(int type) {
		Collection<GoodsTempInfo> infos = getTempInfos();
		List<GoodsTempInfo> listInfo = new ArrayList<GoodsTempInfo>();
		if (infos.isEmpty()) {
			return null;
		}
		for (GoodsTempInfo info : infos) {
			if (info.getType() == type) {
				listInfo.add(info);
			}
		}

		return listInfo;
	}

	/** 创建一个实物模板消息 **/
	public static GoodsTempMsg createMsg(GoodsTempInfo tempInfo) {
		GoodsTempMsg msg = new GoodsTempMsg();
		if (tempInfo == null) {
			return msg;
		}
		msg.setTemplateId(tempInfo.getTemplateId());
		msg.setCount(tempInfo.getCount());
		msg.setDesc(tempInfo.getDesc());
		msg.setDetailImage(tempInfo.getDetailImage());
		msg.setName(tempInfo.getName());
		msg.setIcon(tempInfo.getIcon());
		msg.setShowImage01(tempInfo.getShowImage01());
		msg.setShowImage02(tempInfo.getShowImage02());
		msg.setType(tempInfo.getType());
		return msg;
	}

	/** 获取库存 **/
	public static int hGet(int templateId) {
		ShardedJedis jedis = null;
		jedis = RedisMgr.getJedis();
		int count = 0;
		try {
			String str = jedis.hget(redisKey_count, String.valueOf(templateId));

			if (str == null) {
				return 0;
			}
			count = Integer.parseInt(str);

		} catch (Exception e) {
			Log.error("Exchange hGet(）" + e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return count;
	}

	/** 设置库存 **/
	public static void hSet(int templateId, int count) {
		ShardedJedis jedis = null;
		jedis = RedisMgr.getJedis();

		try {
			jedis.hset(redisKey_count, String.valueOf(templateId), String.valueOf(count));

		} catch (Exception e) {
			Log.error("Exchange hGet(）" + e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

	}

	public static String getRedisKey() {
		return redisKey_count;
	}

	/** 实物数据保存 **/
	public static boolean save() {
		// 读取实物模板(从redis中读取)
		Collection<GoodsTempInfo> tempInfos = getTempInfos();
		int tsize = (tempInfos != null) ? tempInfos.size() : 0;
		if (tsize <= 0) {
			return true; // 没有数据
		}

		// 整理更新列表
		List<GoodsTempInfo> updates = new ArrayList<>();

		// 更新实物库存
		ShardedJedis jedis = null;
		try {
			jedis = RedisMgr.getJedis();

			// 从redis读取数据，判断是否修改sql中的数据
			for (GoodsTempInfo tempInfo : tempInfos) {
				int id = tempInfo.getTemplateId();
				// 读取数量
				String str = jedis.hget(redisKey_count, String.valueOf(id));
				if (str == null) {
					continue;
				}
				// 更新数量
				int nowCount = DataUtils.toInt(str);
				tempInfo.setCount(nowCount);

				// 判断是否修改数据了
				if (!tempInfo.isUpdate()) {
					continue; // 没变化
				}
				updates.add(tempInfo);
				tempInfo.commit();
			}
		} catch (Exception e) {
			Log.error("GoodsTempMgr模板保存错误! " + e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		// 执行保存
		int usize = (updates != null) ? updates.size() : 0;
		if (usize > 0) {
			GoodsTempDAO dao = DaoMgr.getInstance().getDao(GoodsTempDAO.class);
			dao.insertOrUpdate(updates);
		}

		return true;
	}

	public static Result checkTempInfo(GoodsTempInfo tempInfo) {

		// 检测名字
		if (tempInfo.getName() == null) {
			return Result.error("名字不能为空 ! ");
		}

		// 检测类型
		if (tempInfo.getType() == 0) {
			return Result.error("类型不能为空 ! ");
		}

		// 检测成本价
		if (tempInfo.getCost() == 0) {
			return Result.error("成本价不能为0");
		}

		// 检测市场价
		if (tempInfo.getMarket() == 0) {
			return Result.error("市场价不能为0");
		}
		// 检测库存
		if (tempInfo.getCount() == 0) {
			return Result.error("库存不能为0");
		}

		return Result.succeed();
	}

}

