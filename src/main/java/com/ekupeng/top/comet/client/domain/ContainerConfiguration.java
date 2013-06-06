/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: ContainerConfiguration.java
 *
 */
package com.ekupeng.top.comet.client.domain;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description: Comet客户端容器相关配置
 * @ClassName: ContainerConfiguration
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-6-2 下午5:32:25
 * @version V1.0
 */
@Component("containerConfiguration")
public class ContainerConfiguration {
	
	@Value("${appName}")
	private String appName;
	/*
	 * 客户端连接失败后的尝试次数
	 */
	@Value("${comet.container.connectRetryCount}")
	private int connectRetryCount = 3;

	/*
	 * 尝试间隔时间，单位：毫秒
	 */
	@Value("${comet.container.connectRetryInterval}")
	private int connectRetryInterval = 10 * 1000;

	/*
	 * 容器启动错误尝试间隔时间，单位：毫秒
	 */
	@Value("${comet.container.startErrorRetryInterval}")
	private int startErrorRetryInterval = 5 * 60 * 1000;

	/*
	 * 当服务端在发布时客户端的休眠时间，默认5分钟 ，单位：毫秒
	 */
	@Value("${comet.container.sleepTimeOfServerInUpgrade}")
	private long sleepTimeOfServerInUpgrade = 5 * 60 * 1000;

	/*
	 * 因为服务端每24小时会断开客户端连接，所以为了最低程度的降低消息丢失，在服务端断开之间客户端重连一次 ，默认是23小时加55分钟后自动重连
	 * ，单位：毫秒
	 */
	@Value("${comet.container.reconnectInterval}")
	private long reconnectInterval = (23 * 60 + 55) * 60 * 1000;

	/*
	 * 消息消费者线程池最小容量
	 */
	@Value("${comet.container.consumer.threadsCore}")
	private int consumerThreadsCore = 10;

	/*
	 * 消息消费者线程池最大容量
	 */
	@Value("${comet.container.consumer.threadsMax}")
	private int consumerThreadsMax = 200;

	/*
	 * 消息队列容量
	 */
	@Value("${comet.container.message.taskQueueSize}")
	private int messageTaskQueueSize = 50000;

	/*
	 * 长连接配置
	 */
	@Autowired
	private CometConfiguration cometConfiguration;

	/*
	 * 发起长连接的本地IP地址
	 */
	private String localIP;

	public ContainerConfiguration() {
		super();
		InetAddress address = null;
		try {
			address = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
		}
		this.localIP = address.getHostAddress();
	}

	public int getConnectRetryCount() {
		return connectRetryCount;
	}

	public int getConnectRetryInterval() {
		return connectRetryInterval;
	}

	public long getSleepTimeOfServerInUpgrade() {
		return sleepTimeOfServerInUpgrade;
	}

	public int getConsumerThreadsCore() {
		return consumerThreadsCore;
	}

	public int getConsumerThreadsMax() {
		return consumerThreadsMax;
	}

	public int getMessageTaskQueueSize() {
		return messageTaskQueueSize;
	}

	public String getLocalIP() {
		return localIP;
	}

	public void setLocalIP(String localIP) {
		this.localIP = localIP;
	}

	public CometConfiguration getCometConfiguration() {
		return cometConfiguration;
	}

	public long getReconnectInterval() {
		return reconnectInterval;
	}

	public void setReconnectInterval(long reconnectInterval) {
		this.reconnectInterval = reconnectInterval;
	}

	public void setConnectRetryCount(int connectRetryCount) {
		this.connectRetryCount = connectRetryCount;
	}

	public void setConnectRetryInterval(int connectRetryInterval) {
		this.connectRetryInterval = connectRetryInterval;
	}

	public void setSleepTimeOfServerInUpgrade(long sleepTimeOfServerInUpgrade) {
		this.sleepTimeOfServerInUpgrade = sleepTimeOfServerInUpgrade;
	}

	public void setConsumerThreadsCore(int consumerThreadsCore) {
		this.consumerThreadsCore = consumerThreadsCore;
	}

	public void setConsumerThreadsMax(int consumerThreadsMax) {
		this.consumerThreadsMax = consumerThreadsMax;
	}

	public void setMessageTaskQueueSize(int messageTaskQueueSize) {
		this.messageTaskQueueSize = messageTaskQueueSize;
	}

	public void setCometConfiguration(CometConfiguration cometConfiguration) {
		this.cometConfiguration = cometConfiguration;
	}

	public int getStartErrorRetryInterval() {
		return startErrorRetryInterval;
	}

	public void setStartErrorRetryInterval(int startErrorRetryInterval) {
		this.startErrorRetryInterval = startErrorRetryInterval;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

}
