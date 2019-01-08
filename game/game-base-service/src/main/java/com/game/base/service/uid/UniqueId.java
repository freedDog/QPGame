package com.game.base.service.uid;

/**
 * 数据唯一ID
 * 全局唯一高效率 KEY 生成 参考 KeyGenerateEnum 不支持短K
 */
public enum UniqueId {
	/** 用户Id **/
	USER {
		@Override
		public int getType() {
			return 1;
		}
	},

	/** 角色Id **/
	PLAYER {
		@Override
		public int getType() {
			return 2;
		}
	},

	/** 物品Id **/
	ITEM {
		@Override
		public int getType() {
			return 3;
		}
	},

	/** 邮件 **/
	MAIL {
		@Override
		public int getType() {
			return 4;
		}
	},
	/** 任务 **/
	TASK {
		@Override
		public int getType() {
			return 5;
		}
	},
	/** 任务 **/
	GOOD_ORDER {
		@Override
		public int getType() {
			return 6;
		}
	},
	/** 公告 **/
	NOTICE {
		@Override
		public int getType() {
			return 7;
		}
	},
//	/** 抢宝期号 **/
//	SNATCH {
//		@Override
//		public int getType() {
//			return 8;
//		}
//	},
//	/** 夺宝赛房间 **/
//	CONTEST_ROOM {
//		@Override
//		public int getType() {
//			return 9;
//		}
//
//	},

	/** 录像记录 **/
	GAME_VIDEO {
		@Override
		public int getType() {
			return 10;
		}
	},
	/** 抽奖 */
	SEACHE_DROP{
		@Override
		public int getType() {
			return 11;
		}
	}
	,
	/** 公会 */
	GUILD{
		@Override
		public int getType() {
			return 12;
		}
	}
	,
	/** 公会成员 */
	GUILD_MEMBER{
		@Override
		public int getType() {
			return 13;
		}
	}
	,
	/** 申请信息 */
	GUILD_MESSAGE{
		@Override
		public int getType() {
			return 14;
		}
	}
	,
	/** 基金帐单 */
	GUILD_FUND{
		@Override
		public int getType() {
			return 15;
		}
	}
	,
	/** 充值订单 */
	ORDER{
		@Override
		public int getType() {
			return 16;
		}
	},
	
	/** 提现订单 */
	PROPAY{
		@Override
		public int getType() {
			return 17;
		}
	}
	//
	;

	/** 获取枚举对应类型 **/
	public abstract int getType();

	/** 生成一个唯一Id **/
	public long getUniqueId(int gameZoneId) {
		return UniqueIdMgr.getUniqueId(this, gameZoneId);
	}

	/** 生成一个唯一Id **/
	public long getUniqueId() {
		return getUniqueId(1);
	}
}

