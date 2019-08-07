package com.pinyougou.pojogroup;

import java.io.Serializable;
import java.util.List;

import com.pinyougou.pojo.TbOrderItem;

public class Cart implements Serializable {
	private String sellerId;
	private String sellerName;
	private List<TbOrderItem> orderItemList;
	public Cart(String sellerId, String sellerName, List<TbOrderItem> orderItemList) {
		super();
		this.sellerId = sellerId;
		this.sellerName = sellerName;
		this.orderItemList = orderItemList;
	}
	public Cart() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getSellerId() {
		return sellerId;
	}
	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}
	public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	public List<TbOrderItem> getOrderItemList() {
		return orderItemList;
	}
	public void setOrderItemList(List<TbOrderItem> orderItemList) {
		this.orderItemList = orderItemList;
	}
	
	
}
