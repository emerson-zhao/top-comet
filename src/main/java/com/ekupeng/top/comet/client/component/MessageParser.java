/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: MessageParser.java
 *
 */
package com.ekupeng.top.comet.client.component;

import com.ekupeng.top.comet.client.Message;

/**
 * @Description: 消息解析器
 * @ClassName: MessageParser
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-6-2 下午5:35:51
 * @version V1.0
 */
public interface MessageParser {

	/**
	 * 将原始Json数据解析成消息
	 * 
	 * @param rawJson
	 * @return
	 */
	public Message parse(String rawJson);

}
