/**
 *  BizMessageListener.java
 *
 *  Copyright ekupeng,Inc. 2012-2013   
 */
package com.ekupeng.top.comet.client.queue;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ekupeng.top.comet.client.Message;
import com.ekupeng.top.comet.client.domain.BizMessageType;
import com.ekupeng.top.comet.client.domain.TradeMessageType;
import com.taobao.api.domain.NotifyTrade;

/**
 * @Description: 业务消息监听器
 * @ClassName: BizMessageListener
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-6-5 下午7:45:07
 * @version V1.0
 */
@Service("bizMessageListener")
public class BizMessageListener implements InitializingBean {

	/**
	 * some routing-key
	 */
	@Value("${trade.created}")
	private String tradeCreated;
	@Value("${trade.paid}")
	private String tradePaid;
	@Value("${trade.success}")
	private String tradeSuccess;
	@Value("${trade.closed}")
	private String tradeClosed;
	@Value("${trade.sent}")
	private String tradeSent;
	@Value("${task.complete}")
	private String taskComplete;
	@Value("${appID}")
	private String appId;
	@Value("${exchange.name}")
	private String exchangeName;
	@Autowired
	private RabbitTemplate rabbitTemplate;

	public void onMessage(Object m) {
		if (m == null)
			return;
		if (m instanceof Message) {
			String jsonString = ((Message<String>) m).getMessage();
			if (JSONUtil.getBizMessageType(jsonString).equals(
					BizMessageType.TRADE_MESSAGE)) {
				NotifyTrade trade = JSONUtil.getNotifyTrade(jsonString);
				String status = trade.getStatus();
				// 目前只使用了创建、付款、成功
				if (TradeMessageType.TRADE_CREATE.getType().equals(status)) {
					rabbitTemplate.convertAndSend(exchangeName, tradeCreated,
							jsonString);
				} else if (TradeMessageType.TRADE_BUYER_PAY.getType().equals(
						status)) {
					rabbitTemplate.convertAndSend(exchangeName, tradePaid,
							jsonString);
				} else if (TradeMessageType.TRADE_SUCCESS.getType().equals(
						status)) {
					rabbitTemplate.convertAndSend(exchangeName, tradeSuccess,
							jsonString);
				}
			} else if (JSONUtil.getBizMessageType(jsonString).equals(
					BizMessageType.ITEM_MESSAGE)) {
				// 宝贝消息相关
				// NotifyItem trade = JSONUtil.getNotifyItem(jsonString);
				// TODO
			} else if (JSONUtil.getBizMessageType(jsonString).equals(
					BizMessageType.REFUND_MESSAGE)) {
				// 退款消息相关
				// NotifyRefund trade = JSONUtil.getNotifyRefund(jsonString);
				// TODO
			} else if (JSONUtil.getBizMessageType(jsonString).equals(
					BizMessageType.TASK_MESSAGE)) {
				// 任务消息相关
				// NotifyTopats trade = JSONUtil.getNotifyTopats(jsonString);
				// TODO
			} else {
				return;
			}
		}

	}

	public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		appId = appId.toUpperCase();
		tradeCreated = appId + "." + tradeCreated;
		tradePaid = appId + "." + tradePaid;
		tradeSuccess = appId + "." + tradeSuccess;
		tradeClosed = appId + "." + tradeClosed;
		tradeSent = appId + "." + tradeSent;
		taskComplete = appId + "." + taskComplete;
		exchangeName = appId + "." + exchangeName.toUpperCase();
	}

	public void setTradeCreated(String tradeCreated) {
		this.tradeCreated = tradeCreated;
	}

	public void setTradePaid(String tradePaid) {
		this.tradePaid = tradePaid;
	}

	public void setTradeSuccess(String tradeSuccess) {
		this.tradeSuccess = tradeSuccess;
	}

	public void setTradeClosed(String tradeClosed) {
		this.tradeClosed = tradeClosed;
	}

	public void setTradeSent(String tradeSent) {
		this.tradeSent = tradeSent;
	}

	public void setTaskComplete(String taskComplete) {
		this.taskComplete = taskComplete;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}

}
