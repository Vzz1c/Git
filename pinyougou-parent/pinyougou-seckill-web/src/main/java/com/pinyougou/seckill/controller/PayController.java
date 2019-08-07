package com.pinyougou.seckill.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.common.util.Hash;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;

import entity.Result;

@RestController
@RequestMapping("/pay")
public class PayController {
	@Reference
	private WeixinPayService weixinPayService;
	@Reference
	private SeckillOrderService seckillOrderService;

	@RequestMapping("/createNative")
	public Map createNative() {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println(userId+"userId");
		TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(userId);
		if (seckillOrder != null) {
			long fen = (long) (seckillOrder.getMoney().doubleValue() * 100);// 金额（分）
			System.out.println(seckillOrder.getId()+"seckillOrder.getId()");
			System.out.println(fen+"fenfenfenfenfenfenfen");
			Map createNative = weixinPayService.createNative(seckillOrder.getId() + "", +fen + "");
			System.out.println(createNative+"payController结果");
			return createNative;
		} else {
			return new HashMap();
		}
	}
	
	@RequestMapping("/queryPayStatus")
	public Result queryPayStatus(String out_trade_no) {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		Result result = null;
		int x = 0;
		while (true) {
			Map<String, String> map = weixinPayService.queryPayStatus(out_trade_no);
			if (map == null) {
				result = new Result(false, "支付出错");
				break;
			}
			if (map.get("trade_state").equals("SUCCESS")) {
				result = new Result(true, "支付成功");
				seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no),
						map.get("transaction_id"));
				break;
			}
			x++;
			;
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (x > 5) {
				result = new Result(false, "二维码超时");
				Map<String,String> closePayResultMap = weixinPayService.closePay(out_trade_no);
				if (closePayResultMap!=null&&"FAll".equals(closePayResultMap.get("return_code"))) {
					if ("ORDERPAID".equals(closePayResultMap.get("err_code"))) {
						result = new Result(true, "支付成功");
						seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no),map.get("transaction_id"));
					}
				}
				
				
				if (!result.isSuccess()) {
					seckillOrderService.deleteOrderFromRedis(userId, Long.valueOf(out_trade_no));
				}
				break;
			}
		}
		return result;
	}

}
