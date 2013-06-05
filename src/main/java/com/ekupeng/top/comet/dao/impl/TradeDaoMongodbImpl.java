/**
 * Copyright ekupeng,Inc. 2012-2013
 * @Title: TradeDaoMongodbImpl.java
 *
 */
package com.ekupeng.top.comet.dao.impl;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.ekupeng.top.comet.dao.TradeDao;
import com.taobao.api.domain.Trade;

/**
 * @Description: 淘宝交易数据接口Mongodb实现
 * @ClassName: TradeDaoMongodbImpl
 * @author emerson <emsn1026@gmail.com>
 * @date 2013-5-27 下午6:00:22
 * @version V1.0
 */
public class TradeDaoMongodbImpl implements TradeDao {

	private MongoTemplate mongoTemplate;

	/*
	 * Override
	 */
	@Override
	public void insert(Trade trade) {
		mongoTemplate.insert(trade);
	}

	/*
	 * Override
	 */
	@Override
	public Trade findOne(String id) {
		return mongoTemplate.findOne(new Query(Criteria.where("id").is(id)),
				Trade.class);
	}

	/*
	 * Override
	 */
	@Override
	public List<Trade> findAll() {
		return mongoTemplate.find(new Query(), Trade.class);
	}

	/*
	 * Override
	 */
	@Override
	public List<Trade> findByRegex(String regex) {
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Criteria criteria = new Criteria("name").regex(pattern.toString());
		return mongoTemplate.find(new Query(criteria), Trade.class);
	}

	/*
	 * Override
	 */
	@Override
	public void removeOne(String id) {
		Criteria criteria = Criteria.where("id").in(id);
		if (criteria == null) {
			Query query = new Query(criteria);
			if (query != null
					&& mongoTemplate.findOne(query, Trade.class) != null)
				mongoTemplate.remove(mongoTemplate.findOne(query, Trade.class));
		}
	}

	/*
	 * Override
	 */
	@Override
	public void removeAll() {
		List<Trade> list = this.findAll();
		if (list != null) {
			for (Trade trade : list) {
				mongoTemplate.remove(trade);
			}
		}
	}

	/*
	 * Override
	 */
	@Override
	public void findAndModify(String id) {
		// mongoTemplate.updateFirst(new Query(Criteria.where("id").is(id)), new
		// Update().inc("age", 3));

	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

}
