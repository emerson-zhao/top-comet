package com.ekupeng.top.comet.web.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ekupeng.top.comet.client.CometClientContainer;
import com.ekupeng.top.comet.client.component.CometStatusMonitor;
import com.ekupeng.top.comet.client.domain.ContainerConfiguration;

@Controller
public class HomeController {

	@Autowired
	private CometClientContainer cometClientContainer;
	@Autowired
	private ContainerConfiguration containerConfiguration;
	@Autowired
	private CometStatusMonitor cometStatusMonitor;

	@RequestMapping(value = "/")
	public ModelAndView monitor(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		request.setAttribute("appName", containerConfiguration.getAppName());
		request.setAttribute("appKey", containerConfiguration
				.getCometConfiguration().getAppkey());
		request.setAttribute("startUpTime",
				cometClientContainer.getStartUpTime());
		request.setAttribute("status", cometClientContainer
				.getCurrentCometStatus().toString());
		request.setAttribute("messageCount",
				String.valueOf(cometClientContainer.getMessageTotalCount()));
		request.setAttribute("messageCountFromLastStartUp", String
				.valueOf(cometClientContainer
						.getMessageTotalCountFromLastStartUp()));
		request.setAttribute("bizMessageCount",
				String.valueOf(cometClientContainer.getBizMessageTotalCount()));
		request.setAttribute("bizMessageCountFromLastStartUp", String
				.valueOf(cometClientContainer
						.getBizMessageTotalCountFromLastStartUp()));
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
		Date lastStartUpTime = cometClientContainer.getLastStartUpTime();
		Date lastHeartBeatTime = cometClientContainer.getLastHeartBeatTime();
		request.setAttribute("lastStartUpTime",
				lastStartUpTime == null ? "尚未连接" : sdf.format(lastStartUpTime));
		request.setAttribute(
				"lastHeartBeatTime",
				lastHeartBeatTime == null ? "尚未连接" : sdf
						.format(lastHeartBeatTime));
		StringBuffer changeLogs = new StringBuffer();
		SortedMap<Date, String> logs = cometStatusMonitor.getChangeLogs();
		for (Date d : logs.keySet()) {
			changeLogs.append(sdf.format(d) + " : " + logs.get(d) + "<br>");
		}
		request.setAttribute("logs", changeLogs.toString());
		return new ModelAndView("monitor");
	}

	public void setCometClientContainer(
			CometClientContainer cometClientContainer) {
		this.cometClientContainer = cometClientContainer;
	}

	public void setContainerConfiguration(
			ContainerConfiguration containerConfiguration) {
		this.containerConfiguration = containerConfiguration;
	}

	public void setCometStatusMonitor(CometStatusMonitor cometStatusMonitor) {
		this.cometStatusMonitor = cometStatusMonitor;
	}

}
