/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: MessageListener.java
 *
 */
package com.ekupeng.top.comet.client.component;

import com.ekupeng.top.comet.client.Message;
import com.ekupeng.top.comet.client.domain.CometOperationCommand;

/**
 * @Description: 淘宝长连接消息监听器接口，仅处理淘宝的Raw消息，不涉及任何业务处理
 *               消息的返回类型及对应的状态见：http://open.taobao
 *               .com/doc/detail.htm?spm=0.0.0.0.llAnA5&id=101208#s2
 * @ClassName: MessageListener
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-6-2 下午6:57:24
 * @version V1.0
 */
public interface MessageListener {

	/**
	 * 200 接受连接:连接成功（服务端接受连接并且连接保持24小时）
	 * 
	 * @param message
	 */
	public CometOperationCommand onConnected(Message message);

	/**
	 * 201 心跳包
	 * 
	 * @param message
	 */
	public CometOperationCommand onHeartBeat(Message message);

	/**
	 * 202 推送的业务消息
	 * 
	 * @param message
	 */
	public CometOperationCommand onBizMessage(Message message);

	/**
	 * 203 消息被丢弃的通知
	 * 
	 * @param message
	 */
	public CometOperationCommand onMessageDiscard(Message message);

	/**
	 * 101 长连接时间达到最大值，服务器断开客户端，需要客户端进行重连
	 * 
	 * @param message
	 */
	public CometOperationCommand onClientMaxTime(Message message);

	/**
	 * 102 服务端因升级断开客户端，需要等待重连
	 * 
	 * @param message
	 */
	public CometOperationCommand onServerUpgrade(Message message);

	/**
	 * 103 服务端由于某些问题断开客户端，需要等待重连
	 * 
	 * @param message
	 */
	public CometOperationCommand onServerError(Message message);

	/**
	 * 104 由于存在相同的连接参数，导致本链接被另一连接挤占下线
	 */
	public CometOperationCommand onDuplicateConnection();

	/**
	 * 105 由于消息接受过慢，服务端断开客户端
	 */
	public CometOperationCommand onSlowConsumer();

	/**
	 * 处理未知消息
	 * 
	 * @param message
	 */
	public CometOperationCommand onOtherMessage(Message message);

	/**
	 * 处理异常
	 * 
	 * @param e
	 */
	public CometOperationCommand onException(String msg, Exception e);

}
