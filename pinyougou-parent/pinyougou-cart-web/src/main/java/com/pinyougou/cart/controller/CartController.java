package com.pinyougou.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojogroup.Cart;

import entity.Result;
import util.CookieUtil;

@RestController
@RequestMapping("/cart")
public class CartController {

	@Reference(timeout = 6000)
	private CartService cartService;

	@Autowired
	private HttpServletRequest request;
	@Reference(timeout = 6000)
	private OrderService orderService;

	@Autowired
	private HttpServletResponse response;

	@CrossOrigin(origins = "http://localhost:9105", allowCredentials = "true")
	@RequestMapping("/findCartList")
	public List<Cart> findCartList() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("当前登录人findCartList:" + username);

		String cookieValue = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
		if (cookieValue == null || cookieValue.equals("")) {
			cookieValue = "[]";
		}
		List<Cart> cartList_cookie = JSON.parseArray(cookieValue, Cart.class);

		if ("anonymousUser".equals(username)) {
			return cartList_cookie;
		} else {
			List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
			if (cartList_cookie.size() > 0) {
				// 得到合并后的购物车
				System.out.println("执行合并购物车");
				List<Cart> mergeCartList = cartService.mergeCartList(cartList_redis, cartList_cookie);
				cartService.saveCartListToRedis(username, mergeCartList);
				CookieUtil.deleteCookie(request, response, "cartList");
				return mergeCartList;
			}
			return cartList_redis;
		}
	}

	@RequestMapping("/addGoodsToCartList")
	@CrossOrigin(origins = "http://localhost:9105", allowCredentials = "true")
	public Result addGoodsToCartList(Long itemId, Integer num) {
		// 当前登录人账号
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("当前登录人：" + name);

		try {
			// 提取购物车
			List<Cart> cartList = findCartList();
			// 调用服务方法操作购物车
			cartList = cartService.addGoodsToCartList(cartList, itemId, num);

			if (name.equals("anonymousUser")) {// 如果未登录
				// 将新的购物车存入cookie
				String cartListString = JSON.toJSONString(cartList);
				util.CookieUtil.setCookie(request, response, "cartList", cartListString, 3600 * 24, "UTF-8");
				System.out.println("向cookie存储购物车");

			} else {// 如果登录
				cartService.saveCartListToRedis(name, cartList);
			}

			return new Result(true, "存入购物车成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "存入购物车失败");
		}

	}

	@RequestMapping("/text")
	public void text() {
		orderService.text();
	}



}
