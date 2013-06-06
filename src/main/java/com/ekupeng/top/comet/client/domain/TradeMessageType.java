/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: TradeMessageType.java
 *
 */
package com.ekupeng.top.comet.client.domain;

/**
 * @Description: 交易消息类型(目前只列举了我们使用的几种类型，其余需要使用时请在注释处进行添加)
 * @ClassName: TradeMessageType
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-6-6 下午2:01:40
 * @version V1.0
 */
public enum TradeMessageType {
	/*
	 * 交易创建 TradeCreate
	 */
	TRADE_CREATE("TradeCreate", "交易创建"),
	/*
	 * 修改交易费用 TradeModifyFee
	 */
	/*
	 * 关闭或修改子订单 TradeCloseAndModifyDetailOrder
	 */
	/*
	 * 关闭交易 TradeClose
	 */
	TRADE_CLOSE("TradeClose", "关闭交易"),
	/*
	 * 买家付款 TradeBuyerPay
	 */
	TRADE_BUYER_PAY("TradeBuyerPay", "买家付款 "),
	/*
	 * 卖家发货 TradeSellerShip
	 */
	TRADE_SELLER_SHIP("TradeSellerShip", "卖家发货 "),
	/*
	 * 延长收货时间 TradeDelayConfirmPay
	 */
	/*
	 * 子订单退款成功 TradePartlyRefund
	 */
	/*
	 * 子订单打款成功 TradePartlyConfirmPay
	 */
	/*
	 * 交易成功 TradeSuccess
	 */
	TRADE_SUCCESS("TradeSuccess", "交易成功");
	/*
	 * 交易超时提醒 TradeTimeoutRemind
	 */
	/*
	 * 交易评价变更 TradeRated
	 */
	/*
	 * 交易备注修改 TradeMemoModified
	 */
	/*
	 * 修改交易收货地址 TradeLogisticsAddressChanged
	 */
	/*
	 * 修改订单信息（SKU等）TradeChanged
	 */
	/*
	 * 交易备注修改 TradeMemoModified
	 */
	/*
	 * 创建支付宝订单 TradeAlipayCreate
	 */
	private String type;
	private String description;

	TradeMessageType(String type, String description) {
		this.type = type;
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

}
