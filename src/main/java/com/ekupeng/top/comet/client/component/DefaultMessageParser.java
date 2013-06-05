/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: DefaultMessageParser.java
 *
 */
package com.ekupeng.top.comet.client.component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ekupeng.top.comet.client.Message;
import com.ekupeng.top.comet.client.config.MessageType;

/**
 * @Description: 默认消息解析器实现
 * @ClassName: DefaultMessageParser
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-6-3 下午12:15:54
 * @version V1.0
 */
@Component("messageParser")
public class DefaultMessageParser implements MessageParser {

	/*
	 * 消息格式模式参见
	 * http://open.taobao.com/doc/detail.htm?spm=0.0.0.0.xWmYUR&id=101208#s2
	 */
	private Pattern pattern = Pattern
			.compile("\\{\"packet\":\\{\"code\":(\\d+)(,\"msg\":(.+))?\\}\\}");

	/*
	 * Override
	 */
	@Override
	public Message<String> parse(String rawJson) {
		if (StringUtils.hasText(rawJson)) {
			Matcher matcher = pattern.matcher(rawJson);
			Message<String> message = new Message<String>();
			message.setRawJson(rawJson);
			if (matcher.find()) {
				String code = matcher.group(1);
				String msg = matcher.group(3);
				message.setMessageType(MessageType.getMessageTypeByCode(code));
				message.setMessage(msg);
			}
			return message;
		} else {
			return null;
		}
	}

}
