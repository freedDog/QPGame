package com.game.base.service.module;

import java.util.Arrays;

import com.game.framework.component.log.Log;
import com.game.framework.utils.collections.ArrayUtils;

/**
 * 模块名
 * 
 */
public enum ModuleName {
	/** 客户端模块(用于发送消息) **/
	CLIENT {
		protected int[] getCodes() {
			return new int[] { 1, 10000 };
		}
	},

	/** core模块 **/
	CORE {
		protected int[] getCodes() {
			return new int[] { 10001, 13000 };
		}
	},

	/** 中控模块 **/
	CONTROL,
	/** (斗地主)房间模块 **/
	ROOM,
	/** 抢宝模块 **/
	SNATCH,
	/** 夺宝赛模块 **/
	CONTEST,
	/** 斗金牛模块 **/
	BULLGOLDFIGHT,
	/** 德州扑克模块 **/
	TEXASHOLDEM,
	/** 炸金花模块 **/
	ZHAJINHUA,
	/**
	 * 公会模块
	 */
	GUILD{
		protected int[] getCodes() {
			return new int[] { 20001, 21000 };
		}
	},
	
	LHD{
		protected int[] getCodes() {
			return new int[] { 21001, 22000 };
		}
	}
	
	;

	protected int[] codes; // 消息区间
	protected String[] urls; // 列表区间

	private ModuleName() {
	}

	/** 获取协议区间 **/
	protected int[] getCodes() {
		// 获取数据
		if (codes == null) {
			// 获取枚举在列表中的位置.
			final int codeRange = 1000; // 消息区间, 1个模块1000.
			int index = ArrayUtils.indexOf(values(), this);
			// 判断是否是起始协议
			int[] codes0 = null;
			if (index == 0) {
				// 起始协议
				final int codeStart = 1000; // 起始消息Id
				codes0 = new int[] { codeStart + index * codeRange + 1, codeStart + (index + 1) * codeRange };
			} else {
				// 获取前个协议的后缀
				ModuleName prevModuleName = ModuleName.values()[index - 1];
				int[] prevCodes = prevModuleName.getCodes();
				int codeStart = prevCodes[1] + 1;
				int codeIndex = (int) (codeStart / (double) codeRange);
				// codeStart = codeRange * codeIndex; // 区间取整
				// int codeEnd = codeRange +codeRange; // 区间取整
				int codeEnd = codeRange * (codeIndex + 1); // 区间取整
				// 接着区间.
				codes0 = new int[] { codeStart, codeEnd };
			}
			// 写入.
			this.codes = codes0;
		}

		return codes;
	}

	/** 获取模块对应url起始. **/
	protected String[] getModuleUrls() {
		if (this.urls != null) {
			return urls;
		}

		String moduleName = this.name();
		// 去除掉头字段
		// final String prefix = "SG_";
		// if (moduleName.indexOf(prefix) == 0) {
		// moduleName = moduleName.substring(prefix.length());
		// }
		// 小写写入.
		moduleName = moduleName.toLowerCase() + "/";
		String[] urls0 = new String[] { moduleName };
		this.urls = urls0;
		return urls0;
	}

	/** 检测是否是这个模块的协议段 **/
	public boolean checkCode(int code) {
		int[] codes = getCodes();
		if (codes[0] <= code && code <= codes[1]) {
			return true;
		}
		return false;
	}

	/** 检测是否是这个模块的url **/
	public boolean checkUrl(String url) {
		// 获取模块对应url起始.
		String[] urls = getModuleUrls();
		int usize = (urls != null) ? urls.length : 0;
		for (int j = 0; j < usize; j++) {
			String url0 = urls[j];
			if (url.startsWith(url0)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return name();
	}

	/********************* 静态处理 *************************/
	static {
		// 处理模块信息
		ModuleName[] moduleNames = ModuleName.values();
		int msize = moduleNames.length;
		for (int i = 0; i < msize; i++) {
			ModuleName moduleName = moduleNames[i];
			// 生成最后一个消息的码.
			int[] getCodes = moduleName.getCodes();
			int start = getCodes[0];
			int end = getCodes[1];

			// 检测区间数据
			if (start <= 0 || end <= 0) {
				Log.error("模块消息区间不能小于0! " + moduleName + " [" + start + "," + end + "]");
				System.exit(0);
			}

			// 检测区间数据
			if (start >= end) {
				Log.error("模块消息区间数值不对! " + moduleName + " [" + start + "," + end + "]");
				System.exit(0);
			}
			// 获取模块url
			String[] getUrls = moduleName.getModuleUrls();
			int usize = (getUrls != null) ? getUrls.length : 0;

			// 对已有code区间进行检测是否重复
			for (int j = 0; j < i; j++) {
				// 检测是否是同个模块
				ModuleName moduleName0 = moduleNames[j];
				if (moduleName0.equals(moduleName)) {
					continue;
				}

				// 判断区间是否冲突, 只要检测首位是否相交即可.
				int[] checkCodes = moduleName0.getCodes();
				if (!(end < checkCodes[0] || start > checkCodes[0])) {
					Log.error("模块消息区间数值冲突! " + moduleName + " [" + start + "," + end + "] -> " + moduleName0);
					System.exit(0);
				}
				// 检测url是否冲突
				if (usize > 0) {
					String[] checkUrls = moduleName0.getModuleUrls();
					int cusize = (checkUrls != null) ? checkUrls.length : 0;
					for (int k = 0; k < cusize; k++) {
						String checkUrl = checkUrls[k];
						for (int l = 0; l < usize; l++) {
							String getUrl = getUrls[l];
							if (getUrl.startsWith(checkUrl) || checkUrl.startsWith(getUrl)) {
								Log.error("模块消息url冲突! " + moduleName + " [" + getUrl + "] -> " + moduleName0 + " [" + checkUrl + "]");
								System.exit(0);
							}
						}
					}
				}
			}
			// 插入数据
			Log.debug("模块信息: " + moduleName + " codes=[" + start + "," + end + "] urls=" + Arrays.toString(getUrls));
		}
	}

	/** 根据url获取对应模块 **/
	public static ModuleName getModuleByUrl(String url) {
		ModuleName[] moduleNames = ModuleName.values();
		int msize = (moduleNames != null) ? moduleNames.length : 0;
		for (int i = 0; i < msize; i++) {
			ModuleName moduleName = moduleNames[i];
			// 获取模块对应url起始.
			if (moduleName.checkUrl(url)) {
				return moduleName;
			}
		}
		return null;
	}

	/** 根据协议Id获取对应模块 **/
	public static ModuleName getModuleByCode(int code) {
		ModuleName[] moduleNames = ModuleName.values();
		int msize = (moduleNames != null) ? moduleNames.length : 0;
		for (int i = 0; i < msize; i++) {
			ModuleName moduleName = moduleNames[i];
			if (moduleName.checkCode(code)) {
				return moduleName;
			}
		}
		return null;
	}

}

