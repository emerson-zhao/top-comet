/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: CometOperationCommand.java
 *
 */
package com.ekupeng.top.comet.client.domain;

/**
 * @Description: 长连接操作命令，由监听器处理完业务后返回给消息消费者，容器将根据该命令执行任务
 * @ClassName: CometOperationCommand
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-6-4 下午7:52:59
 * @version V1.0
 */
public enum CometOperationCommand {
	/*
	 * 关闭
	 */
	CLOSE,
	/*
	 * 忽略
	 */
	IGNORE,
	/*
	 * 重连
	 */
	RECONNECT
}
