/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: CometStatusMonitor.java
 *
 */
package com.ekupeng.top.comet.client.component;

import java.util.Date;
import java.util.SortedMap;

import com.ekupeng.top.comet.client.domain.CometStatus;

/**
 * @Description: 长连接状态监控器
 * @ClassName: CometStatusMonitor
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-6-2 下午6:47:02
 * @version V1.0
 */
public interface CometStatusMonitor {

	/**
	 * 状态变迁时可扩展点
	 * 
	 * @param originalStatus
	 *            迁移前的状态
	 * @param currentStatus
	 *            迁移后的状态
	 */
	public void onStatusChange(CometStatus originalStatus,
			CometStatus currentStatus);

	/**
	 * 获取最近20次状态迁移的变化日志
	 * 
	 * @return
	 */
	public SortedMap<Date, String> getChangeLogs();

}
