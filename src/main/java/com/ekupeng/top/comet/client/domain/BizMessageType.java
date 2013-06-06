/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: BizMessageType.java
 *
 */
package com.ekupeng.top.comet.client.domain;

/**
 * @Description: 消息类型
 * @ClassName: BizMessageType
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-6-6 下午1:42:13
 * @version V1.0
 */
public enum BizMessageType {

	/**
	 * 交易
	 */
	TRADE_MESSAGE,

	/**
	 * 商品
	 */
	ITEM_MESSAGE,

	/**
	 * 退款
	 */
	REFUND_MESSAGE,

	/**
	 * 任务
	 */
	TASK_MESSAGE,

	/**
	 * 其他
	 */
	OTHER_MESSAGE;
}
