package com.game.core.http.open;

import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.game.base.service.config.ConfigMgr;
import com.game.base.service.constant.LoginConst;
import com.game.base.service.constant.PlayerType;
import com.game.base.service.constant.TextTempId;
import com.game.base.service.db.DaoMgr;
import com.game.base.service.http.HttpHandler;
import com.game.base.service.http.HttpMgr.HttpUrl;
import com.game.base.service.language.LanguageSet;
import com.game.base.service.mgr.GameChannelMgr;
import com.game.base.service.mgr.LoginMgr;
import com.game.base.service.module.ModuleName;
import com.game.base.service.rpc.handler.ICoreService;
import com.game.base.service.uid.UniqueId;
import com.game.base.utils.GameUtils;
import com.game.base.utils.TextUtils;
import com.game.core.login.LoginInventory;
import com.game.core.player.CorePlayer;
import com.game.entity.bean.AccountData;
import com.game.entity.bean.Device;
import com.game.entity.dao.LoginDAO;
import com.game.entity.dao.PlayerDAO;
import com.game.entity.dao.UserDAO;
import com.game.entity.entity.PlayerExtendInfo;
import com.game.entity.entity.PlayerInfo;
import com.game.entity.entity.UserInfo;
import com.game.entity.http.bean.CreateRoleInfo;
import com.game.entity.http.bean.HttpDataResult;
import com.game.entity.http.bean.HttpResult;
import com.game.entity.http.bean.LoginInfo;
import com.game.entity.http.bean.LoginKeyResult;
import com.game.entity.http.bean.LoginResult;
import com.game.framework.component.log.Log;
import com.game.framework.framework.api.baidu.BaiduIPLocation.BaiduIPLocationInfo;
import com.game.framework.framework.mgr.ServiceMgr;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.RpcCallback;
import com.game.framework.framework.xml.XmlNode;
import com.game.framework.utils.StringUtils;
import com.game.framework.utils.collections.ArrayUtils;
import com.game.framework.utils.struct.result.Result;
import com.game.utils.KeyUtils;
import com.game.utils.SyncKeyMgr;

/**
 * 登陆请求<br>
 */
@HttpUrl("core/login")
public class LoginRequest extends HttpHandler {
	/** 默认平台 **/
	public static final String DEFAULT_PLATFORM = "tgtgame";
	protected static String loginEncryptKey; // 登陆解码key
	
	public static final String[] paramKeyr = new String[]{"account","password","platform","connectIp"};
	@Override
	public Object execute(Map<String, String> params, ProxyChannel channel, RpcCallback callback) throws Exception {
		
		// 判断登陆参数
		String data = params.get("data");
		//验证
		if(!SyncKeyMgr.checkSign(SyncKeyMgr.createSourceStr(params.get("platform"),params.get("account"),params.get("password"),data), params.get("sign"))){
			return LoginResult.error(LanguageSet.get(TextTempId.ID_7, "非法请求"));
		}
		LoginInfo loginInfo = JSON.parseObject(data, LoginInfo.class);
		if (loginInfo == null) {
			Log.error("Json数据解析失败, loginInfo 为空, params = " + params);
			return LoginResult.error(LanguageSet.get(TextTempId.ID_7, "data"));
		}
		// 判断是否带登陆key, 如果带了直接跳过登陆验证.
		String loginKey = null;//loginInfo.getLoginKey();
		// sdk登陆验证
		String loginEncryptKey = getLoginEncryptKey();
		
		if (StringUtils.isEmpty(loginKey)) {
			try {
				
				//======改 数据加密==========
				JSONObject acJson = new JSONObject();
				for(String pk : paramKeyr){
					String vk = params.remove(pk);
					if(StringUtils.isEmpty(vk)){
						return LoginResult.error(LanguageSet.get(TextTempId.ID_7, "登陆账号或者密码错误"));
					}
					acJson.put(pk, vk);
				}
				String acData = KeyUtils.encrypt(loginEncryptKey, acJson.toJSONString());
				params.put("acData", acData);
//				Log.info("玩家数据1："+acJson.toJSONString());
//				Log.info("玩家数据2："+JSON.toJSONString(params));
				//======end==========
				// 登陆服验证,获取登陆key
				HttpDataResult<LoginKeyResult> result = LoginMgr.getInstance().call("/login", params, new TypeReference<HttpDataResult<LoginKeyResult>>() {
				});
				if (!result.isSucceed()) {
					return Result.create(Result.FAIL, "登陆服验证失败, " + TextUtils.getText(result.getMsg()));
				}
				loginKey = result.getData().getData();
				loginInfo.setExtra(result.getData().getExtra());
				loginInfo.setConnectIp(acJson.getString("connectIp"));
			} catch (Exception e) {
				Log.error("登陆验证错误!", e);
				return LoginResult.error(LanguageSet.get(TextTempId.ID_7, "登陆验证错误"));
			}
		}
		
		AccountData accountData = KeyUtils.decrypt(loginEncryptKey, loginKey, AccountData.class);
		if (accountData == null) {
			return LoginResult.error(LanguageSet.get(TextTempId.ID_1005));
		}

		// 设备信息
		Device device = loginInfo.getDevice();
		device = (device != null) ? device : new Device();

		// 填充参数
//		loginInfo.setConnectIp(params.get("connectIp"));
//		loginInfo.setDevice(loginInfo.getDevice());
		loginInfo.setDevice(device);
		// 执行登陆
		Object retObj = null;
		try {
			retObj = login(channel, callback, accountData, loginInfo);
		} catch (Exception e) {
			Log.error("玩家登陆错误! " + params, e);
			return LoginResult.error(LanguageSet.get(TextTempId.ID_7, "登陆错误"));
		}
		return retObj;
	}

	/** 登陆操作 **/
	protected static LoginResult login(final ProxyChannel channel, RpcCallback callback, final AccountData accountData, final LoginInfo loginInfo) {
		// 获取账号信息
		UserInfo userInfo = getUserInfo(accountData, true);
		if (userInfo == null) {
			return LoginResult.error(LanguageSet.get(TextTempId.ID_7, "创建账号错误"));
		}
		final long userId = userInfo.getUserId();

		// 检测账号封停
		Date banDate = userInfo.getBanTime();
		long banTime = (banDate != null) ? banDate.getTime() : 0L;
		if (banTime > 0 && System.currentTimeMillis() < banTime) {
			return LoginResult.error(LanguageSet.get(TextTempId.ID_1006));
		}

		// 按照UserId提交队列
		final UserInfo userInfo0 = userInfo;
		ServiceMgr.enqueue(userId, new HttpHandler.HttpRunnable(channel, callback) {
			@Override
			protected Object execute(final ProxyChannel channel, final RpcCallback callback) throws Exception {
				// 获取玩家信息
				LoginDAO dao = DaoMgr.getInstance().getDao(LoginDAO.class);
				PlayerInfo playerInfo = dao.getPlayerByUserId(userId);

				// 加载玩家数据
				if (playerInfo == null) {
					// 整理玩家数据
					String name = accountData.getName();
					if (GameUtils.isMobilePhoneNumaber(name)) {
						// 手机号码作为名称，隐藏中间的4为号码
						name = name.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
					}
					// 游客登录
					if (accountData.getPlatform().equals(LoginConst.PLATFORM_VISITOR)) {
						// 修改游客的登录名称
						String id = String.valueOf(userId).substring(6);
						name = "游客" + id;
					}
					int sex = accountData.getSex();
					// 没有账号, 默认创建角色.
					CreateRoleInfo createInfo = new CreateRoleInfo();
					createInfo.setUserId(userId);
					createInfo.setDevice(loginInfo.getDevice());
					createInfo.setGameZoneId(1);
					createInfo.setName(name);
					createInfo.setSex(sex);
					createInfo.setConnectIp(loginInfo.getConnectIp());
					createInfo.setHeadImgUrl(accountData.getHeadImgUrl());
					createInfo.setAgencyNumber(accountData.getAgencyNumber());

					// 创建角色
					HttpResult result = CreateRoleRequest.createPlayer(channel, callback, userInfo0, createInfo);
					if (!result.isSucceed()) {
						return LoginResult.error(result.getMsg());
					}
					// 重新获取一次
					playerInfo = dao.getPlayerByUserId(userId);

					if (playerInfo == null) {
						return LoginResult.error(LanguageSet.get(TextTempId.ID_1002));
					}
				} else {
					// 检测玩家头像是否又更改
					playerInfo.setHeadImgUrl(accountData.getHeadImgUrl());
					
					// 只有注册和升级用户的时候绑定
					/*if (StringUtils.isEmpty(playerInfo.getAgencyNumber())) {
						if (!StringUtils.isEmpty(accountData.getAgencyNumber())) {
							playerInfo.setAgencyNumber(accountData.getAgencyNumber());
						}
					}

					// 判断邀请码是否为空
					
					if (StringUtils.isEmpty(playerInfo.getAgencyNumber())) {
						// playerInfo.setProxyName("UU平台");
						playerInfo.setProxyId(0);
					} else {
						// 邀请码不为空，获得该邀请码所代理的代理账号
						WebUtilsDAO webUtilsDAO = DaoMgr.getInstance().getDao(WebUtilsDAO.class);
						ProxyInfo proxyInfo = webUtilsDAO.getProxyInfo(playerInfo.getAgencyNumber());
						if (proxyInfo != null) {
							playerInfo.setProxyId(proxyInfo.getProxyID());
							// playerInfo.setProxyName(proxyInfo.getAccount());
						}
					}*/
					if (playerInfo.isUpdate()) {
						// 保存到数据库
						PlayerDAO playerDao = DaoMgr.getInstance().getDao(PlayerDAO.class);
						playerDao.insertOrUpdate(playerInfo);
						playerInfo.commit();
					}
				}

				// 跳转到某个进程
				final long playerId = playerInfo.getPlayerId();
				ICoreService service = GameChannelMgr.getChannelServiceByPlayerId(playerId, ModuleName.CORE, ICoreService.class);
				service.loginPlayer(playerId, loginInfo, new RpcCallback() {
					@SuppressWarnings("unused")
					protected void onCallBack(LoginResult result) {
						onLoginCallback(channel, callback, userInfo0, loginInfo, result);
					}

					@Override
					protected void onTimeOut() {
						LoginResult result = LoginResult.error(LanguageSet.get(TextTempId.ID_1004));
						onLoginCallback(channel, callback, userInfo0, loginInfo, result);
					}
				});

				// // 获取连接服务器.
				// ServerConfig serverConfig = channel.getConfig();
				// // 返回结果
				// LoginResult loginResult = new LoginResult();
				// loginResult.setUserId(userId);
				// loginResult.setPlayerId(playerId);
				// loginResult.setKey(key);
				// // 登陆成功
				// loginResult.setCode(LoginResult.RESP_LOGIN_SUCC);
				// loginResult.setHost(serverConfig.getHost());
				// loginResult.setPort(serverConfig.getPort());
				// return loginResult;

				return null; // 异步返回
			}
		});

		return null; // 异步返回
	}

	/** 登陆回调 **/
	private static void onLoginCallback(final ProxyChannel channel, RpcCallback callback, UserInfo userInfo, LoginInfo loginInfo, LoginResult loginResult) {
		// 失败
		if (!loginResult.isSucceed()) {
			HttpRunnable.httpCallback(callback, 1, loginResult);
			return;
		}

		// 填充成功消息
		loginResult.setUserId(userInfo.getUserId());
		loginResult.setCode(LoginResult.RESP_LOGIN_SUCC);
		//新的请求模式下 无需在向客户端暴露 网关位置
		/*ServerConfig serverConfig = channel.getConfig();
		loginResult.setHost(serverConfig.getHost());
		loginResult.setPort(serverConfig.getPort());*/
		loginResult.setExtra(loginInfo.getExtra());

		// 回调
		HttpRunnable.httpCallback(callback, 1, loginResult);
		return;
	}

	/** 执行玩家登陆 **/
	public static LoginResult loginPlayer(CorePlayer player, LoginInfo loginInfo) {
		// 判断账号类型
		if (player.isRobet()) {
			// debug模式下可以登录机器人
			if (!ConfigMgr.isDebug()) {
				return LoginResult.error(LanguageSet.get(TextTempId.ID_7, "机器人角色不能登录!"));
			} else {
				// 临时改成普通玩家
				player.setType(PlayerType.NORMAL);
			}
		}

		// 登陆成功执行
		final String key = StringUtils.uuid(); // 登陆key
		if (!onLoginSuccess(player, key, loginInfo)) {
			return LoginResult.error(LanguageSet.get(TextTempId.ID_1004));
		}

		// Log.info("客户端验证成功!" + player + " " + loginInfo);

		// 返回结果
		LoginResult loginResult = new LoginResult();
		loginResult.setKey(key);
		loginResult.setPlayerId(player.getPlayerId());
		loginResult.setCode(LoginResult.RESP_LOGIN_SUCC);
		return loginResult;
	}

	/** 登陆成功处理, 读取数据指定key. **/
	protected static boolean onLoginSuccess(CorePlayer player, String key, LoginInfo loginInfo) {
		Device device = loginInfo.getDevice();

		// 绑定key和设备信息
		final PlayerExtendInfo extendInfo = player.getExtendInfo();

		// 更新IP地址
		String connetcIp = loginInfo.getConnectIp();
		String nowIp = extendInfo.getUpdateIP();
		if (nowIp == null || !nowIp.equals(connetcIp)) {
			extendInfo.setUpdateIP(connetcIp);
		}

		// 地理位置
		double[] location = StringUtils.splitToDouble(loginInfo.getLocation(), ",");
		location = ArrayUtils.resetArray(location, 2);
		player.setLocation(location[0], location[1]);

		// 绑定登陆key
		LoginInventory loginInventory = player.getInventory(LoginInventory.class);
		loginInventory.setLoginKey(key);
		if (device != null) {
			loginInventory.setDevice(device);
		}
		return true;
	}

	/** 更新地址信息(线程安全, 提交任务给玩家处理) **/
	public static void updateAddress(final CorePlayer player, final String connetcIp) {
		// 提交个任务去读取IP地址, 不要卡主主线程.
		player.enqueue(new Runnable() {
			@Override
			public void run() {
				// 更新IP地址
				BaiduIPLocationInfo info = GameUtils.iplocation.location(connetcIp);
				if (info == null) {
					return;
				}
				// 填写数据
				PlayerExtendInfo extendInfo = player.getExtendInfo();
				extendInfo.setAddress(info.toEncode());

				// 更新定位
				player.setLocation(info.getX(), info.getY());
				// Log.debug("player=" + player + " ip=" + connetcIp + " location=" + info);
			}
		});
	}

	/** 获取登陆解密key **/
	public static String getLoginEncryptKey() {
		if (loginEncryptKey != null) {
			return loginEncryptKey;
		}
		final String defaultKey = "xsgserver2018";
		// 获取key
		XmlNode loginNode = ConfigMgr.getElem("Login");
		if (loginNode == null) {
			loginEncryptKey = defaultKey;
			return loginEncryptKey;
		}
		// 获取key
		loginEncryptKey = loginNode.getAttr("encryptkey", defaultKey);
		return loginEncryptKey;
	}

	/** 获取账号信息 **/
	public static UserInfo getUserInfo(AccountData accountData, boolean create) {
		String account = accountData.getAccount();
		String platform = accountData.getPlatform();
		final int gameZoneId = 1;

		// 获取账号信息
		LoginDAO dao = DaoMgr.getInstance().getDao(LoginDAO.class);
		UserInfo userInfo = dao.getUser(account, platform, gameZoneId);
		if (userInfo == null) {
			if (!create) {
				return null; // 不创建账号
			}

			// 读取创建参数
			String modilePhone = accountData.getModilePhone();

			// 创建玩家数据
			userInfo = new UserInfo();
			userInfo.setUserId(UniqueId.USER.getUniqueId(gameZoneId));
			userInfo.setAccount(account);
			userInfo.setPlatform(platform);
			userInfo.setGameZoneId(gameZoneId);
			userInfo.setCreateTime(new Date());
			userInfo.setMobilePhone(modilePhone);

			// 执行保存
			UserDAO udao = DaoMgr.getInstance().getDao(UserDAO.class);
			udao.insertOrUpdate(userInfo);
		}
		// 对忽略的错误做容错处理
		// 判断是否绑定手机号码
		if (!StringUtils.isEmpty(accountData.getModilePhone()) && StringUtils.isEmpty(userInfo.getMobilePhone())) {
			userInfo.setMobilePhone(accountData.getModilePhone());
			// 执行保存
			UserDAO udao = DaoMgr.getInstance().getDao(UserDAO.class);
			udao.insertOrUpdate(userInfo);
		}
		return userInfo;
	}
}
