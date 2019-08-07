package com.pinyougou.page.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class ItemPageServiceImpl implements ItemPageService {
	
	@Autowired
	private FreeMarkerConfig freeMarkerConfig;
	@Autowired
	private TbGoodsMapper tbGoodsMapper;
	@Autowired
	private TbGoodsDescMapper tbGoodsDescMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbItemMapper itemMapper;

	@Value("${pagedir}")
	private String pagedir;
	@Override
	public boolean genItemHtml(Long goodsId) {
		// TODO Auto-generated method stub
		Configuration configuration = freeMarkerConfig.getConfiguration();
		System.out.println(goodsId+"com.pinyougou.page.service.impl;");
		try {
			Template template = configuration.getTemplate("item.ftl");
			Map dataModel = new HashMap();
			TbGoods goods = tbGoodsMapper.selectByPrimaryKey(goodsId);
			TbGoodsDesc goodsDesc = tbGoodsDescMapper.selectByPrimaryKey(goodsId);
			Long category1Id = goods.getCategory1Id();
			Long category2Id = goods.getCategory2Id();
			Long category3Id = goods.getCategory3Id();
			String category1Name = itemCatMapper.selectByPrimaryKey(category1Id).getName();
			String category2Name = itemCatMapper.selectByPrimaryKey(category2Id).getName();
			String category3Name = itemCatMapper.selectByPrimaryKey(category3Id).getName();
			Writer out = new FileWriter(pagedir+goodsId+".html");
			dataModel.put("goods", goods);
			dataModel.put("goodsDesc", goodsDesc);
			dataModel.put("category1Name", category1Name);
			dataModel.put("category2Name", category2Name);
			dataModel.put("category3Name", category3Name);
			
			//sku列表
			TbItemExample example=new TbItemExample();
			Criteria criteria = example.createCriteria();
			criteria.andStatusEqualTo("1");//状态为有效
			criteria.andGoodsIdEqualTo(goodsId);//指定 SPU ID
			example.setOrderByClause("is_default desc");//按照状态降序，保证第一个为默
			List<TbItem> itemList = itemMapper.selectByExample(example);
			dataModel.put("itemList", itemList);

			
			//****************************************************
			template.process(dataModel, out);
			out.close();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	@Override
	public boolean deleteItemHtml(Long [] goodsIds) {
		// TODO Auto-generated method stub
		try {
			for(Long goodsId:goodsIds) {
				new File(pagedir+goodsId+".html").delete();
			}
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	
}
