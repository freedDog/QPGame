package com.game.base.service.constant;

/**
 * 实物订单状态
 * GoodsOrderState.java
 * @author JiangBangMing
 * 2019年1月8日下午3:19:55
 */
public interface GoodsOrderState {

	/** 关闭 **/
	short CLOSE = -1;
	/** 待确认地址**/
	short INIT=0;
	/** 审核未通过**/
	short NOPASS =1;
	/** 待审核**/
	short WAIT= 2;
	/** 审核通过待发货**/
	short READY =3;
	/** 已发货**/
	short GOING = 4;
	/** 接收成功**/
	short RECV=5;
	/** 完成晒单**/
	short DONE =6;
	/** 接收失败**/
	short FAIL =7;
	/** 退换中**/
	short EXIST =8;
	
	
}
