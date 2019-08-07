package com.pinyougou.cart.service.impl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
@Service
public class CartServiceImpl implements CartService {
	@Autowired
	private TbItemMapper itemMapper;
	@Override
	public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
		// TODO Auto-generated method stub
		//1.根据商品 SKU ID 查询 SKU 商品信息
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		if (item==null) {
			throw new RuntimeException("商品不存在");
		}
		if (!"1".equals(item.getStatus())) {
			throw new RuntimeException("商品状态不合法");
		}
		
		//2.获取商家 ID
		String sellerId = item.getSellerId();
		
		//3.根据商家 ID 判断购物车列表中是否存在该商家的购物车
		Cart cart = searchBySellerId(cartList,sellerId);
		//4.如果购物车列表中不存在该商家的购物车
		if (cart==null) {
			//4.1 新建购物车对象 设置商家id和商家名称
			cart=new Cart();
			cart.setSellerId(sellerId);
			cart.setSellerName(item.getSeller());
			//再创建购物车明细列表
			List<TbOrderItem> orderItemList = new ArrayList<TbOrderItem>();
			//从item添加信息到订单
			TbOrderItem orderItem = createOrderItem(item,num);
			orderItemList.add(orderItem);
			cart.setOrderItemList(orderItemList);
			cartList.add(cart);
		}else {//5.如果购物车列表中存在该商家的购物车
			// 查询购物车明细列表中是否存在该商品
			TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(),itemId);
			//5.1. 如果没有，新增购物车明细
			if (orderItem==null) {
				orderItem=	createOrderItem(item,num);
				cart.getOrderItemList().add(orderItem);
			}else {
				//5.2. 如果有，在原购物车明细上添加数量，更改金额
				orderItem.setNum(orderItem.getNum()+num);
				orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum().doubleValue()));
				if (orderItem.getNum()<=0) {
					cart.getOrderItemList().remove(orderItem);
				}
				if (cart.getOrderItemList().size()<=0) {
					cartList.remove(cart);
				}
			}
			
		}
		
		
		return cartList;
	}
	/**
	 * 根据商家id查询购物车中是否存在此商家 如存在则返回对应商家的购物车列表 如不存在则返回Null;
	 * @param cartList
	 * @param sellerId
	 * @return
	 */
	private Cart searchBySellerId(List<Cart> cartList,String sellerId ) {
		
		for(Cart cart:cartList) {
			if (sellerId.equals(cart.getSellerId())) {
				return cart;
			}
		}
		return null;
	}
	private TbOrderItem createOrderItem(TbItem item,Integer num) {
		TbOrderItem tbOrderItem = new TbOrderItem();
		tbOrderItem.setGoodsId(item.getGoodsId()); //设置spu的id
		tbOrderItem.setItemId(item.getId());		//设置sku的id;	
		tbOrderItem.setNum(num);					//设置sku数量 
		tbOrderItem.setPicPath(item.getImage());	//设置此sku的图片
		tbOrderItem.setPrice(item.getPrice());		//设置此sku的价格
		tbOrderItem.setSellerId(item.getSellerId());//设置此sku的的商家id
		tbOrderItem.setTitle(item.getTitle());		//设置此sku的标题
		tbOrderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
		return tbOrderItem;
	}
	/**
	 * 根据itemID查询商家的明细列表
	 * @param orderItemList
	 * @param itemId
	 * @return
	 */
	private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList,Long itemId) {
		for (TbOrderItem tbOrderItem : orderItemList) {
			if (tbOrderItem.getItemId().longValue()==itemId.longValue()) {
				return tbOrderItem;
			}
		}
		return null;
	}
	@Autowired
	private RedisTemplate redisTemplate;
	@Override
	public List<Cart> findCartListFromRedis(String username) {
		// TODO Auto-generated method stub
		
		List<Cart> cartList = (List<Cart>)redisTemplate.boundHashOps("cartList").get(username);
		System.out.println("从 redis 中提取购物车数据.....");
		if(cartList==null){
			cartList=new ArrayList();
		}
		return cartList;
	}
	@Override
	public void saveCartListToRedis(String username, List<Cart> cartList) {
		// TODO Auto-generated method stub
		System.out.println("saveCartListToRedis"+cartList.size());
		redisTemplate.boundHashOps("cartList").put(username, cartList);
	}
	@Override
	public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
		// TODO Auto-generated method stub
		for(Cart cart:cartList2) {
			for(TbOrderItem orderItem:cart.getOrderItemList()) {
				addGoodsToCartList(cartList1,orderItem.getItemId(),orderItem.getNum());
			}
		}
		return cartList1;
	}
	
}
