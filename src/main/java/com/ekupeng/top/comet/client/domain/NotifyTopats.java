/**
 * 
 */
package com.ekupeng.top.comet.client.domain;

import java.util.Date;

import com.taobao.api.TaobaoObject;
import com.taobao.api.internal.mapping.ApiField;

/**
 * @description top任务通知消息域模型
 * @author Emerson
 * 
 */
public class NotifyTopats extends TaobaoObject {
	private static final long serialVersionUID = 3463592343434865354L;

	/**
	 * 任务主题
	 */
	@ApiField("topic")
	private String topic;

	/**
	 * 消息状态
	 */
	@ApiField("status")
	private String status;

	/**
	 * 任务id
	 */
	@ApiField("task_id")
	private Long taskId;

	/**
	 * 任务状态
	 */
	@ApiField("task_status")
	private String taskStatus;

	/**
	 * 时间戳（格式：yyyy-MM-dd HH:mm:ss）
	 */
	@ApiField("timestamp")
	private Date timestamp;

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
