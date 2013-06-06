/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: DefaultCometStatusMonitor.java
 *
 */
package com.ekupeng.top.comet.client.component;

import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

import com.ekupeng.top.comet.client.domain.CometStatus;

/**
 * @Description: 默认长连接状态监控器
 * @ClassName: DefaultCometStatusMonitor
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-6-3 下午12:19:48
 * @version V1.0
 */
@Component("cometStatusMonitor")
public class DefaultCometStatusMonitor implements CometStatusMonitor {

	// 维护最近20次状态变化
	private volatile SortedMap<Date, String> changeLogs = new TreeMap<Date, String>();

	/*
	 * Override
	 */
	@Override
	public synchronized void onStatusChange(CometStatus originalStatus,
			CometStatus currentStatus) {
		if (changeLogs.size() >= 20) {
			changeLogs.remove(changeLogs.firstKey());
		}
		String log = originalStatus.name() + " --> " + currentStatus.name();
		changeLogs.put(new Date(), log);
	}

	public SortedMap<Date, String> getChangeLogs() {
		return changeLogs;
	}
}
