package com.pinyougou.manager.controller;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {
	@Reference
	private GoodsService goodsService;
	@Autowired
	private Destination queueSolrDestination;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Destination queueSolrDeleteDestination;//用户在索引库中删除记录
	@Autowired
	private Destination topicPageDestination;
	@Autowired 
	private Destination topicPageDeleteDestination;
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbGoods goods){
		try {
			
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {

			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(final Long [] ids){
		try {
			goodsService.delete(ids);
//			itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
			
			jmsTemplate.send(queueSolrDeleteDestination,new MessageCreator() {
				
				@Override
				public Message createMessage(Session session) throws JMSException {
					// TODO Auto-generated method stub
					return session.createObjectMessage(ids);
				}
			});
			
			jmsTemplate.send(topicPageDeleteDestination,new MessageCreator() {
				
				@Override
				public Message createMessage(Session session) throws JMSException {
					// TODO Auto-generated method stub
					return session.createObjectMessage(ids);
				}
			});
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}
//	@Reference(timeout = 10000)
//	private ItemSearchService  itemSearchService;
	
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long [] ids,String status) {
		try {
			goodsService.updateStatus(ids, status);
			if ("1".equals(status)) {
				//导入索引库
				List<TbItem> list = goodsService.findItemListByGoodsIdandStatus(ids, status);
				System.out.println(list.size()+"================================================");
//				itemSearchService.importList(list);
				if (list.size()>0) {
					final String jsonString = JSON.toJSONString(list);
					jmsTemplate.send(queueSolrDestination,new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							// TODO Auto-generated method stub 
							return session.createTextMessage(jsonString);
						}
					});
				}
				
				//生成商品详情页
//				for(Long goodsId:ids){
//					itemPageService.genItemHtml(goodsId);
//				}
				for(final Long goodsId:ids){
					jmsTemplate.send(topicPageDestination,new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							// TODO Auto-generated method stub
							return session.createTextMessage(goodsId+"");
						}
					});
				}
			}
			
			return new Result(true,"审核成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(true,"审核失败");
		}
	}
	@RequestMapping("/updateIsMarketable")
	public Result updateIsMarketable(Long[] ids,String isMarketable) {
		try {
			goodsService.updateIsMarketable(ids, isMarketable);
			return new Result(true,"成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(true,"失败");
		}
	}
//	@Reference(timeout = 50000)
//	private ItemPageService itemPageService;
 
//	@RequestMapping("/genHtml")
//	public void genHtml(Long goodsId){
//		System.out.println(goodsId+"pinyougou-manager-web");
//		itemPageService.genItemHtml(goodsId);
//	}
}
