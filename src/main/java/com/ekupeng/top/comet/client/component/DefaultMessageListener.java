/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: DefaultMessageListener.java
 *
 */
package com.ekupeng.top.comet.client.component;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.ekupeng.top.comet.client.Message;
import com.ekupeng.top.comet.client.domain.CometOperationCommand;
import com.ekupeng.top.comet.client.domain.ContainerConfiguration;
import com.ekupeng.top.comet.client.domain.MessageType;

/**
 * @Description: 默认消息监听器实现
 * @ClassName: DefaultMessageListener
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-6-3 下午12:18:29
 * @version V1.0
 */
@Component("messageListener")
public class DefaultMessageListener implements MessageListener,
		InitializingBean {

	@Autowired
	private ContainerConfiguration containerConfiguration;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Value("${discard.message.queue}")
	private String discardRoutingKey;

	@Value("${biz.message.queue}")
	private String bizRoutingKey;

	@Value("${error.message.queue}")
	private String errorRoutingKey;

	@Value("${exchange.name}")
	private String exchangeName;

	@Value("${appID}")
	private String appId;

	/*
	 * 文件日志
	 */
	private final static Logger logger = LoggerFactory
			.getLogger(DefaultMessageListener.class);
	/*
	 * 邮件日志
	 */
	private final static Logger topCometEmailLogger = LoggerFactory
			.getLogger("topCometEmail");
	private final static Marker topCometMarker = MarkerFactory
			.getMarker("topCometMarker");

	/*
	 * Override
	 */
	@Override
	public CometOperationCommand onConnected(Message message) {
		if (message == null)
			return CometOperationCommand.IGNORE;
		String groupId = String.valueOf(System.currentTimeMillis());
		if (topCometEmailLogger.isInfoEnabled()) {
			topCometEmailLogger.info(
					topCometMarker,
					getEmailInfo(groupId, containerConfiguration, message
							.getMessageType().getCode(), message
							.getMessageType().getDescription(), true));
		}
		return CometOperationCommand.IGNORE;
	}

	/*
	 * Override
	 */
	@Override
	public CometOperationCommand onHeartBeat(Message message) {
		return CometOperationCommand.IGNORE;
	}

	/*
	 * Override
	 */
	@Override
	public CometOperationCommand onBizMessage(Message message) {
		// 将消息发送到业务消息的队列
		rabbitTemplate.convertAndSend(exchangeName, bizRoutingKey, message);
		return CometOperationCommand.IGNORE;
	}

	/*
	 * Override
	 */
	@Override
	public CometOperationCommand onMessageDiscard(Message message) {
		// 将消息发送到丢失消息的队列
		rabbitTemplate.convertAndSend(exchangeName, discardRoutingKey, message);
		return CometOperationCommand.IGNORE;
	}

	/*
	 * Override
	 */
	@Override
	public CometOperationCommand onClientMaxTime(Message message) {
		String groupId = String.valueOf(System.currentTimeMillis());
		if (topCometEmailLogger.isInfoEnabled()) {
			topCometEmailLogger.info(
					topCometMarker,
					getEmailInfo(groupId, containerConfiguration, message
							.getMessageType().getCode(), message
							.getMessageType().getDescription(), true));
		}
		// 将消息发送到错误消息的队列
		rabbitTemplate.convertAndSend(exchangeName, errorRoutingKey, message
				+ "$" + new Date());
		return CometOperationCommand.RECONNECT;
	}

	/*
	 * Override
	 */
	@Override
	public CometOperationCommand onServerUpgrade(Message message) {
		String groupId = String.valueOf(System.currentTimeMillis());
		topCometEmailLogger.error(
				topCometMarker,
				getEmailInfo(groupId, containerConfiguration, message
						.getMessageType().getCode(), message.getMessageType()
						.getDescription(), true));
		// 将消息发送到错误消息的队列
		rabbitTemplate.convertAndSend(exchangeName, errorRoutingKey, message
				+ "$" + new Date());
		// 系统配置的默认等待时间，单位是毫秒
		long waitSecond = containerConfiguration
				.getSleepTimeOfServerInUpgrade();
		// 解析返回消息中服务器指定的等待时间，单位是秒
		Object o = message.getMessage();
		if (o != null) {
			String strSec = (String) o;
			try {
				// 若服务器指定等待时间，则使用服务器的时间，将秒转化为毫秒
				waitSecond = Long.valueOf(strSec) * 1000;
			} catch (NumberFormatException e) {
				waitSecond = containerConfiguration
						.getSleepTimeOfServerInUpgrade();
			}
		}
		try {
			Thread.sleep(waitSecond);
		} catch (InterruptedException e) {
			// ignore
		}
		// 等待结束后让容器进行重新连接
		return CometOperationCommand.RECONNECT;

	}

	/*
	 * Override
	 */
	@Override
	public CometOperationCommand onServerError(Message message) {
		String groupId = String.valueOf(System.currentTimeMillis());
		topCometEmailLogger.error(
				topCometMarker,
				getEmailInfo(groupId, containerConfiguration, message
						.getMessageType().getCode(), message.getMessageType()
						.getDescription(), true));
		// 将消息发送到错误消息的队列
		rabbitTemplate.convertAndSend(exchangeName, errorRoutingKey, message
				+ "$" + new Date());
		// 默认等待时间1分钟，单位是毫秒
		long waitSecond = 60 * 1000;
		// 解析返回消息中服务器指定的等待时间，单位是秒
		Object o = message.getMessage();
		if (o != null) {
			String strSec = (String) o;
			try {
				// 若服务器指定等待时间，则使用服务器的时间，将秒转化为毫秒
				waitSecond = Long.valueOf(strSec) * 1000;
			} catch (NumberFormatException e) {
				waitSecond = 60 * 1000;
			}
		}
		try {
			Thread.sleep(waitSecond);
		} catch (InterruptedException e) {
			// ignore
		}
		// 等待结束后让容器进行重新连接
		return CometOperationCommand.RECONNECT;
	}

	/*
	 * Override
	 */
	@Override
	public CometOperationCommand onDuplicateConnection() {
		String groupId = String.valueOf(System.currentTimeMillis());
		String msg = "通常是由于达到最长连接时间，客户端自动重连造成，你应该会先收到一封#连接达到最大时间#的邮件，如果没有收到此类邮件，很可能本连接被不明连接挤占，请关注当前的服务器消息长连接状态~";
		topCometEmailLogger.error(
				topCometMarker,
				getEmailInfo(groupId, containerConfiguration,
						MessageType.DUPLICATE_CONNECTION.getCode(), msg, true));
		// 将消息发送到错误消息的队列
		rabbitTemplate.convertAndSend(exchangeName, errorRoutingKey,
				MessageType.DUPLICATE_CONNECTION.getCode() + "$"
						+ MessageType.DUPLICATE_CONNECTION.getDescription()
						+ "$" + new Date());
		return CometOperationCommand.IGNORE;
	}

	/*
	 * Override
	 */
	@Override
	public CometOperationCommand onSlowConsumer() {
		String groupId = String.valueOf(System.currentTimeMillis());
		topCometEmailLogger.error(
				topCometMarker,
				getEmailInfo(groupId, containerConfiguration,
						MessageType.SLOW_CONSUMER.getCode(),
						MessageType.SLOW_CONSUMER.getDescription(), true));
		// 将消息发送到错误消息的队列
		rabbitTemplate.convertAndSend(exchangeName, errorRoutingKey,
				MessageType.SLOW_CONSUMER.getCode() + "$"
						+ MessageType.SLOW_CONSUMER.getDescription() + "$"
						+ new Date());
		return CometOperationCommand.RECONNECT;
	}

	/*
	 * Override
	 */
	@Override
	public CometOperationCommand onOtherMessage(Message message) {
		String groupId = String.valueOf(System.currentTimeMillis());
		topCometEmailLogger.error(
				topCometMarker,
				getEmailInfo(groupId, containerConfiguration, message
						.toString(), message.getMessageType().getDescription(),
						true));
		// 将消息发送到错误消息的队列
		rabbitTemplate.convertAndSend(exchangeName, errorRoutingKey, message
				+ "$" + new Date());
		return CometOperationCommand.IGNORE;
	}

	/*
	 * Override
	 */
	@Override
	public CometOperationCommand onException(String msg, Exception e) {
		String groupId = String.valueOf(System.currentTimeMillis());
		topCometEmailLogger
				.error(topCometMarker,
						getEmailInfo(groupId, containerConfiguration, msg,
								"出现异常", true), e);
		// 将消息发送到错误消息的队列
		rabbitTemplate.convertAndSend(exchangeName, errorRoutingKey, msg + "$"
				+ e.getMessage() + "$" + new Date());
		return CometOperationCommand.IGNORE;
	}

	public void setContainerConfiguration(
			ContainerConfiguration containerConfiguration) {
		this.containerConfiguration = containerConfiguration;
	}

	/**
	 * 拼装邮件日志的内容
	 * 
	 * @param groupId
	 *            业务分组id，由于两个并行的业务邮件会交错，不容易区分多个交错业务涉及信息（比如邮件或日志）的分组，
	 *            此ID用于区分同一业务行为产生的信息
	 * @param configuration
	 * @param serverResponseCode
	 * @param msg
	 * @param isImportant
	 * @return
	 */
	private String getEmailInfo(String groupId,
			ContainerConfiguration containerConfiguration,
			String serverResponseCode, String msg, boolean isImportant) {
		Assert.notNull(containerConfiguration);
		String info = "GroupID:" + groupId;
		if (isImportant)
			info += "<br>  ##请关注##  <br>";
		info += "  服务器响应状态码或长连接的当前状态：" + serverResponseCode + " , <br> 消息内容："
				+ msg + " ,<br> 当前Comet长连接参数如下：ip："
				+ containerConfiguration.getLocalIP() + " ,<br> appkey："
				+ containerConfiguration.getCometConfiguration().getAppkey()
				+ " ,<br> 连接id："
				+ containerConfiguration.getCometConfiguration().getId()
				+ " ,<br> userId："
				+ containerConfiguration.getCometConfiguration().getUserid()
				+ " ,<br> 当前内容生成时间：" + new Date() + " 。<br>（请根据groupId: "
				+ groupId + " 查询日志以获取详细信息！）";
		return info;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		appId = appId.toUpperCase();
		discardRoutingKey = appId + "." + discardRoutingKey.toUpperCase();
		bizRoutingKey = appId + "." + bizRoutingKey.toUpperCase();
		errorRoutingKey = appId + "." + errorRoutingKey.toUpperCase();
		exchangeName = appId + "." + exchangeName.toUpperCase();
	}

}
