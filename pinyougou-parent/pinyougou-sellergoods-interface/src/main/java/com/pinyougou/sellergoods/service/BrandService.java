package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;
import entity.Result;

/**
 * 品牌列表
 * @author Vzz1c
 *
 */
public interface BrandService {
	
	/**
	 * 查询所有
	 * @return
	 */
	public List<TbBrand> findAll();
	/**
	 * 分页显示
	 * @param pageNum 当前页面
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	/**
	 * 添加
	 * @param tbBrand
	 * @return
	 */
	public Result add(TbBrand tbBrand);
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public TbBrand findById(long id);
	/**
	 * 修改
	 * @param tbBrand
	 * @return
	 */
	public Result update(TbBrand tbBrand);
	/**
	 * 根据id删除
	 * @param ids
	 * @return
	 */
	public Result delete(long[] ids);
	/**
	 * 分页&&模糊查询
	 * @param tbBrand
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public PageResult findPage(TbBrand tbBrand,int pageNum,int pageSize);
	public List<Map> selectOptionList();
}
