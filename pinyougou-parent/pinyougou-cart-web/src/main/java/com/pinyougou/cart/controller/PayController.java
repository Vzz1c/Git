package com.pinyougou.cart.controller;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;

import entity.Result;


@RestController
@RequestMapping("/pay")
public class PayController {
	@Reference(timeout = 5000)
	private WeixinPayService weixinPayService;
	@Reference
	private OrderService orderService;
	@RequestMapping("/createNative")
	public Map createNative() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		TbPayLog tbPayLog = orderService.searchPayLogFromRedis(name);
		if (tbPayLog!=null) {
			Map resultMap = weixinPayService.createNative(tbPayLog.getOutTradeNo(), "1");
			return resultMap;
		}else {
			return new HashMap();
		}
	}
	@RequestMapping("/queryPayStatus")
	public Result queryPayStatus(String out_trade_no) {
		
		Result result =null;
		int x=0;
		while(true){
			System.out.println("进入到死循环");
			Map resultMap = weixinPayService.queryPayStatus(out_trade_no);
			
			System.out.println(resultMap);
			
			if (resultMap==null) {
				System.out.println("经过了支付出错");
				result=new Result(false,"支付出错");
				
				break;
			}
			if (resultMap.get("trade_state").equals("SUCCESS")) {
				System.out.println("经过了支付成功");
				result=new Result(true,"支付成功");
				Object object = resultMap.get("transaction_id");
				orderService.updateOrderStatus(out_trade_no, (String)resultMap.get("transaction_id"));
				break;
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			x+=1;
			if (x>100) {
				System.out.println("经过了超时");
				result=new Result(false,"二维码超时");
				break;
			}
		}
		return result;
	}
}
