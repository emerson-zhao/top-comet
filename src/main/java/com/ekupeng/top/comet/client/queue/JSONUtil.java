/**
 *  JSONUtil.java
 *
 *  Copyright ekupeng,Inc. 2012-2013   
 */
package com.ekupeng.top.comet.client.queue;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.ekupeng.top.comet.client.domain.BizMessageType;
import com.ekupeng.top.comet.client.domain.NotifyTopats;
import com.taobao.api.domain.NotifyItem;
import com.taobao.api.domain.NotifyRefund;
import com.taobao.api.domain.NotifyTrade;

/**
 * @Description: JSON与淘宝数据的转化工具
 * @ClassName: JSONUtil
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-6-6 下午1:39:47
 * @version V1.0
 */
public class JSONUtil {

	/**
	 * json转交易
	 * 
	 * @param jsonString
	 * @return
	 */
	public static NotifyTrade getNotifyTrade(String jsonString) {
		jsonString = formTransition(jsonString);
		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(jsonString);
		jsonObject = jsonObject.getJSONObject("notifyTrade");
		Object pojo = JSONObject.toBean(jsonObject, NotifyTrade.class);
		return (NotifyTrade) pojo;
	}

	/**
	 * json转商品
	 * 
	 * @param jsonString
	 * @return
	 */
	public static NotifyItem getNotifyItem(String jsonString) {
		jsonString = formTransition(jsonString);
		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(jsonString);
		jsonObject = jsonObject.getJSONObject("notifyItem");
		Object pojo = JSONObject.toBean(jsonObject, NotifyItem.class);
		return (NotifyItem) pojo;
	}

	/**
	 * json转退款
	 * 
	 * @param jsonString
	 * @return
	 */
	public static NotifyRefund getNotifyRefund(String jsonString) {
		jsonString = formTransition(jsonString);
		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(jsonString);
		jsonObject = jsonObject.getJSONObject("notifyRefund");
		Object pojo = JSONObject.toBean(jsonObject, NotifyRefund.class);
		return (NotifyRefund) pojo;
	}

	/**
	 * json转任务
	 * 
	 * @param jsonString
	 * @return
	 */
	public static NotifyTopats getNotifyTopats(String jsonString) {
		jsonString = formTransition(jsonString);
		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(jsonString);
		jsonObject = jsonObject.getJSONObject("NotifyTopats");
		Object pojo = JSONObject.toBean(jsonObject, NotifyTopats.class);
		return (NotifyTopats) pojo;
	}

	/**
	 * 得到通知类型
	 * 
	 * @param jsonString
	 * @return
	 */
	public static BizMessageType getBizMessageType(String jsonString) {
		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(jsonString);
		Object jo = (Object) jsonObject.get("notify_trade");
		if (jo != null) {
			return BizMessageType.TRADE_MESSAGE;
		}
		jo = (Object) jsonObject.get("notify_item");
		if (jo != null) {
			return BizMessageType.ITEM_MESSAGE;
		}
		jo = (Object) jsonObject.get("notify_refund");
		if (jo != null) {
			return BizMessageType.REFUND_MESSAGE;
		}
		jo = (Object) jsonObject.get("notify_topats");//
		if (jo != null) {
			return BizMessageType.TASK_MESSAGE;
		}
		return BizMessageType.OTHER_MESSAGE;
	}

	/**
	 * 格式转换
	 * 
	 * @param jsonString
	 * @return
	 */
	public static String formTransition(String jsonString) {
		if (!jsonString.startsWith("{")) {
			jsonString = "{" + jsonString + "}";
		}

		int index = -1;
		while ((index = jsonString.indexOf("_")) >= 0) {
			int ind1 = jsonString.indexOf(":", index);
			if (ind1 == -1) {
				jsonString = jsonString.replaceFirst("_", "*");
			} else {
				int ind2 = jsonString.indexOf(",", index);
				if (ind2 == -1) {
					ind2 = jsonString.indexOf("}", index);
				}
				if (ind1 < ind2) {
					jsonString = jsonString.replaceFirst("_", "");
					jsonString = jsonString.substring(0, index)
							+ (jsonString.charAt(index) + "").toUpperCase()
							+ jsonString.substring(index + 1,
									jsonString.length());
				} else {
					jsonString = jsonString.replaceFirst("_", "*");
				}
			}
		}
		jsonString = jsonString.replace("*", "_");
		return jsonString;
	}
}
