/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: Message.java
 *
 */
package com.ekupeng.top.comet.client;

import com.ekupeng.top.comet.client.config.MessageType;

/**
 * @Description: 消息
 * @ClassName: Message
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-6-2 下午5:40:45
 * @version V1.0
 */
public class Message<T> {

	/**
	 * 对应的消息类型
	 */
	private MessageType messageType;
	/**
	 * 消息内容
	 */
	private T message;
	/**
	 * 原始json数据
	 */
	private String rawJson;

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public T getMessage() {
		return message;
	}

	public void setMessage(T message) {
		this.message = message;
	}

	public String getRawJson() {
		return rawJson;
	}

	public void setRawJson(String rawJson) {
		this.rawJson = rawJson;
	}

	@Override
	public String toString() {
		return "Message [messageType=" + messageType + ", message=" + message
				+ ", rawJson=" + rawJson + "]";
	}
	
	
}
