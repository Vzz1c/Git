package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;
@Service
@Transactional
public class BrandServiceImpl implements BrandService{
	@Autowired
	private TbBrandMapper brandMapper;
	@Override
	public List<TbBrand> findAll() {
		// TODO Auto-generated method stub
		return brandMapper.selectByExample(null);
	}
	@Override
	public PageResult findPage(int pageNum,int pageSize) {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNum,pageSize);
		Page<TbBrand> page = (Page<TbBrand>)brandMapper.selectByExample(null);
		return new PageResult(page.getTotal(),page.getResult());
	}
	@Override
	public Result add(TbBrand tbBrand) {
		// TODO Auto-generated method stub
		try {
			brandMapper.insert(tbBrand);
			return new Result(true,"添加成功");
		} catch (Exception e) {

			// TODO Auto-generated catch block
			
			return new Result(false,"添加失败");
		}			
	}
	@Override
	public TbBrand findById(long id) {
	
		// TODO Auto-generated method stub
		
		TbBrand tbBrand = brandMapper.selectByPrimaryKey(id);
		return tbBrand;
	}
	@Override
	public Result update(TbBrand tbBrand) {
		// TODO Auto-generated method stub
		try {
			brandMapper.updateByPrimaryKey(tbBrand);
			return new Result(true,"修改成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
		
			return new Result(false,"修改失败");
		}
	}
	@Override
	public Result delete(long[] ids) {
		// TODO Auto-generated method stub
		try {
			for (int i = 0; i < ids.length; i++) {
				brandMapper.deleteByPrimaryKey(ids[i]);
			}
			return new Result(true,"删除成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
		
			return new Result(false,"删除失败");
		}
		
	}
	@Override
	public PageResult findPage(TbBrand tbBrand, int pageNum, int pageSize) {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNum, pageSize);
		TbBrandExample example = new TbBrandExample();
		Criteria createCriteria = example.createCriteria();
		if(tbBrand!=null&&!"".equals(tbBrand)) {
			if (tbBrand.getName()!=null&&tbBrand.getName().length()>0){
				createCriteria.andNameLike("%"+tbBrand.getName()+"%");
			}
			if (tbBrand.getFirstChar()!=null&&tbBrand.getFirstChar().length()>0){
				createCriteria.andFirstCharLike("%"+tbBrand.getFirstChar()+"%");
			}
		}
		Page<TbBrand> page = (Page<TbBrand>)brandMapper.selectByExample(example);
		return new PageResult(page.getTotal(),page.getResult());
	}
	

	 @Override 
	 public List<Map> selectOptionList() {
		 	return brandMapper.selectOptionList();
		 	}

} 
