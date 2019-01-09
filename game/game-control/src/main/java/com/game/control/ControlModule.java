package com.game.control;

import com.game.base.service.config.ModuleConfig;
import com.game.base.service.mgr.CountMgr;
import com.game.base.service.module.Module;
import com.game.base.service.module.ModuleName;
import com.game.base.service.server.App;
import com.game.framework.framework.xml.XmlNode;

/**
 * core模块
 * 
 */
public class ControlModule extends Module {
	protected ControlModule(XmlNode moduleNode) {
		super(moduleNode);
	}

	@Override
	public boolean init() {
		// 初始化通用功能
		if (!super.init()) {
			return false;
		}

		// 初始化组件
		if (!initStatic(CountMgr.class)) {
			return false;
		}
//		if (!initStatic(LobbyTempMgr.class)) {
//			return false;
//		}
//		if (!initStatic(CentreNoticeMgr.class)) {
//			return false;
//		}
//		if (!initStatic(CentreRankMgr.class)) {
//			return false;
//		}
//		if (!initStatic(GoodsTempMgr.class)) {
//			return false;
//		}
//		if (!initStatic(ExchangeMgr.class)) {
//			return false;
//		}
//		if (!initStatic(GlobalGameConfigMgrX.class)) {
//			return false;
//		}
////		if (!initStatic(ActivityTempMgr.class)) {
////			return false;
////		}
//		if (!initStatic(DirtyTempMgr.class)) {
//			return false;
//		}
//		if (!initStatic(RoomCardResultMgr.class)) {
//			return false;
//		}
//		if (!initStatic(RoomCardResultMsgMgr.class)) {
//			return false;
//		}
//		if (!init(RobetMgr.class, RobetMgr.getInstance())) {
//			return false;
//		}
//		if (!initStatic(CentreLobbyMgr.class)) {
//			return false;
//		}
//		if (!initStatic(RobotIncomeExpenseMgr.class)) {
//			return false;
//		}

		// 注册定时器
//		TimeMgr.register(new SaveGoodsDataTimer()); // 保存库存
//		TimeMgr.register(new SaveGlobalConfigTimer());// 保存VIP配置
//		TimeMgr.register(new CheckGoodsStateTimer());
//		TimeMgr.register(new SaveRobotIncomeExpenseTimer()); // 机器人收入支出信息更新到数据库

		return true;
	}

	@Override
	public void destroy() {
//		CentreNoticeMgr.save();
//		Log.info("走马灯保存成功");
//
//		GoodsTempMgr.save();
//		Log.info("保存实物模板成功");
//
//		GlobalGameConfigMgrX.save();
//		Log.info("保存全局参数");
	}

	@Override
	public ModuleName getModuleName() {
		return ModuleName.CONTROL;
	}

	@Override
	protected boolean createConfig(int[] gameZoneIds) {
		super.config = new ModuleConfig(getModuleName().name(), App.getInstance().getConfig(), gameZoneIds, ModuleConfig.MODE_UNIQUE);
		return true;
	}

}
