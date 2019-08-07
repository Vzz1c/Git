package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/brand")
public class BrandController {
	@Reference
	private BrandService brandService;
	@RequestMapping("/findAll")
	public List<TbBrand> findAll() {
		return brandService.findAll();
	}
	@RequestMapping("/findPage")
	public PageResult findPage(int page,int rows) {
		PageResult findPage = brandService.findPage(page, rows);
	
		return findPage; 
	}
	@RequestMapping("/add")
	public Result add(@RequestBody TbBrand tbBrand) {
		Result add = brandService.add(tbBrand);
		return add;
	}
	@RequestMapping("/findById")
	public TbBrand findById(long id) {
		TbBrand tbBrand = brandService.findById(id);
		return tbBrand;
	}
	@RequestMapping("/update")
	public Result update(@RequestBody TbBrand tbBrand) {
		Result result = brandService.update(tbBrand);
		return result;
	}
	@RequestMapping("/delete")
	public Result delete(long[] ids) {
		Result result = brandService.delete(ids);
		return result;
	}
	@RequestMapping("/search")
	public PageResult serach(@RequestBody TbBrand tbBrand,int page,int rows) {
		PageResult result = brandService.findPage(tbBrand, page, rows);
		return result;
	}
	@RequestMapping("/selectOptionList")
	public List<Map> selectOptionList(){
		List<Map> maps = brandService.selectOptionList();
		System.out.println(maps);
		return maps;
	 }
	 
}
