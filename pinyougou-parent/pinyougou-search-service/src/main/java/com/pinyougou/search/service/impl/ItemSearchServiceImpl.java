 package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightEntry.Highlight;
import org.springframework.data.solr.core.query.result.HighlightPage;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Service(timeout = 6000)
public class ItemSearchServiceImpl implements ItemSearchService {

	@Autowired
	private SolrTemplate solrTemplate;

	@Override
	public Map<String, Object> search(Map searchMap) {
		// TODO Auto-generated method stub
		// 空格处理
		String keywords = (String) searchMap.get("keywords");
		System.out.println(keywords);
		if (keywords==null||"".equals(keywords)) {
			return new HashMap<String, Object>();
		}
		
		Map map = new HashMap();
		// 查询所有
//		Query query = new SimpleQuery("*:*"); 
//		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
//		query.addCriteria(criteria);
//		ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
//		map.put("rows", page.getContent());
		// 查询列表
		Map searchList = searchList(searchMap);
		// 分组查询 商品分类列表
		List<String> categoryList = searchCategoryList(searchMap);
		map.putAll(searchList);
		map.put("categoryList", categoryList);

		String category = (String) searchMap.get("category");
		// 判断传递过来的规格是否为空
		// 如果不为空 按传递过来的分类名称筛选
		// 如果为空
		if (!"".equals(category)) {
			map.putAll(searchBrandAndSpecList(category));
		} else {
			if (categoryList.size() > 0) {
				map.putAll(searchBrandAndSpecList(categoryList.get(0)));
			}
		}

		return map;
	}

	private Map searchList(Map searchMap) {
		Map map = new HashMap();
		HighlightQuery query = new SimpleHighlightQuery();
		// 构建高亮对象
		HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
		// 设置前缀
		highlightOptions.setSimplePrefix("<em style='color:red'>");
		// 设置后缀
		highlightOptions.setSimplePostfix("</em>");
		// 设置高亮选择
		query.setHighlightOptions(highlightOptions);

		// 关键字查询
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);

		// 分类过滤
		if (!"".equals(searchMap.get("category"))) {
			FilterQuery filterQuery = new SimpleFilterQuery();
			Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
			filterQuery.addCriteria(filterCriteria);
			query.addFilterQuery(filterQuery);
		}

		// 按品牌分类过滤

		if (!"".equals(searchMap.get("brand"))) {
			FilterQuery filterQuery = new SimpleFilterQuery();
			Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
			filterQuery.addCriteria(filterCriteria);
			query.addFilterQuery(filterQuery);
		}

		// 规格过滤

		if (searchMap.get("spec") != null) {
			Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
			Set<String> keySet = specMap.keySet();
			for (String key : keySet) {
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
				filterQuery.addCriteria(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
		}
		// 价格过滤

		if (!"".equals(searchMap.get("price"))) {

			String price = (String) searchMap.get("price");
			// 价格区间
			String[] price_interval = price.split("-");
			// 如果最低价格不等于0 ==>此为专门跳过[0-500]这个区间的 0的值 这样只用走第二个if判断小于500这个值即可
			// 如果价格区间在[3000-*] 第一个值是不等于0的 则只会走第一个if判断 大于这个3000的值即可 不会走第二个if的小于判断
			if (!price_interval[0].equals("0")) {
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price_interval[0]);
				filterQuery.addCriteria(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
			if (!price_interval[1].equals("*")) {
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price_interval[1]);
				filterQuery.addCriteria(filterCriteria);
				query.addFilterQuery(filterQuery);
			}

		}
		// 提取当前页码
		Integer pageNo = (Integer) searchMap.get("pageNo");
		if (pageNo == null) {
			pageNo = 1;
		}
		// 提取每页显示记录数
		Integer pageSize = (Integer) searchMap.get("pageSize");
		if (pageSize == null) {
			pageSize = 20;
		}
		// 通过当前页 和 每页显示条数计算出起始索引
		query.setOffset((pageNo - 1) * pageSize);
		query.setRows(pageSize);

		String sortValue = (String) searchMap.get("sortValue");
		String sortField = (String) searchMap.get("sortField");
		if (sortValue != null && !"".equals(sortValue)) {
			if ("ASC".equals(sortValue)) {
				System.out.println("进入了ASC");
				Sort sort = new Sort(Sort.Direction.ASC, "item_"+sortField); // 升序 价格由低到高
				query.addSort(sort);
			} 
			if ("DESC".equals(sortValue)) {
				System.out.println("进入了DESC");
				Sort sort = new Sort(Sort.Direction.DESC, "item_"+sortField); // 降序 价格由高到底
				query.addSort(sort);
			}
		}
		// ***************************************************************************************
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
		// 高亮入口集合
		List<HighlightEntry<TbItem>> entryList = page.getHighlighted();

		for (HighlightEntry<TbItem> entry : entryList) {
			// 获取高亮列表
			List<Highlight> highlightList = entry.getHighlights();
			if (highlightList.size() > 0 && highlightList.get(0).getSnipplets().size() > 0) {
				String string = highlightList.get(0).getSnipplets().get(0);
				TbItem entity = entry.getEntity();
				entity.setTitle(string);
			}
		}
		List<TbItem> searchlist = page.getContent();
		map.put("rows", searchlist);
		// 总页数 用于前端处理分页栏
		map.put("totalPages", page.getTotalPages());
		// 数据总条数
		map.put("totalNum", page.getTotalElements());
		return map;
	}

	/**
	 * 分组查询 商品分类列表
	 * 
	 * @param searchMap
	 * @return
	 */
	private List<String> searchCategoryList(Map searchMap) {
		List<String> list = new ArrayList<String>();
		Query query = new SimpleQuery();
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions);
		// 获取分组页
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
		// 获取分组结果对象
		GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
		// 获取分组入口页
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		// 获取分组入口集合
		List<GroupEntry<TbItem>> entryList = groupEntries.getContent();
		for (GroupEntry<TbItem> entry : entryList) {
			list.add(entry.getGroupValue());
		}
		return list;
	}

	/**
	 * 根据商品分类名称查询品牌和规格列表
	 * 
	 * @return
	 */
	@Autowired
	private RedisTemplate redisTemplate;

	private Map searchBrandAndSpecList(String category) {
		Map map = new HashMap();
		Long templateId = (Long) redisTemplate.boundHashOps("itemCat").get(category);

		if (templateId != null) {
			List brandList = (List) redisTemplate.boundHashOps("brandList").get(templateId);
			map.put("brandList", brandList);
			List specList = (List) redisTemplate.boundHashOps("specList").get(templateId);
			map.put("specList", specList);

		}
		return map;
	}

	@Override
	public void importList(List list) {
		// TODO Auto-generated method stub
		System.out.println(list.size()+"list.size()list.size()");
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
	}

	@Override
	public void deleteByGoodsIds(List goodsIdList) {
		// TODO Auto-generated method stub
		Query query = new SimpleQuery();
		Criteria criteria= new Criteria("item_goodsid").in(goodsIdList);
		query.addCriteria(criteria);
		solrTemplate.delete(query);
		solrTemplate.commit();
	}
}
