package com.pinyougou.page.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.page.service.ItemPageService;


@Component
public class PageListener implements MessageListener {
	@Autowired
	private ItemPageService itemPageService;

	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		TextMessage textMessage=(TextMessage)message;
		String text;
		try {
			System.out.println("PageListener接收到消息");
			text = textMessage.getText();
			boolean b = itemPageService.genItemHtml(Long.parseLong(text));//long基本数据类型
			System.out.println("网页生成结果:"+b);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
