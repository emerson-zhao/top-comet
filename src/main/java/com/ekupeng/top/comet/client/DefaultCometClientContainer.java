/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: DefaultCometClientContainer.java
 *
 */
package com.ekupeng.top.comet.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.ekupeng.top.comet.client.component.CometConnectClient;
import com.ekupeng.top.comet.client.component.CometStatusMonitor;
import com.ekupeng.top.comet.client.component.MessageListener;
import com.ekupeng.top.comet.client.component.MessageParser;
import com.ekupeng.top.comet.client.config.CometOperationCommand;
import com.ekupeng.top.comet.client.config.CometStatus;
import com.ekupeng.top.comet.client.config.ContainerConfiguration;
import com.ekupeng.top.comet.client.config.MessageType;

/**
 * @Description: 长连接客户端容器默认实现:一个容器同一时间仅维护一个连接
 * @ClassName: DefaultCometClientContainer
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-6-3 上午11:30:36
 * @version V1.0
 */
@Component("cometClientContainer")
public class DefaultCometClientContainer implements CometClientContainer {

	/*
	 * 文件日志
	 */
	private final static Logger logger = LoggerFactory
			.getLogger(DefaultCometClientContainer.class);
	/*
	 * 邮件日志
	 */
	private final static Logger topCometEmailLogger = LoggerFactory
			.getLogger("topCometEmail");
	private final static Marker topCometMarker = MarkerFactory
			.getMarker("topCometMarker");

	/*
	 * 容器配置
	 */
	@Autowired
	private ContainerConfiguration containerConfiguration;

	/*
	 * 消息解析器
	 */
	@Autowired
	private MessageParser messageParser;

	/*
	 * 长连接客户端
	 */
	@Autowired
	private CometConnectClient cometConnectClient;

	/*
	 * 消息监听器
	 */
	@Autowired
	private MessageListener messageListener;

	/*
	 * 长连接状态监听器
	 */
	@Autowired
	private CometStatusMonitor cometStatusMonitor;

	/*
	 * 容器停止标志
	 */
	private volatile boolean stop = true;

	/*
	 * 消息流
	 */
	private BufferedReader messageReader;

	/*
	 * 消费者线程池
	 */
	private ThreadPoolExecutor consumerPool;

	/*
	 * 长连接状态
	 */
	private volatile CometStatusWrapper cometStatus = new CometStatusWrapper();

	/*
	 * 锁
	 */
	final static private Object MUTEX = new Object();

	/*
	 * Override
	 */
	@Override
	public synchronized void start() throws IOException {
		if (!stop) {
			return;
		} else {
			stop = false;
		}
		requiredComponentValidate();
		// 初始化消费者线程池
		initConsumerPool(containerConfiguration);
		// 启动连接控制线程
		Thread connectionController = new Thread(new ConnectionController(),
				"connectionController-"
						+ String.valueOf(System.currentTimeMillis()));
		connectionController.setDaemon(true);
		connectionController.start();

	}

	/*
	 * Override
	 */
	@Override
	public void stop() {
		String groupId = String.valueOf(System.currentTimeMillis());
		stop = true;
		cometStatus.setCurrentCometStatus(CometStatus.CLOSED);
		try {
			cometConnectClient.closeConnection();
		} catch (IllegalStateException e) {
			logger.error("长连接Client关闭异常", e);
		} catch (IOException e) {
			logger.error("长连接Client关闭异常", e);
		}
		consumerPool.shutdown();
		String msg = "CometClientContainer的stop方法被调用，容器关闭！";
		if (topCometEmailLogger.isInfoEnabled()) {
			topCometEmailLogger.info(
					topCometMarker,
					getEmailInfo(groupId, containerConfiguration,
							CometStatus.CLOSED.toString(), msg, true));
		}
	}

	/**
	 * 启动长连接
	 * 
	 * @param groupId
	 *            业务分组id，由于两个并行的业务邮件会交错，不容易区分多个交错业务涉及信息（比如邮件或日志）的分组，
	 *            此ID用于区分同一业务行为产生的信息
	 * @throws IOException
	 */
	private synchronized void doStart(String groupId) throws IOException {
		int retryCount = 0;
		int targetRetryCount = containerConfiguration.getConnectRetryCount();
		int retryInterval = containerConfiguration.getConnectRetryInterval();
		IOException ioe = null;
		// 修改当前长连接的状态为连接中状态
		cometStatus.setCurrentCometStatus(CometStatus.RECONNECTING);
		while (retryCount <= targetRetryCount) {
			try {
				InputStream inputStream = cometConnectClient
						.openNewConnection();
				messageReader = new BufferedReader(new InputStreamReader(
						inputStream, "UTF-8"));
				break;
			} catch (IOException e) {
				ioe = e;
				String msg = "";
				if (retryCount == 0) {
					msg = "长连接首次连接失败，准备重试：";
				} else {
					msg = "长连接第" + retryCount + "次重新连接失败：";
				}
				logger.error(getExceptionMsgByGroupId(groupId, msg), e);
				retryCount++;
				if (retryCount <= targetRetryCount) {
					try {
						Thread.sleep(retryInterval);
					} catch (InterruptedException ie) {
						// ignore
					}
				} else {
					// 如果重试次数满后失败，修改当前长连接的状态为关闭状态状态，并抛出异常
					cometStatus.setCurrentCometStatus(CometStatus.CLOSED);
					String errMsg = "建立长连接失败,重试" + (retryCount - 1)
							+ "次连接后仍无法成功";
					throw new IOException(errMsg, ioe);
				}
			} catch (RuntimeException e) {
				cometStatus.setCurrentCometStatus(CometStatus.CLOSED);
				throw e;
			}
		}
		// 修改当前长连接的状态为已连接状态
		cometStatus.setCurrentCometStatus(CometStatus.CONNECTED);
	}

	/**
	 * 初始化消费者线程池
	 * 
	 * @param containerConfiguration2
	 */
	private void initConsumerPool(ContainerConfiguration containerConfiguration) {
		Assert.notNull(containerConfiguration);
		if (this.consumerPool != null && !this.consumerPool.isShutdown()) {
			consumerPool.shutdown();
		}
		this.consumerPool = new ThreadPoolExecutor(
				containerConfiguration.getConsumerThreadsCore(),
				containerConfiguration.getConsumerThreadsMax(), 60,
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(
						containerConfiguration.getMessageTaskQueueSize()));
	}

	/**
	 * 对容器必要的组件进行检查，确保都已初始化
	 */
	private void requiredComponentValidate() {
		if (containerConfiguration == null)
			throw new RuntimeException("容器相关配置不能为空！");
		if (messageParser == null)
			throw new RuntimeException("消息解析器不能为空！");
		if (cometConnectClient == null)
			throw new RuntimeException("长连接客户端不能为空！");
		if (messageListener == null)
			throw new RuntimeException("消息监听器布恩那个为空！");
		// 如果配置了监控器，则设置监控器
		if (cometStatusMonitor != null) {
			cometStatus.setCometStatusMonitor(cometStatusMonitor);
		}
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

	/**
	 * 按groupId格式获取异常消息
	 * 
	 * @param groupId
	 * @param msg
	 * @return
	 */
	private String getExceptionMsgByGroupId(String groupId, String msg) {
		String prefix = "  @groupId=" + groupId + "@  ";
		return prefix + "  ---  message:" + msg;
	}

	/**
	 * 执行操作命令
	 * 
	 * @param command
	 */
	private void executeCommand(CometOperationCommand command) {
		if (command == null || command.equals(CometOperationCommand.IGNORE)) {
			return;
		} else if (command.equals(CometOperationCommand.CLOSE)) {
			stop();
		} else if (command.equals(CometOperationCommand.RECONNECT)) {
			cometStatus.setCurrentCometStatus(CometStatus.RECONNECTING);
			MUTEX.notifyAll();
		}
	}

	@Override
	public CometStatus getCurrentCometStatus() {
		return cometStatus.getCurrentCometStatus();
	}

	public void setContainerConfiguration(
			ContainerConfiguration containerConfiguration) {
		this.containerConfiguration = containerConfiguration;
	}

	public void setMessageParser(MessageParser messageParser) {
		this.messageParser = messageParser;
	}

	public void setCometConnectClient(CometConnectClient cometConnectClient) {
		this.cometConnectClient = cometConnectClient;
	}

	public void setMessageListener(MessageListener messageListener) {
		this.messageListener = messageListener;
	}

	public void setCometStatusMonitor(CometStatusMonitor cometStatusMonitor) {
		this.cometStatusMonitor = cometStatusMonitor;
	}

	/**
	 * @Description: 连接控制器
	 * @ClassName: ConnectionController
	 * @author emerson <emsn1026@gmail.com>
	 * @date 2013-6-3 下午5:37:31
	 * @version V1.0
	 */
	class ConnectionController implements Runnable {

		@Override
		public void run() {
			// 如果没有关闭，则循环运行
			while (!stop) {
				// 如果当前状态是关闭状态或连接中(客户端超时重连时)，则建立长连接
				if (cometStatus.getCurrentCometStatus().equals(
						CometStatus.CLOSED)
						|| cometStatus.getCurrentCometStatus().equals(
								CometStatus.RECONNECTING)) {
					// 用时间戳来表示当前业务分组ID
					String groupId = String.valueOf(System.currentTimeMillis());
					if (topCometEmailLogger.isInfoEnabled()) {
						String info = "准备建立长连接";
						topCometEmailLogger.info(
								topCometMarker,
								getEmailInfo(groupId, containerConfiguration,
										cometStatus.getCurrentCometStatus()
												.toString(), info, false));
					}
					// 启动长连接
					try {
						doStart(groupId);
					} catch (Exception e) {
						String msg = "启动长连接失败：" + e.toString();
						logger.error(getExceptionMsgByGroupId(groupId, msg), e);
						topCometEmailLogger.error(
								topCometMarker,
								getEmailInfo(groupId, containerConfiguration,
										"4XX", msg, true));
						// 启动失败，可能是网络等临时性原因造成，休眠系统指定时间后重试
						long sleepTime = containerConfiguration
								.getStartErrorRetryInterval();
						if (sleepTime == 0) {
							sleepTime = 5 * 60 * 1000;
						}
						logger.error("连接重试次数"
								+ containerConfiguration
										.getStartErrorRetryInterval()
								+ " 次已满，仍无法成功启动，可能是由于网络环境问题造成，当前时间是  "
								+ new Date() + " ，根据系统配置，休眠" + sleepTime / 1000
								/ 60 + " 分钟后再重启！");
						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e1) {
							// ignore
						}
						// 休眠结束后重试
						logger.error("休眠结束,当前时间是  " + new Date()
								+ " ，容器将开始自动重启进行连接！");
						cometStatus
								.setCurrentCometStatus(CometStatus.RECONNECTING);
						continue;
					}
					// 启动消息分发器线程
					Thread dispatcher = new Thread(new MessageDispatcher(
							Thread.currentThread()), "Dispatcher-" + groupId);
					dispatcher.setDaemon(true);
					dispatcher.start();
				} else if (cometStatus.getCurrentCometStatus().equals(
						CometStatus.CONNECTED)) {
					// 如果是已连接状态则开始休眠
					try {
						synchronized (MUTEX) {
							// 休眠至客户端超时，即服务器断开之前
							MUTEX.wait(containerConfiguration
									.getReconnectInterval());
							// 使客户端开始重连
							cometStatus
									.setCurrentCometStatus(CometStatus.RECONNECTING);
						}
					} catch (InterruptedException e) {
						// ingore
					}
				} else {
					String groupId = String.valueOf(System.currentTimeMillis());
					String msg = "当前Comet状态异常，为："
							+ ((cometStatus == null) ? null : cometStatus
									.getCurrentCometStatus());
					logger.error(getExceptionMsgByGroupId(groupId, msg));
					// 关闭容器
					stop();
					throw new RuntimeException(msg);
				}
			}
			// 为避免数据不一致，当标识符为停止时，再次关闭所有内容
			if (stop) {
				stop();
			}
		}
	}

	/**
	 * @Description: 消息分发线程
	 * @ClassName: MessageDispatcher
	 * @author emerson <emsn1026@gmail.com>
	 * @date 2013-6-4 下午6:29:52
	 * @version V1.0
	 */
	class MessageDispatcher implements Runnable {
		/*
		 * 创建本线程的连接控制线程句柄
		 */
		Thread connectionController;

		public MessageDispatcher(Thread connectionController) {
			super();
			this.connectionController = connectionController;
		}

		@Override
		public void run() {
			try {
				while (!stop) {
					String msg = messageReader.readLine();
					consumerPool.submit(new Consumer(msg));
				}
			} catch (Exception e) {
				String groupId = String.valueOf(System.currentTimeMillis());
				String msg = "消息流读取或消费出现异常";
				logger.error(getExceptionMsgByGroupId(groupId, msg), e);
				try {
					topCometEmailLogger.error(
							topCometMarker,
							getEmailInfo(groupId, containerConfiguration,
									"5XX", msg, true));
				} finally {
					cometStatus.setCurrentCometStatus(CometStatus.RECONNECTING);
					connectionController.interrupt();
				}
			}
		}

	}

	/**
	 * @Description: 消息消费者
	 * @ClassName: Consumer
	 * @author emerson <emsn1026@gmail.com>
	 * @date 2013-6-3 下午7:12:28
	 * @version V1.0
	 */
	class Consumer implements Runnable {
		/*
		 * 待消费的消息
		 */
		private String msg;

		public Consumer(String msg) {
			super();
			this.msg = msg;
		}

		@Override
		public void run() {
			// 解析消息
			try {
				Message<String> message = messageParser.parse(msg);
				System.out.println(message);
				if (message == null)
					return;
				// 根据不同的消息类型，调用监听器进行处理
				MessageType type = message.getMessageType();
				CometOperationCommand command = CometOperationCommand.IGNORE;
				if (MessageType.BIZ_MESSAGE.equals(type)) {
					command = messageListener.onBizMessage(message);
				} else if (MessageType.CLIENT_MAX_TIME.equals(type)) {
					command = messageListener.onClientMaxTime(message);
				} else if (MessageType.CONNECTED.equals(type)) {
					command = messageListener.onConnected(message);
				} else if (MessageType.DUPLICATE_CONNECTION.equals(type)) {
					command = messageListener.onDuplicateConnection();
				} else if (MessageType.HEART_BEAT.equals(type)) {
					command = messageListener.onHeartBeat(message);
				} else if (MessageType.MESSAGE_DISCARD.equals(type)) {
					command = messageListener.onMessageDiscard(message);
				} else if (MessageType.OTHER_MESSAGE.equals(type)) {
					command = messageListener.onOtherMessage(message);
				} else if (MessageType.SERVER_ERROR.equals(type)) {
					command = messageListener.onServerError(message);
				} else if (MessageType.SERVER_UPGRADE.equals(type)) {
					command = messageListener.onServerUpgrade(message);
				} else if (MessageType.SLOW_CONSUMER.equals(type)) {
					command = messageListener.onSlowConsumer();
				} else {
					command = messageListener.onOtherMessage(message);
				}
				// 执行监听器处理完业务后返回的需要容器执行的命令
				executeCommand(command);
			} catch (Exception e) {
				messageListener.onException(msg, e);
			}
		}
	}
}
