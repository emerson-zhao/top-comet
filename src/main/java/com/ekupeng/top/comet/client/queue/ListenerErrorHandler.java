/**
 *  ListenerErrorHandler.java
 *
 *  Copyright ekupeng,Inc. 2012-2013   
 */
package com.ekupeng.top.comet.client.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.ErrorHandler;

/**
 * @ClassName: ListenerErrorHandler
 * @Description: 处理消息容器未能处理的异常
 * @author Emerson emsn1026@gmail.com
 * @date 2012-7-27 下午10:29:38
 * @version V1.0
 * 
 */
public class ListenerErrorHandler implements ErrorHandler {

	static Logger logger = LoggerFactory.getLogger(ListenerErrorHandler.class);

	private RabbitTemplate rabbitTemplate;

	private String routingKey;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.util.ErrorHandler#handleError(java.lang.Throwable)
	 */
	@Override
	public void handleError(Throwable e) {
		logger.error(e.getMessage(), e);
	}

	public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}

}
