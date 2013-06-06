/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: TopCometWebStartListener.java
 *
 */
package com.ekupeng.top.comet.web.listener;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.ekupeng.top.comet.client.CometClientContainer;
import com.ekupeng.top.comet.client.queue.QueueBindingUtil;

/**
 * @Description: 实现ServletContextListener，在上下文初始化时启动Top的Comet长连接
 * @ClassName: TopCometWebStartListener
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-5-27 下午1:36:37
 * @version V1.0
 */
public class TopCometWebStartListener implements ServletContextListener {

	final private static Logger logger = LoggerFactory
			.getLogger(TopCometWebStartListener.class);

	/*
	 * Override
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(sce.getServletContext());

		// 初始化消息队列及其路由绑定关系
		QueueBindingUtil queueBindingUtil = (QueueBindingUtil) ctx
				.getBean("queueBindingUtil");
		try {
			queueBindingUtil.declareQueueAndBind();
		} catch (IOException e) {
			logger.error("初始化消息队列及其路由绑定关系失败", e);
			throw new RuntimeException("初始化消息队列及其路由绑定关系失败", e);
		}

		// 启动长连接客户端容器
		CometClientContainer taobaoCometService = (CometClientContainer) ctx
				.getBean("cometClientContainer");
		try {
			taobaoCometService.start();
		} catch (IOException e) {
			logger.error("消息通知长连接客户端容器启动失败", e);
		}

	}

	/*
	 * Override
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(sce.getServletContext());
		CometClientContainer taobaoCometService = (CometClientContainer) ctx
				.getBean("cometClientContainer");
		try {
			taobaoCometService.stop();
		} catch (IOException e) {
			logger.error("消息通知长连接客户端容器关闭失败", e);
		}

	}

}
