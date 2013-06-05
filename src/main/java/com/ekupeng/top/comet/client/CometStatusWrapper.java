/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: CometStatusWrapper.java
 *
 */
package com.ekupeng.top.comet.client;

import com.ekupeng.top.comet.client.component.CometStatusMonitor;
import com.ekupeng.top.comet.client.config.CometStatus;

/**
 * @Description: 长连接状态包装类
 * @ClassName: CometStatusWrapper
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-6-3 下午6:54:30
 * @version V1.0
 */
public class CometStatusWrapper {
	/*
	 * 长连接状态，初始为关闭（CLOSED），修改该状态属性将激活监控器
	 */
	private volatile CometStatus currentCometStatus = CometStatus.CLOSED;

	/*
	 * 长连接状态监控器
	 */
	private CometStatusMonitor cometStatusMonitor;

	protected CometStatus getCurrentCometStatus() {
		return currentCometStatus;
	}

	/**
	 * 若配置了监控器，则修改状态属性将激活监控器
	 * @param currentCometStatus
	 */
	protected void setCurrentCometStatus(CometStatus currentCometStatus) {
		if (cometStatusMonitor != null) {
			if (!this.currentCometStatus.equals(currentCometStatus)) {
				cometStatusMonitor.onStatusChange(this.currentCometStatus,
						currentCometStatus);
			}
		}
		this.currentCometStatus = currentCometStatus;
	}

	protected void setCometStatusMonitor(CometStatusMonitor cometStatusMonitor) {
		this.cometStatusMonitor = cometStatusMonitor;
	}

}
