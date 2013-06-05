/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: CometClientContainer.java
 *
 */
package com.ekupeng.top.comet.client;

import java.io.IOException;

import com.ekupeng.top.comet.client.config.CometStatus;

/**
 * @Description: 长连接客户端容器：一个容器只维护一个长连接
 * @ClassName: CometClientContainer
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-6-2 下午5:23:37
 * @version V1.0
 */
public interface CometClientContainer {

	/**
	 * 启动容器
	 */
	public void start() throws IOException;

	/**
	 * 停止容器
	 */
	public void stop() throws IOException;
	
	
	/**
	 * 返回当前长连接的状态
	 * @return
	 */
	public CometStatus getCurrentCometStatus();

}
