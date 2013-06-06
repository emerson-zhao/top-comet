/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: CometStatus.java
 *
 */
package com.ekupeng.top.comet.client.domain;

/**
 * @Description: 长连接状态
 * @ClassName: CometStatus
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-6-2 下午6:50:11
 * @version V1.0
 */
public enum CometStatus {
	/**
	 * 重新连接中
	 */
	RECONNECTING,
	/**
	 * 连接中状态
	 */
	CONNECTED,
	/**
	 * 连接已关闭
	 */
	CLOSED;

}
