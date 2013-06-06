<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>TopComet长连接监控页面</title>
</head>
<body>
	<h1>当前长连接基本信息如下</h1>
	<p>appName：${appName}</p>
	<p>appkey：${appKey}</p>
	<p>容器启动时间：${startUpTime}</p>
	<p>当前长连接状态：${status}</p>
	<p>容器成功启动后的总消息数：${messageCount}</p>
	<p>最近一次连接成功后收到的总消息数：${messageCountFromLastStartUp}</p>
	<p>容器成功启动后的总业务消息数：${bizMessageCount}</p>
	<p>最近一次连接成功后收到的总业务消息数：${bizMessageCountFromLastStartUp}</p>
	<p>最近一次连接成功时间：${lastStartUpTime}</p>
	<p>最近一次心跳时间：${lastHeartBeatTime}</p>
	<p>
		服务器自启动后长连接容器的最近20次状态变更日志：<br>${logs}
	</p>
</body>
</html>
