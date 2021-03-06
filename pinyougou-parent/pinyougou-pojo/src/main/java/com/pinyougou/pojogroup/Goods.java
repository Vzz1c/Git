package com.pinyougou.pojogroup;

import java.io.Serializable;
import java.util.List;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
/**
 * 商品组合类
 * @author Vzz1c
 *
 */
public class Goods implements Serializable {
	
	private TbGoods goods;
	private TbGoodsDesc goodsDesc;
	private List<TbItem> itemList;
	public TbGoods getGoods() {
		return goods;
	}
	public void setGoods(TbGoods goods) {
		this.goods = goods;
	}
	public TbGoodsDesc getGoodsDesc() {
		return goodsDesc;
	}
	public void setGoodsDesc(TbGoodsDesc goodsDesc) {
		this.goodsDesc = goodsDesc;
	}
	public List<TbItem> getItemList() {
		return itemList;
	}
	public void setItemList(List<TbItem> itemList) {
		this.itemList = itemList;
	}
	public Goods(TbGoods goods, TbGoodsDesc goodsDesc, List<TbItem> itemList) {
		super();
		this.goods = goods;
		this.goodsDesc = goodsDesc;
		this.itemList = itemList;
	}
	public Goods() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "Goods [goods=" + goods + ", goodsDesc=" + goodsDesc + ", itemList=" + itemList + "]";
	}
	
}
