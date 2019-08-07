package com.pinyougou.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import com.pinyougou.pojo.TbSeckillGoodsExample.Criteria;

@Component
public class SeckillTask {
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	@Scheduled(cron = "* * * * * ?")
	public void refreshSeckillGoods() {
		System.out.println("执行了任务调度" + new Date());
		ArrayList goodsIds = new ArrayList(redisTemplate.boundHashOps("seckillGoods").keys());
		System.out.println("循环开始");
		if (goodsIds != null || goodsIds.size() > 0) {
			for (int i = 0; i < goodsIds.size(); i++) {
				Object object = goodsIds.get(i);
				String idString=object+"";
				TbSeckillGoods tbSeckillGoods = seckillGoodsMapper.selectByPrimaryKey(Long.valueOf(idString));
				if ("0".equals(tbSeckillGoods.getStatus())) {
					System.out.println("进入到删除");
					redisTemplate.boundHashOps("seckillGoods").delete(Long.valueOf(idString));
				}
			}
		}
		if (goodsIds.size() == 0) {
			goodsIds.add("");
		}
		System.out.println("循环结束");
		System.out.println(goodsIds);

		TbSeckillGoodsExample example = new TbSeckillGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");
		criteria.andStartTimeLessThanOrEqualTo(new Date());
		criteria.andEndTimeGreaterThan(new Date());
		criteria.andNumGreaterThan(0);
		criteria.andIdNotIn(new ArrayList(goodsIds));

		List<TbSeckillGoods> seckillGoodsList = seckillGoodsList = seckillGoodsMapper.selectByExample(example);
		for (TbSeckillGoods tbSeckillGoods : seckillGoodsList) {
			redisTemplate.boundHashOps("seckillGoods").put(tbSeckillGoods.getId(), tbSeckillGoods);
		}
	}
	@Scheduled(cron = "* * * * * ?")
	public void removeSeckillGoods() {
		List<TbSeckillGoods> seckillGoodslist=redisTemplate.boundHashOps("seckillGoods").values();
		for (TbSeckillGoods tbSeckillGoods : seckillGoodslist) {
			long time = tbSeckillGoods.getEndTime().getTime();
			long time2 = new Date().getTime();
			if (time < time2) {
				seckillGoodsMapper.updateByPrimaryKey(tbSeckillGoods);
				redisTemplate.boundHashOps("seckillGoods").delete(tbSeckillGoods.getId());
			}
		}
		System.out.println("执行清除过期商品==========");
	}
}
