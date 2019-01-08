package com.game.core.http.open;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.game.base.service.config.ConfigMgr;
import com.game.base.service.config.ServerConfig;
import com.game.base.service.constant.CurrencyId;
import com.game.base.service.constant.PlayerConstant;
import com.game.base.service.constant.PlayerType;
import com.game.base.service.constant.ProductSourceType;
import com.game.base.service.constant.TextTempId;
import com.game.base.service.db.DaoMgr;
import com.game.base.service.http.JsonHandler;
import com.game.base.service.language.LanguageSet;
import com.game.base.service.tempmgr.GameConfigMgr;
import com.game.base.service.uid.UniqueId;
import com.game.core.player.CorePlayer;
import com.game.core.player.CorePlayerMgr;
import com.game.entity.bean.Device;
import com.game.entity.bean.Product;
import com.game.entity.dao.LoginDAO;
import com.game.entity.dao.PlayerDAO;
import com.game.entity.entity.PlayerInfo;
import com.game.entity.entity.UserInfo;
import com.game.entity.http.bean.CreateRoleInfo;
import com.game.entity.http.bean.HttpResult;
import com.game.entity.http.bean.LoginResult;
import com.game.framework.component.log.Log;
import com.game.framework.framework.rpc.ProxyChannel;
import com.game.framework.framework.rpc.RpcCallback;
import com.game.framework.utils.StringUtils;
import com.game.framework.utils.struct.result.Result;

/**
 * 创建玩家<br>
 * CreateRoleRequest.java
 * @author JiangBangMing
 * 2019年1月8日下午2:10:23
 */
//@HttpUrl("core/createrole")
public class CreateRoleRequest extends JsonHandler<CreateRoleInfo> {
	@Override
	public Object execute(CreateRoleInfo data, Map<String, String> params, ProxyChannel channel, RpcCallback callback) {
		return HttpResult.error(LanguageSet.get(TextTempId.ID_6));
	}

	/** 创建玩家 **/
	public static LoginResult createPlayer(ProxyChannel channel, RpcCallback callback, UserInfo userInfo, CreateRoleInfo createInfo) {
		long userId = userInfo.getUserId();
		String key = createInfo.getKey();

		// 参数
		PlayerInfo playerInfo = null;
		LoginDAO playerDao = DaoMgr.getInstance().getDao(LoginDAO.class);

		// 检测是否重复创建账号
		if (userId > 0) {
			playerInfo = playerDao.getPlayerByUserId(userId);
			if (playerInfo != null) {
				return Result.error(LoginResult.class, LanguageSet.get(TextTempId.ID_1002));
			}
		}

		// 创建玩家
		playerInfo = createPlayerInfo(createInfo, userInfo);
		if (playerInfo == null) {
			return Result.error(LoginResult.class, LanguageSet.get(TextTempId.ID_1003));
		}

		// 获取coreplayer
		long playerId = playerInfo.getPlayerId();
		CorePlayer player = CorePlayerMgr.getInstance().get(playerId);
		if (player == null) {
			return Result.error(LoginResult.class, LanguageSet.get(TextTempId.ID_1004));
		}

		// 创建角色成功,执行创建初始化
		Device device = createInfo.getDevice();
		if (!onCreateSuccess(player, key, device)) {
			return Result.error(LoginResult.class, LanguageSet.get(TextTempId.ID_1002));
		}

		// 返回结果
		LoginResult loginResult = new LoginResult();
		loginResult.setCode(LoginResult.RESP_LOGIN_SUCC);
		loginResult.setUserId(userId);
		loginResult.setPlayerId(playerId);
		loginResult.setKey(key);
		// 登陆成功
		if (channel != null) {
			ServerConfig serverConfig = channel.getConfig();
			loginResult.setHost(serverConfig.getHost());
			loginResult.setPort(serverConfig.getPort());
		}
		return loginResult;
	}

	/** 创建角色 **/
	private static PlayerInfo createPlayerInfo(CreateRoleInfo createInfo, UserInfo userInfo) {
		// 参数检测
		String name = createInfo.getName();
		if (StringUtils.isEmpty(name)) {
			Log.error("创建账号错误,名字为空! createInfo=" + createInfo, true);
			return null;
		}
		short level = 1;
		long playerId = UniqueId.PLAYER.getUniqueId();
		// 性别处理
		short sex = (short) createInfo.getSex();

		// 创建玩家信息
		PlayerInfo info = new PlayerInfo();
		info.setPlayerId(playerId);
		info.setName(name);
		info.setLevel(level);
		info.setCreateTime(new Date());
		info.setLoginTime(new Date());
		info.setLogoutTime(new Date());
		info.setUpdateTime(new Date(0)); // 更新时间不设置
		info.setUserId(createInfo.getUserId());
		info.setType(PlayerType.NORMAL);
		info.setSex(sex);
		info.setCreateIP(createInfo.getConnectIp());
		info.setHeadImgUrl(createInfo.getHeadImgUrl());
		info.setAgencyNumber(null);
		info.setProxyId(0);
//		//=======代理绑定信息========
//		AddProxyErrorInfo errorInfo = new AddProxyErrorInfo();
//		Map<String,Object> agencyParam = new HashMap<>();
//		agencyParam.put("playerId", playerId);
//		agencyParam.put("account", userInfo.getAccount());
//		agencyParam.put("proxyID", KeyGenerateEnum.RedisKey.keyLong());
//		errorInfo.setPlayerId(playerId);
//		errorInfo.setAccount(userInfo.getAccount());
//		errorInfo.setProxyID((long)agencyParam.get("proxyID"));
//		//======================
//		// 判断邀请码是否为空
//		if (!(StringUtils.isEmpty(createInfo.getAgencyNumber()) || StringUtils.isEmpty(createInfo.getDevice().getMac()))) {
//			// 邀请码不为空，获得该邀请码所代理的代理账号
//			WebUtilsDAO webUtilsDAO = DaoMgr.getInstance().getDao(WebUtilsDAO.class);
//			ProxyInfo proxyInfo = webUtilsDAO.getProxyInfo(createInfo.getAgencyNumber());
//			if (proxyInfo != null){
//				Map<String,String> paramHttp = new HashMap<>();
//				
//				AutoAgecyBindInfo aab = new AutoAgecyBindInfo();
//				aab.setAgencynumber(createInfo.getAgencyNumber());
//				aab.setDevicenumber(createInfo.getDevice().getMac());
//				
//				paramHttp.put("opertion", "0");
//				paramHttp.put("data", KeyUtils.encrypt(LoginRequest.getLoginEncryptKey(), aab));
//				Log.info("注册账号（"+userInfo.getAccount()+"）试图绑定代理码（"+createInfo.getAgencyNumber()+"） mac（"+createInfo.getDevice().getMac()+"）,开始执行绑定流程");
//				Result resultHttp = LoginMgr.getInstance().call("/autoAgencyBindInfo", paramHttp);
//				//表示没有查询到
//				if(resultHttp.getCode() == 201){
//					paramHttp.put("opertion", "1");
//					aab.setPhonecode(userInfo.getAccount());
//					paramHttp.put("data", KeyUtils.encrypt(LoginRequest.getLoginEncryptKey(), aab));
//					resultHttp = LoginMgr.getInstance().call("/autoAgencyBindInfo", paramHttp);
//					if (resultHttp.getCode() == 200) {
//						// 设置邀请码和代理码
//						info.setProxyId(proxyInfo.getProxyID());
//						// info.setProxyName(proxyInfo.getAccount());
//						info.setAgencyNumber(createInfo.getAgencyNumber());
//						//=======代理绑定信息======
//						agencyParam.put("superiorproxy", proxyInfo.getProxyID());
//						agencyParam.put("superiorAccount", proxyInfo.getAccount());
//						agencyParam.put("superiorAgencyNumber", proxyInfo.getInviCode());
//						agencyParam.put("superiorPlayerId", proxyInfo.getPlayerId());
//						errorInfo.setSuperiorproxy(proxyInfo.getProxyID());
//						errorInfo.setSuperiorAccount(proxyInfo.getAccount());
//						errorInfo.setSuperiorAgencyNumber(proxyInfo.getInviCode());
//						errorInfo.setSuperiorPlayerId(proxyInfo.getPlayerId());
//						//===================
//						Log.info("注册账号（"+userInfo.getAccount()+"）试图绑定代理码（"+createInfo.getAgencyNumber()+"） mac（"+createInfo.getDevice().getMac()+"）,并且绑定成功！");
//					}else{
//						Log.error("http接口: /autoAgencyBindInfo->save 请求失败："+resultHttp.getCode() +" bindInfo:注册账号（"+userInfo.getAccount()+"）试图绑定代理码（"+createInfo.getAgencyNumber()+"） mac（"+createInfo.getDevice().getMac());
//					}
//				}else if(resultHttp.getCode() == 200){
//					Log.info("注册账号（"+userInfo.getAccount()+"）试图绑定代理码（"+createInfo.getAgencyNumber()+"） mac（"+createInfo.getDevice().getMac()+"）,查询到该机器和代理码已经绑定过一次用户了！");
//				}else{
//					Log.error("http接口: /autoAgencyBindInfo->query 请求失败Code："+resultHttp.getCode() +" bindInfo:注册账号（"+userInfo.getAccount()+"）试图绑定代理码（"+createInfo.getAgencyNumber()+"） mac（"+createInfo.getDevice().getMac());
//				}
//			}else{
//				Log.info("account:"+userInfo.getAccount()+" 找不到代理号："+createInfo.getAgencyNumber());
//			}
//		}
//		if(!userInfo.getPlatform().equals(LoginConst.PLATFORM_VISITOR)){
//			Result agenResult = AgencyMgr.getInstance().call("/playerproxy/insert", agencyParam);
//			if(!agenResult.isSucceed()){
//				Log.error("注册账号<自动注册代理> 请求失败："+JSON.toJSONString(agencyParam));
//				AddProxyErrorDAO errorDao = DaoMgr.getInstance().getDao(AddProxyErrorDAO.class);
//				if(errorDao != null){
//					errorDao.insertOrUpdate(errorInfo);
//				}else{
//					Log.error("注册账号<自动注册代理> 代理信息存储失败 player:"+playerId);
//				}
//			}
//			info.setIsAgency(1);//已是代理的标识
//		}
		// 保存到数据库
		PlayerDAO playerDao = DaoMgr.getInstance().getDao(PlayerDAO.class);
		playerDao.insertOrUpdate(info);
		return info;
	}
	
	/** 创建成功处理, 读取数据指定key. **/
	private static boolean onCreateSuccess(final CorePlayer player, String key, Device device) {
		try {

			// 获取初始化道具
			Product[] products = GameConfigMgr.createPlayerProducts;
			int psize = (products != null) ? products.length : 0;
			if (psize > 0) {
				// 添加初始道具
				Result result = player.addProducts(products, ProductSourceType.CREATE_ROLE, false);
				if (!result.isSucceed()) {
					// 提示即可, 不用断开.
					Log.error("添加创角色资源失败! " + Arrays.toString(GameConfigMgr.createPlayerProducts) + " " + result.getMsg());
				}
			}

			// 测试物品下发
			if (ConfigMgr.isDebug()) {
				player.addCurrency(CurrencyId.POINT, PlayerConstant.CREATE_ROLE_INIT_POINT, ProductSourceType.CREATE_ROLE, false);
			}

			/*List<Product> products1 = Arrays.asList(GameConfigMgr.loginAwards);
			MailMgr.addMailByPlayer(player, "首次登陆奖励", "恭喜您获得首次登陆奖励", products1, TimeUtils.oneWeekTime, ProductSourceType.LOGINAWARDS);*/

			// 判断是否已经绑定手机了
			if (!StringUtils.isEmpty(player.getUserInfo().getMobilePhone())) {
				// 赠送绑定手机奖励
				List<Product> products2 = Arrays.asList(GameConfigMgr.bindPhoneExpends);
//				MailMgr.addMailByPlayer(player, "绑定手机奖励", "恭喜您获得绑定手机奖励", products2, TimeUtils.oneWeekTime, ProductSourceType.BINDPHONEAWARDS);
			}

//			// 更新抢宝玩家数据
//			SnatchInventory snatchInventory = player.getInventory(SnatchInventory.class);
//			snatchInventory.updateSnatchPlayerInfoModule();

		} catch (Exception e) {
			Log.error("创建角色, 初始化数据错误!", e);
			return false;
		}

		// 按照登陆成功流程处理
		// if (!LoginRequest.onLoginSuccess(player, key, device)) {
		// return false;
		// }
		return true;
	}
}
