package com.pinyougou.portal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;

@RestController
@RequestMapping("/portal")
public class ContentController {
	@Reference
	private ContentService contentService;
	@RequestMapping("/findByCotegoryId")
	public List<TbContent> findByCotegoryId(Long cotegoryId){
		List<TbContent> list = contentService.findByCotegoryId(cotegoryId);
		return list;
	}
}
