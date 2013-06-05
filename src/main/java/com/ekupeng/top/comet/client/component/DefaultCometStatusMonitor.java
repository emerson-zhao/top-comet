/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: DefaultCometStatusMonitor.java
 *
 */
package com.ekupeng.top.comet.client.component;

import org.springframework.stereotype.Component;

import com.ekupeng.top.comet.client.config.CometStatus;

/**
 * @Description: 默认长连接状态监控器
 * @ClassName: DefaultCometStatusMonitor
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-6-3 下午12:19:48
 * @version V1.0
 */
@Component("cometStatusMonitor")
public class DefaultCometStatusMonitor implements CometStatusMonitor {

	/* 
	 * Override
	 */
	@Override
	public void onStatusChange(CometStatus originalStatus,
			CometStatus currentStatus) {
		// TODO Auto-generated method stub

	}

}
