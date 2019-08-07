package com.pinyougou.solrUtil;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;
@Component
public class SolrUtil {
	@Autowired
	private TbItemMapper tbItemMapper;
	@Autowired
	private SolrTemplate solrTemplate;
	public void improtItemData() {
		TbItemExample example = new TbItemExample();
		Criteria createCriteria = example.createCriteria();
		createCriteria.andStatusEqualTo("1");
		List<TbItem> list = tbItemMapper.selectByExample(example);
		for (TbItem tbItem : list) {
			tbItem.setSpecMap(JSON.parseObject(tbItem.getSpec(),Map.class));
		}
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
	
	}
	public static void main(String[] args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
		SolrUtil solrUtil = (SolrUtil)applicationContext.getBean("solrUtil");
		solrUtil.improtItemData();
	}
}
