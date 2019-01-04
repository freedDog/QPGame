package com.game.base.service.cmd;

import java.lang.reflect.Method;

import com.game.framework.component.log.Log;
import com.game.framework.component.method.MethodInvoker;
import com.game.proto.msg.Message;
import com.game.proto.msg.RpMessage;

/**
 * 函数执行器<br>
 * CmdInvoker.java
 * @author JiangBangMing
 * 2019年1月4日下午4:28:44
 */
public class CmdInvoker extends MethodInvoker<Object[]> {

	public CmdInvoker(Object obj, Method method) {
		super(obj, method);
	}

	/** 从参数中获取符合这个类型的第一个对象 **/
	@SuppressWarnings("unchecked")
	public static <T> T getArgs(Object[] objs, Class<T> clazz) {
		for (Object obj : objs) {
			if (clazz.isInstance(obj)) {
				return (T) obj;
			}
		}
		return null;
	}

	/**
	 * 参数解析, 自动通过objs找出最为符合的数据<br>
	 * 目前暂时没有用处, 因为改用接口直接按需获取了.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] parseArguments(Object[] objs) throws Exception {
		// 判断解析
		if (obj == null) {
			return new Object[0]; // 空参数
		}
		// 获取消息体
		Message msg = getArgs(objs, Message.class);

		// 获取函数参数
		Class<?>[] parameterTypes = method.getParameterTypes();
		int psize = (parameterTypes != null) ? parameterTypes.length : 0;

		// 转化参数
		Object[] args = new Object[psize];
		for (int i = 0; i < psize; i++) {
			Class<?> pc = parameterTypes[i];
			// 判断参数类型
			if (RpMessage.class.isAssignableFrom(pc)) {
				args[i] = (msg != null) ? msg.getMessage((Class<? extends RpMessage>) pc) : null;
			}
			// else if (pc == Player.class)
			// {
			// long playerId = (msg != null) ? msg.getPlayerId() : 0;
			// if (playerId <= 0)
			// {
			// Log.error("玩家Id为空, 不能获取玩家信息. method=" + method, true);
			// return null;
			// }
			// // 获取玩家信息
			// // Player player =
			// // PlayerMgr.getInstance().getFromCache(playerId);
			// Player player = PlayerMgr.getInstance().get(playerId);
			// if (player == null)
			// {
			// Log.error("玩家数据不在内存中. method=" + method, true);
			// return null;
			// }
			// args[i] = player;
			// }
			else {
				// 通用处理
				args[i] = getArgs(objs, pc);
			}
			// 空检测
			if (args[i] == null) {
				Log.error("参数获取失败: method=" + method + " i=" + i + " c=" + pc, true);
				return null;
			}
		}
		return args;
	}

}
