package com.game.base.service.module;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.game.base.service.cmd.Cmd;
import com.game.base.service.cmd.Cmd.CmdFunc;
import com.game.base.service.cmd.CmdInvoker;
import com.game.base.service.cmd.CmdMgr;
import com.game.base.service.config.ModuleConfig;
import com.game.base.service.constant.MsgType;
import com.game.base.service.constant.TextTempId;
import com.game.base.service.http.HttpHandler;
import com.game.base.service.http.HttpMgr;
import com.game.base.service.http.HttpMgr.HttpUrl;
import com.game.base.service.http.IHttpHandler;
import com.game.base.service.language.LanguageSet;
import com.game.base.service.mgr.RouteMgr;
import com.game.base.service.player.Player;
import com.game.base.service.server.App;
import com.game.base.utils.DataUtils;
import com.game.entity.http.bean.HttpResult;
import com.game.framework.component.log.Log;
import com.game.framework.component.method.MethodInvoker.IParseArguments;
import com.game.framework.component.method.MethodMgr;
import com.game.framework.framework.component.ComponentMgr;
import com.game.framework.framework.mgr.ServiceMgr;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.ProxyService;
import com.game.framework.framework.rpc.RpcCallback;
import com.game.framework.framework.xml.XmlNode;
import com.game.framework.utils.ObjectUtils;
import com.game.framework.utils.StringUtils;
import com.game.proto.msg.Message;
import com.game.proto.msg.RpMessage;
import com.game.proto.protocol.Protocol;
import com.game.proto.rp.text.TextMsg;

/**
 * 逻辑服模块
 * Module.java
 * @author JiangBangMing
 * 2019年1月4日下午3:55:35
 */
public abstract class Module extends ComponentMgr implements IParseArguments<Object[]> {
	/** 模块配置数据节点 **/
	protected XmlNode moduleNode;
	/** 模块配置 **/
	protected ModuleConfig config;

	protected Module(XmlNode moduleNode) {
		this.moduleNode = moduleNode;

		// 创建配置
		createConfig(null);
	}

	/** 获取模块名 **/
	public abstract ModuleName getModuleName();

	/** 创建模块配置 **/
	protected boolean createConfig(int[] gameZoneIds) {
		config = new ModuleConfig(getModuleName().name(), App.getInstance().getConfig(), gameZoneIds, 0);
		return true;
	}

	/** 获取模块配置 **/
	public final ModuleConfig getConfig() {
		return config;
	}

	/** 模块初始化 **/
	public boolean init() {
		String packetName = StringUtils.getPacketName(this.getClass());
		boolean result = true;
		final List<Object> errorObjs = new ArrayList<>();
		// 注册 http url
		result = HttpMgr.register(packetName, new HttpMgr.IFilter() {
			@Override
			public boolean filter(HttpUrl ha, Class<?> clazz) {
				String url = ha.value();
				ModuleName moduleName = Module.this.getModuleName();
				if (!moduleName.checkUrl(url)) {
					Log.error("http接口url不符合模块区间: " + moduleName + " " + Arrays.toString(moduleName.getModuleUrls()) + " -> " + url + " clazz" + clazz);
					errorObjs.add(clazz); // 标记错误
					return false;
				}
				return true;
			}
		});
		if (!result || errorObjs.size() > 0) {
			Log.error("注册模块url失败! " + packetName);
			return false;
		}

		// 注册cmd
		result = CmdMgr.register(packetName, new MethodMgr.IFilter<Cmd, Cmd.CmdFunc>() {
			@Override
			public boolean filter(Cmd ca, CmdFunc ma, Class<?> clazz, Method method) {
				int code = ma.value();
				ModuleName moduleName = Module.this.getModuleName();
				if (!moduleName.checkCode(code)) {
					Log.error("cmd code不符合模块区间: " + moduleName + " " + Arrays.toString(moduleName.getCodes()) + " -> " + code + " " + clazz);
					errorObjs.add(clazz); // 标记错误
					return false;
				}
				return true;
			}
		});
		if (!result || errorObjs.size() > 0) {
			Log.error("注册模块cmd失败! " + packetName);
			return false;
		}

		// 注册rpc模块
		ProxyService service = ProxyService.getInstance();
		if (service != null && !service.register(packetName, null)) {
			Log.error("注册模块rpc失败! " + packetName);
			return false;
		}
		return true;
	}

	/** 模块关闭 **/
	public abstract void destroy();

	/** 模块指令 **/
	public void onModuleCommon(String cmd, Map<String, String> params) {

	}

	/** 模块socket消息接口 **/
	public void onModuleMessage(final Message packet, final ProxyChannel channel) {
//		if (packet.getCode() != ServerProtocol.S_SYNC_TIME)
//			Log.info("onModuleMessage: " + packet);
		final short code = packet.getCode(); // 消息码
		Runnable r = new Runnable() {
			@Override
			public void run() {
				// 检测连接是否还激活
				if (!channel.isConnect()) {
					Log.warn("连接断开! channel=" + channel + " msg=" + packet);
					return;
				}
				// 执行消息
				Object[] params = null;
				try {
					// 根据code执行消息
					params = new Object[] { packet, channel };
					CmdMgr.execute(code, params, Module.this);
				} catch (Exception e) {
					Log.error("来自" + channel + "的远程执行消息执行错误! msg=" + packet, e);
					// 错误处理
					onMessageError(packet, channel, params, e);
					return;
				}
			}

			@Override
			public String toString() {
				return "onModuleMessage[" + code + "]";
			}
		};

		// 根据玩家ID处理
		long playerId = packet.getPlayerId();
		enqueue(playerId, r);
	}

	/** http消息接口 **/
	public void onHttpRequest(final String url, final Map<String, String> params, final ProxyChannel channel, final RpcCallback callback) {
		// 获取接口
		final IHttpHandler handler = HttpMgr.getHandler(url);
		if (handler == null) {
			// 错误返回
			String errStr = LanguageSet.get(TextTempId.ID_7, "nofind handler:" + url);
			String retStr = JSON.toJSONString(HttpResult.create(0, errStr));
			callback.callBack(0, retStr);
			return;
		}

		// 创建异步执行任务
		Runnable r = new HttpHandler.HttpRunnable(channel, callback) {
			@Override
			protected Object execute(ProxyChannel channel, RpcCallback callback) throws Exception {
				return handler.execute(params, channel, callback);// 返回空是个异步的处理
			}

			@Override
			public String toString() {
				return "onHttpRequest[" + url + "]";
			}

		};

		// 获取id
		long id = getHttpCallId(params);
		enqueue(id, r);
	}

	/** 根据消息插入队列 **/
	protected void enqueue(long id, Runnable r) {
		ServiceMgr.execute(r);
	}

	/** 玩家连接验证(登陆绑定连接) **/
	public void onPlayerVerified(ProxyChannel channel, long connectId, long playerId) {
	}

	/** 玩家连接断开 **/
	public void onPlayerLost(ProxyChannel channel, long connectId, long playerId) {
	}

	/** 消息调用错误 **/
	protected void onMessageError(Message packet, ProxyChannel channel, Object[] params, Exception e) {
		// 错误消息整理
		String errStr = e.getLocalizedMessage();
		if (StringUtils.isEmpty(errStr)) {
			errStr = e.toString();
		}

		// 发送错误消息提示回去
		TextMsg textMsg = new TextMsg();
		textMsg.setText(LanguageSet.get(TextTempId.ID_7, errStr));
		textMsg.addType(MsgType.TIP);
		// 发送
		RouteMgr.sendPacketByConnectId(channel, packet.getConnectId(), Protocol.C_TEXT, textMsg);
	}

	/************* 消息参数处理 *************/

	/** 忽略参数, 返回这个对象代表这个可以为空. **/
	protected static final Object PARAM_IGNORE = new Object();
	/** 中断参数, 返回这个对象代表这个指令不执行了. **/
	protected static final Object PARAM_INTERRUPT = new Object();

	/** 获取消息调用所需参数 **/
	protected Object getMessageParam(Method method, int index, Class<?> paramType, Message packet, ProxyChannel channel, RpcCallback callback) throws Exception {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] parseArguments(Method method, Object[] objs) throws Exception {
		// 获取函数参数
		Class<?>[] parameterTypes = method.getParameterTypes();
		int psize = (parameterTypes != null) ? parameterTypes.length : 0;

		// 获取消息体
		Message packet = (Message) objs[0];

		// 转化参数
		Object[] args = new Object[psize];
		for (int i = 0; i < psize; i++) {
			Class<?> pc = parameterTypes[i];
			// 判断参数类型
			if (RpMessage.class.isAssignableFrom(pc)) {
				if (packet != null) {
					args[i] = packet.getMessage((Class<? extends RpMessage>) pc);
				}
			} else {
				// 自动选择
				args[i] = CmdInvoker.getArgs(objs, pc);
				// 手动选择
				if (args[i] == null) {
					Object param = getMessageParam(method, i, pc, packet, (ProxyChannel) objs[1], null);
					if (param == PARAM_IGNORE) {
						continue; // 忽略跳过
					} else if (param == PARAM_INTERRUPT) {
						return null; // 中断不执行下去了.
					}

					args[i] = param;
				}
			}

			// 空检测
			if (args[i] == null) {
				// 发送错误消息
				long playerId = packet.getPlayerId();
				Player.sendText(playerId, MsgType.TIP, LanguageSet.get(TextTempId.ID_7, "参数缺失!"));
				// 日志记录
				Log.error("参数获取失败: method=" + method + " i=" + i + " c=" + pc, true);
				return null;
			}
		}

		return args;
	}

	/**************** http消息Id **********************/

	/** 获取http的请求Id **/
	private long getHttpCallId(Map<String, String> params) {
		long playerId = getHttpCallId(params, "playerId");
		if (playerId != 0) {
			return playerId;
		}
		long userId = getHttpCallId(params, "userId");
		return userId;
	}

	/** 获取http的请求Id **/
	private long getHttpCallId(Map<String, String> params, String key) {
		// 先按照PlayerId获取
		long playerId = DataUtils.toLong(params.get(key));
		if (playerId != 0) {
			return playerId;
		}
		// 看看data有没有数据(json数据)
		String jsonStr = params.get("data");
		if (!StringUtils.isEmpty(jsonStr)) {
			@SuppressWarnings("unchecked")
			Map<String, Object> json = JSON.parseObject(jsonStr, Map.class);
			return ObjectUtils.numberValue(json.get(key));
		}
		return 0L;
	}

}
