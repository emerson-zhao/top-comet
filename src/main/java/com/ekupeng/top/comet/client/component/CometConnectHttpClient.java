/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: CometConnectHttpClient.java
 *
 */
package com.ekupeng.top.comet.client.component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ekupeng.top.comet.client.config.CometConfiguration;
import com.ekupeng.top.comet.client.config.TaobaoConstants;
import com.taobao.api.internal.util.RequestParametersHolder;
import com.taobao.api.internal.util.StringUtils;
import com.taobao.api.internal.util.TaobaoHashMap;
import com.taobao.api.internal.util.TaobaoUtils;

/**
 * @Description:长连接HTTP客户端实现:该客户端最多只维护一个连接
 * @ClassName: CometConnectHttpClient
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-6-2 下午8:18:58
 * @version V1.0
 */
@Component("cometConnectClient")
public class CometConnectHttpClient implements CometConnectClient {

	private static final Logger logger = LoggerFactory
			.getLogger(CometConnectHttpClient.class);

	private HttpClient httpClient;

	/*
	 * 长连接相关配置
	 */
	@Autowired
	private CometConfiguration cometConfiguration;

	private volatile HttpEntity httpEntity;

	public CometConnectHttpClient() {
		super();
	}

	/**
	 * 初始化httpClient
	 */
	private synchronized void initHttpClient() {
		if (httpClient != null)
			return;
		if (cometConfiguration != null) {
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params,
					cometConfiguration.getConnectionTimeout());
			HttpConnectionParams.setSoTimeout(params,
					cometConfiguration.getSoTimeout());
			httpClient = new DefaultHttpClient(params);
		} else {
			httpClient = new DefaultHttpClient();
		}
	}

	@Override
	public synchronized InputStream openConnection() throws IOException {
		if (this.httpEntity != null && this.httpEntity.getContent() != null)
			return this.httpEntity.getContent();
		return openNewConnection();
	}

	@SuppressWarnings("deprecation")
	@Override
	public synchronized InputStream openNewConnection() throws IOException {
		/**
		 * 保留原有的httpEntity
		 */
		HttpEntity oldHttpEntity = this.httpEntity;
		configurationValidate();
		initHttpClient();
		String timpStamp = String.valueOf(System.currentTimeMillis());
		String sign = generateSign(timpStamp, cometConfiguration);

		HttpPost post = new HttpPost(cometConfiguration.getServerUrl());
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TaobaoConstants.PARAM_APPKEY,
				cometConfiguration.getAppkey()));
		if (cometConfiguration.getUserid() != 0) {
			params.add(new BasicNameValuePair(TaobaoConstants.PARAM_USERID,
					String.valueOf(cometConfiguration.getUserid())));
		}
		params.add(new BasicNameValuePair(TaobaoConstants.PARAM_CONNECT_ID,
				cometConfiguration.getId()));
		params.add(new BasicNameValuePair(TaobaoConstants.PARAM_TIMESTAMP,
				timpStamp));
		params.add(new BasicNameValuePair(TaobaoConstants.PARAM_SIGN, sign));

		post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		HttpResponse httpResponse = null;

		httpResponse = httpClient.execute(post);

		if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity.isStreaming()) {
				this.httpEntity = httpResponse.getEntity();
				if (oldHttpEntity != null && oldHttpEntity.getContent() != null)
					oldHttpEntity.getContent().close();
				return httpEntity.getContent();
			} else {
				throw new RuntimeException("该服务器端不支持长连接！");
			}
		} else {
			throw new RuntimeException("长连接打开失败，返回信息如下："
					+ httpResponse.getStatusLine().toString());
		}
	}

	@Override
	public synchronized void closeConnection() throws IllegalStateException,
			IOException {
		if (httpEntity == null)
			return;
		InputStream inputStream = httpEntity.getContent();
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (Exception e) {
				//ignore
			}
		}
		httpEntity = null;
	}

	/**
	 * 配置校验
	 */
	private void configurationValidate() {
		if (cometConfiguration == null
				|| cometConfiguration.getAppkey() == null
				|| cometConfiguration.getSecret() == null
				|| cometConfiguration.getServerUrl() == null) {
			throw new RuntimeException("开启长连接前必须传入有效的cometConfiguration");
		}
	}

	/**
	 * 生成top请求的签名
	 * 
	 * @param timpStamp
	 * @param cometConfiguration
	 * @return
	 */
	private String generateSign(String timpStamp,
			CometConfiguration cometConfiguration) {
		configurationValidate();
		TaobaoHashMap param = new TaobaoHashMap();
		param.put(TaobaoConstants.PARAM_APPKEY, cometConfiguration.getAppkey());
		if (cometConfiguration.getUserid() != 0)
			param.put(TaobaoConstants.PARAM_USERID,
					cometConfiguration.getUserid());
		param.put(TaobaoConstants.PARAM_CONNECT_ID, cometConfiguration.getId());
		param.put(TaobaoConstants.PARAM_TIMESTAMP, timpStamp);
		String sign = null;
		RequestParametersHolder paramHolder = new RequestParametersHolder();
		paramHolder.setProtocalMustParams(param);
		try {
			sign = TaobaoUtils.signTopRequestNew(paramHolder,
					cometConfiguration.getSecret(), false);
			if (StringUtils.isEmpty(sign)) {
				throw new RuntimeException("签名生成失败~");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return sign;
	}

	protected void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	protected void setCometConfiguration(CometConfiguration cometConfiguration) {
		this.cometConfiguration = cometConfiguration;
	}

	public static void main(String[] args) throws IllegalStateException,
			IOException {

		TaobaoHashMap param = new TaobaoHashMap();
		param.put(TaobaoConstants.PARAM_APPKEY, "12302038");
		param.put(TaobaoConstants.PARAM_USERID, "729839245");
		String id = String.valueOf(System.currentTimeMillis());
		String timestamp = String.valueOf(System.currentTimeMillis());
		// param.put(TaobaoConstants.PARAM_CONNECT_ID, id);
		param.put(TaobaoConstants.PARAM_CONNECT_ID, "1234");
		param.put(TaobaoConstants.PARAM_TIMESTAMP, timestamp);

		String sign = null;
		RequestParametersHolder paramHolder = new RequestParametersHolder();
		paramHolder.setProtocalMustParams(param);
		try {
			sign = TaobaoUtils.signTopRequestNew(paramHolder,
					"c237345b3aa9387d06b41b817eafa1f7", false);
			if (StringUtils.isEmpty(sign)) {
				throw new RuntimeException("Get sign error");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://stream.api.taobao.com/stream");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TaobaoConstants.PARAM_APPKEY,
				"12302038"));
		params.add(new BasicNameValuePair(TaobaoConstants.PARAM_USERID,
				"729839245"));
		// params.add(new BasicNameValuePair(TaobaoConstants.PARAM_CONNECT_ID,
		// id));
		params.add(new BasicNameValuePair(TaobaoConstants.PARAM_CONNECT_ID,
				"1234"));
		params.add(new BasicNameValuePair(TaobaoConstants.PARAM_TIMESTAMP,
				timestamp));
		params.add(new BasicNameValuePair(TaobaoConstants.PARAM_SIGN, sign));
		post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(post);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			System.out.println(httpEntity.isStreaming());
			InputStream is = httpEntity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"));
			while (true) {
				String msg = reader.readLine();
				if (msg == null) {
					System.out.println("stop");
					break;
				}
				System.out.println(msg);
			}

		} else {
			System.out.println(httpResponse.getStatusLine().getStatusCode());
			System.out.println("fail");
		}
	}

}
