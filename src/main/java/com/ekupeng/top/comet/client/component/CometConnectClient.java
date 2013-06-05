/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: CometConnectClient.java
 *
 */
package com.ekupeng.top.comet.client.component;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: 长连接客户端
 * @ClassName: CometConnectClient
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-6-2 下午6:29:45
 * @version V1.0
 */
public interface CometConnectClient {

	/**
	 * 打开长连接(若发现已有连接存在，则直接返回当前连接)
	 * 
	 * @throws IOException
	 */
	public InputStream openConnection() throws IOException;

	/**
	 * 打开新连接（若存在旧的连接，则将旧连接关闭）
	 * 
	 * @throws IOException
	 */
	public InputStream openNewConnection() throws IOException;

	/**
	 * 关闭长连接
	 * 
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public void closeConnection() throws IllegalStateException, IOException;

}
