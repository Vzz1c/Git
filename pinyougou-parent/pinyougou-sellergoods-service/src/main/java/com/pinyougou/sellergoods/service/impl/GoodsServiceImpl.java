package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.com.caucho.hessian.io.MapSerializer;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsDescService;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	/** 
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbGoods goods) {
		goodsMapper.insert(goods);		
	}   

	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria createCriteria = example.createCriteria();
		createCriteria.andGoodsIdEqualTo(goods.getGoods().getId()); 
		itemMapper.deleteByExample(example);
		insertItem(goods);
	
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods = new Goods();
		TbGoods tbgoods= goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbgoods);
		TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		goods.setGoodsDesc(goodsDesc);
		TbItemExample example=new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria createCriteria = example.createCriteria();
		createCriteria.andGoodsIdEqualTo(goods.getGoodsDesc().getGoodsId());
		List<TbItem> items = itemMapper.selectByExample(example); 
		goods.setItemList(items);
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(goods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				//criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
							criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);
		List<TbGoods> result = page.getResult();
		return new PageResult(page.getTotal(), page.getResult());
	}

		@Override
		public void add(Goods goods) {
			// TODO Auto-generated method stub
			System.out.println("经过了public void add方法");
			goods.getGoods().setAuditStatus("0");
			TbGoods tbgoods = goods.getGoods();
			goodsMapper.insert(tbgoods);
			TbGoodsDesc tbgoodsDesc = goods.getGoodsDesc();
			Long id = tbgoods.getId();
			tbgoodsDesc.setGoodsId(tbgoods.getId());
			goodsDescMapper.insert(tbgoodsDesc);
//			goods.getGoods().setAuditStatus("0");
//			goodsMapper.insert(goods.getGoods());
//			goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
//			goodsDescMapper.insert(goods.getGoodsDesc());
			insertItem(goods);

		}
		private void setItemValue(TbItem tbItem,Goods goods) {
			tbItem.setPrice(goods.getGoods().getPrice());
			tbItem.setUpdateTime(new Date());
			tbItem.setCategoryid(goods.getGoods().getCategory3Id());
			tbItem.setCreateTime(new Date());
			tbItem.setGoodsId(goods.getGoods().getId());
			tbItem.setSellerId(goods.getGoods().getSellerId());
			
			TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
			tbItem.setCategory(itemCat.getName());
			
			TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
			tbItem.setBrand(brand.getName());
			TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
			tbItem.setSeller(seller.getName());
			List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(),Map.class);
			System.out.println(imageList);
			if(imageList.size()>0) {
				tbItem.setImage((String)imageList.get(0).get("url"));
			}
		}
		private void insertItem(Goods goods) {
			if("1".equals(goods.getGoods().getIsEnableSpec())){
				List<TbItem> itemList = goods.getItemList();
				for (TbItem tbItem : itemList) {
					String title = goods.getGoods().getGoodsName();
					Map<String,Object> map =JSON.parseObject(tbItem.getSpec());
					Set<String> keySet = map.keySet();
					for(String key:keySet) {
						title+=" "+map.get(key);
					}
					tbItem.setTitle(title);
					
					setItemValue(tbItem,goods);
					
					itemMapper.insert(tbItem);
						
				}
			}else {
				TbItem tbItem = new  TbItem();
				tbItem.setTitle(goods.getGoods().getGoodsName());
				tbItem.setPrice(goods.getGoods().getPrice());
				tbItem.setNum(999999);
				tbItem.setStatus("1");
				tbItem.setIsDefault("1");
				tbItem.setSpec("{}");
				setItemValue(tbItem,goods);
				
				itemMapper.insert(tbItem);
				
			}

			
		}

		@Override
		public void updateStatus(Long[] ids, String status) {
			// TODO Auto-generated method s
			for(Long id:ids) {
				TbGoods goods = goodsMapper.selectByPrimaryKey(id);
				goods.setAuditStatus(status);
				goodsMapper.updateByPrimaryKey(goods);
			}
		}

		@Override
		public void updateIsMarketable(Long[] ids, String isMarketable) {
			try {
				// TODO Auto-generated method stub
				for(Long id:ids) {
					TbGoods goods = goodsMapper.selectByPrimaryKey(id);
					String auditStatus = goods.getAuditStatus();
					if("1".equals(auditStatus)) {
						goods.setIsMarketable(isMarketable);
					}
					goodsMapper.updateByPrimaryKey(goods);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}

		@Override
		public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status) {
			// TODO Auto-generated method stub
			TbItemExample example=new TbItemExample();
			com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
			criteria.andGoodsIdIn(Arrays.asList(goodsIds));
			criteria.andStatusEqualTo(status);
			List<TbItem> itemList = itemMapper.selectByExample(example);
			System.out.println(itemList+"itemListitemListitemList");
			return itemList;
		}

		
}
