/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: MessageType.java
 *
 */
package com.ekupeng.top.comet.client.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 消息类型枚举 <a
 *               href="http://open.taobao.com/doc/detail.htm?spm=0.0.0.0 .522J8W
 *               &id=101208#s2" target="_blank">参考淘宝官方文档</a>
 * @ClassName: MessageType
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-6-2 下午5:42:56
 * @version V1.0
 */
public enum MessageType {

	CONNECTED("200", "连接成功（服务端接受连接并且连接保持24小时）"),

	HEART_BEAT(
			"201",
			"服务端发送心跳包，防止连接被断开。msg中的次数表示从连接被接收到当前发送这个心跳包之间的这段时间，共发送了多少个业务消息包，app需要设置读取超时时间为1分钟左右，如果超过1分钟都没读到数据，说明网络可能有问题了，app需要重新发起连接"),

	BIZ_MESSAGE("202",
			"业务消息包。app只需取出业务消息包内容，并作业务逻辑处理，建议：接收数据的线程和处理数据的线程最好分开，不要影响接收度"),

	MESSAGE_DISCARD("203", "app有消息丢弃。服务端发现app的连接断开后会记录下来app的消息丢弃情况"),

	CLIENT_MAX_TIME("101", "连接到达最大时间"),

	SERVER_UPGRADE("102",
			"服务端在升级。 msg表示服务端升级大概需要的时间，单位：秒。app在这段时间之后重新连接服务端，并且使用增量api把这段时间内丢失的消息获取到"),

	SERVER_ERROR(
			"103",
			"由于某些原因服务端出现了一些问题，需要断开客户端。msg表示建议app在多少s之后发起新的请求连接，app可以选择马上发起新的连接请求，也可以在一段时间后发起连接请求"),

	DUPLICATE_CONNECTION(
			"104",
			"由于客户端发起了重复的连接请求，服务端会把前一个连接主动断开。app在新的连接上接收消息，并且把前一个连接上剩余的消息接收完。（可能104包不是之前连接上最后的包，所以最好在写代码的时候读到null后才结束）"),

	SLOW_CONSUMER(
			"105",
			"由于app的消息量太大，但是app的与TOP之间的网络不太好，或者app接收消息太慢。导致服务端有大量的消息积压，这种情况下服务端会断开连接。app端需要检查一下网络环境，或者接收消息和处理消息的线程有没有分开"),

	OTHER_MESSAGE("-1", "异常或淘宝新添加的消息类型，需要发现后手动添加到本消息类型枚举类");

	private String code;
	private String description;
	private static Map<String, MessageType> messageTypes = new HashMap<String, MessageType>();

	static {
		for (MessageType mt : MessageType.class.getEnumConstants()) {
			messageTypes.put(mt.code, mt);
		}
	}

	private MessageType(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * 通过Code获取类型
	 * 
	 * @param code
	 * @return
	 */
	public static MessageType getMessageTypeByCode(String code) {
		MessageType messageType = messageTypes.get(code);
		if (messageType == null)
			return OTHER_MESSAGE;
		return messageTypes.get(code);
	}

}
