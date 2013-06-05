/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: TradeDao.java
 *
 */
package com.ekupeng.top.comet.dao;

import java.util.List;

import com.taobao.api.domain.Trade;

/**
 * @Description: 淘宝交易数据访问接口
 * @ClassName: TradeDao
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-5-27 下午5:53:29
 * @version V1.0
 */
public interface TradeDao {

	/**
	 * 插入交易
	 * 
	 */
	public void insert(Trade trade);

	/**
	 * 
	 * 根据交易号得到交易
	 * 
	 */
	public Trade findOne(String id);

	/**
	 * 
	 * 查询所有交易
	 * 
	 */
	public List<Trade> findAll();

	/**
	 * 
	 * @param regex
	 * @return
	 */
	public List<Trade> findByRegex(String regex);

	/**
	 * 
	 * 根据交易号删除指定的交易
	 */
	public void removeOne(String id);

	/**
	 * 
	 * 删除所有交易
	 */
	public void removeAll();

	/**
	 * 通过ID找到并修改 <b>function:</b>
	 * 
	 */
	public void findAndModify(String id);

}
