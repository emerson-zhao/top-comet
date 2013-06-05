/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: CometConfiguration.java
 *
 */
package com.ekupeng.top.comet.client.config;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description: 长连接相关参数
 * @ClassName: CometConfiguration
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-6-2 下午10:29:58
 * @version V1.0
 */
@Component("cometConfiguration")
public class CometConfiguration {

	/*
	 * 淘宝应用appkey
	 */
	@Value("${comet.top.appkey}")
	private String appkey;

	/*
	 * 淘宝应用secret
	 */
	@Value("${comet.top.appsecret}")
	private String secret;

	/*
	 * 本次长连接的用户id，默认线上该字段为空，若传入则表示为进行测试
	 */
	@Value("${comet.test.userid}")
	private long userid;

	/*
	 * 连接ID，若两次连接ID相同，后者会覆盖前者，将前一连接挤占，本实现类会根据时间戳自动生成
	 */
	private String id;

	/*
	 * 服务端连接url
	 */
	@Value("${comet.top.serverUrl}")
	private String serverUrl = "http://stream.api.taobao.com/stream";
	/*
	 * http连接超时时间，单位：毫秒
	 */
	@Value("${comet.http.connectionTimeout}")
	private int connectionTimeout = 5000;

	/*
	 * 由于服务端每60s向客户端发送一个心跳包，所以请求超时时间设为90s(即9000毫秒)，单位：毫秒
	 */
	@Value("${comet.http.soTimeout}")
	private int soTimeout = 6000 + 3000;

	public CometConfiguration() {
		super();
		this.id = new Date().toString();
	}

	public String getAppkey() {
		return appkey;
	}

	public String getSecret() {
		return secret;
	}

	public long getUserid() {
		return userid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public int getSoTimeout() {
		return soTimeout;
	}

}
