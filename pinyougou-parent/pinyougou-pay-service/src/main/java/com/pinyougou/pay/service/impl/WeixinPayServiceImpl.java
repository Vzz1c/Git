package com.pinyougou.pay.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;

import util.HttpClient;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {
	@Value("${appid}")
	private String appid;
	@Value("${partner}")
	private String partner;
	@Value("${partnerkey}")
	private String partnerkey;

	@Override
	public Map createNative(String out_trade_no, String total_fee) {
		// TODO Auto-generated method stub
		Map<String,String> paramMap = new HashMap<String, String>();
		paramMap.put("appid",appid);
		paramMap.put("mch_id",partner);
		paramMap.put("nonce_str",WXPayUtil.generateNonceStr());
		paramMap.put("body","pinyougou");
		paramMap.put("out_trade_no",out_trade_no);
		paramMap.put("total_fee",total_fee);
		paramMap.put("spbill_create_ip","127.0.0.1");
		paramMap.put("notify_url","http://www.baidu.com");
		paramMap.put("trade_type","NATIVE");
		try {
			String xmlParam= WXPayUtil.generateSignedXml(paramMap, partnerkey);
			System.out.println("请求的参数:"+xmlParam);
			HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
			httpClient.setHttps(true);
			httpClient.setXmlParam(xmlParam);
			httpClient.post();
			//获取结果
			String xmlResult = httpClient.getContent();
			
			Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlResult);
			System.out.println("请求后微信返回的结果"+resultMap);
			Map<String, String> map = new HashMap<String, String>();
			map.put("code_url", resultMap.get("code_url"));
			map.put("total_fee", total_fee);
			map.put("out_trade_no",out_trade_no);
			return map;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new HashMap<>();
		}
	}

	@Override
	public Map queryPayStatus(String out_trade_no) {
		// TODO Auto-generated method stub
		Map<String,String> paramMap = new HashMap<String, String>();
		paramMap.put("appid", appid);
		paramMap.put("mch_id", partner);
		paramMap.put("out_trade_no", out_trade_no);
		paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
		try {
			String xmlParam = WXPayUtil.generateSignedXml(paramMap, partnerkey);
			HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
			httpClient.setHttps(true);
			httpClient.setXmlParam(xmlParam);
			httpClient.post();
			String xmlResult = httpClient.getContent();
			Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlResult);
			System.out.println("微信查询返回结果"+resultMap);
			return resultMap;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("经过了null");
		return null;
	}

	@Override
	public Map closePay(String out_trade_no) {
		Map<String,String> paramMap = new HashMap<String, String>();
		paramMap.put("appid", appid);
		paramMap.put("mch_id", partner);
		paramMap.put("out_trade_no", out_trade_no);
		paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
		try {
			String xmlParam = WXPayUtil.generateSignedXml(paramMap, partnerkey);
			HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
			httpClient.setHttps(true);
			httpClient.setXmlParam(xmlParam);
			httpClient.post();
			String xmlResult = httpClient.getContent();
			Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlResult);
			System.out.println("微信查询返回结果"+resultMap);
			return resultMap;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("经过了null");
		return null;
	}
		
}
