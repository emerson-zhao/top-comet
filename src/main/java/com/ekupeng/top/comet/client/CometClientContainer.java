/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: CometClientContainer.java
 *
 */
package com.ekupeng.top.comet.client;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import com.ekupeng.top.comet.client.domain.CometStatus;

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
	 * 
	 * @return
	 */
	public CometStatus getCurrentCometStatus();

	/**
	 * 获取最近一次连接成功时间
	 * 
	 * @return
	 */
	public Date getLastStartUpTime();

	/**
	 * 获取最近心跳时间
	 * 
	 * @return
	 */
	public Date getLastHeartBeatTime();

	/**
	 * 获取消息总量
	 * 
	 * @return
	 */
	public long getMessageTotalCount();

	/**
	 * 获取从最近一次连接成功后得到的消息总量
	 * 
	 * @return
	 */
	public long getMessageTotalCountFromLastStartUp();

	/**
	 * 获取最近一次连接成功后得到的业务消息总量
	 * 
	 * @return
	 */
	public long getBizMessageTotalCountFromLastStartUp();

	/**
	 * 获取容器启动后得到的业务消息总量
	 * 
	 * @return
	 */
	public long getBizMessageTotalCount();

	/**
	 * 获取容器启动时间
	 * 
	 * @return
	 */
	public Date getStartUpTime();

}
