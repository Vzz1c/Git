package com.itmeima.demo;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

public class ItemSearchListener  implements MessageListener {
	@Autowired
	private ItemSearchService itemSearchService;
	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		System.out.println("监听接收到消息");
		
		try {
			TextMessage textMessage=(TextMessage)message;
			String text = textMessage.getText();
			List<TbItem> list = JSON.parseArray(text,TbItem.class);
			itemSearchService.importList(list);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
