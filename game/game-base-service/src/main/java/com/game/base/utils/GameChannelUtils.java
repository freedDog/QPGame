package com.game.base.utils;

import java.util.List;

import com.game.base.service.config.ServerConfig;
import com.game.base.service.mailbox.MailBox;
import com.game.base.service.mgr.GameChannelMgr;
import com.game.base.service.module.ModuleName;
import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.utils.struct.result.Result;

/**
 * 模块连接工具
 * GameChannelUtils.java
 * @author JiangBangMing
 * 2019年1月8日下午5:59:54
 */
public class GameChannelUtils {

	/** 遍历执行所有模块(中间出现一个错误都会按照错误处理, 但是所有都会继续执行) **/
	public static <T> Result executeAllByModule(ModuleName moduleName, Class<T> serviceClass, IChannelHandler<T> handler) {
		Result result = Result.succeed();
		List<ProxyChannel> channels = GameChannelMgr.getChannelsByModule(moduleName);
		int csize = (channels != null) ? channels.size() : 0;
		for (int i = 0; i < csize; i++) {
			ProxyChannel proxyChannel = channels.get(i);
			try {
				T service = proxyChannel.createImpl(serviceClass);
				Result r = handler.execute(service);
				if (r == null || !r.isSucceed()) {
					result = r;
				}
			} catch (Exception e) {
				result = Result.error("远程执行错误!" + proxyChannel + " " + e.toString());
				Log.error("远程执行错误!" + proxyChannel, e);
			}
		}
		// 处理结果
		return result;
	}

	/** 遍历执行所有模块(中间出现一个错误都会按照错误处理, 但是所有都会继续执行) **/
	public static <T> Result executeAllByPlayerIds(long[] playerIds, ModuleName moduleName, Class<T> serviceClass, IChannelHandler<T> handler) {
		Result result = Result.succeed();
		// 遍历playerId
		int psize = (playerIds != null) ? playerIds.length : 0;
		for (int i = 0; i < psize; i++) {
			long playerId = playerIds[i];
			Result r = executeByPlayerId(playerId, moduleName, serviceClass, handler);
			if (!r.isSucceed()) {
				result = r;
			}
		}
		return result;
	}

	/** 遍历执行所有模块(中间出现一个错误都会按照错误处理, 但是所有都会继续执行) **/
	public static <T> Result executeByPlayerId(long playerId, ModuleName moduleName, Class<T> serviceClass, IChannelHandler<T> handler) {
		// 获取玩家所在地址
		ServerConfig config = MailBox.get(playerId, ModuleName.CORE);
		if (config == null) {
			return Result.error("玩家不在内存!");
		}
		ProxyChannel proxyChannel = GameChannelMgr.getChannel(config);
		if (proxyChannel == null) {
			return Result.error("玩家所在连接不存在!");
		}

		// 处理
		try {
			T service = proxyChannel.createImpl(serviceClass);
			return handler.execute(service);
		} catch (Exception e) {
			Log.error("远程执行错误!" + proxyChannel, e);
			return Result.error("远程执行错误!" + proxyChannel + " " + e.toString());
		}
	}

	/** 中控处理接口 **/
	public interface IChannelHandler<T> {
		/** 执行 **/
		Result execute(T service);
	}
}
