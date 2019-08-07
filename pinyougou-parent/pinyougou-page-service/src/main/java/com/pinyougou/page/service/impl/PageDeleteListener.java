package com.pinyougou.page.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.page.service.ItemPageService;

@Component
public class PageDeleteListener implements MessageListener{
	@Autowired
	private ItemPageService itemPageService;

	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		System.out.println("PageDeleteListener接收到消息");
		ObjectMessage objectMessage=(ObjectMessage)message;

		try {
			
		Long [] goodsIds=(Long [])objectMessage.getObject();
			boolean b = itemPageService.deleteItemHtml(goodsIds);//long基本数据类型
			System.out.println("网页删除:"+b);
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
