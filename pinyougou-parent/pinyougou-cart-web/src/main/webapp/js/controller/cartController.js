app.controller("cartController", function($scope, cartService) {
	$scope.findCartList = function() {
		cartService.findCartList().success(function(response) {
			$scope.cartList = response;
			$scope.totalValue = cartService.sum($scope.cartList);
		})
	}
	$scope.addGoodsToCartList = function(itemId, num) {
		cartService.addGoodsToCartList(itemId, num).success(function(response) {
			if (response.success) {
				$scope.findCartList();// 刷新列表
			} else {
				alert(response.message);// 弹出错误提示
			}
		});
	}
	// sum=function(){
	// $scope.totalNum=0;
	// $scope.totalMoney=0;
	// for(var i =0; i<$scope.cartList.length;i++){
	// var cart=$scope.cartList[i];
	// for(var j =0;j<cart.orderItemList.length;j++){
	// $scope.totalNum+=cart.orderItemList[j].num;
	// $scope.totalMoney+=cart.orderItemList[j].totalFee;
	// }
	// }
	// }
	$scope.findAddressListByUserId = function() {
		cartService.findAddressListByUserId().success(function(response) {
			$scope.addressList = response;

		});
	}
	$scope.addAddress = function() {
		cartService.addAddress($scope.address).success(function(response) {
			if (response.success) {
				alert(response.message);
				location.reload()
			} else {
				alert(response.message);
			}
		})
	}
	$scope.selectAddress = function(address) {
		$scope.address = address;
	}

	$scope.isSelectedAddress = function(address) {
		return $scope.address == address;
	}
	$scope.order = {
		paymentType : "1"
	};
	$scope.selectPayType = function(type) {
		$scope.order.paymentType = type;
	}
	$scope.submitOrder = function() {
		$scope.order.receiverAreaName = $scope.address.address;// 地址
		$scope.order.receiverMobile = $scope.address.mobile;// 手机
		$scope.order.receiver = $scope.address.contact;// 联系人
		cartService.submitOrder($scope.order).success(function(response) {
			if (response.success) {
				// 页面跳转
				if ($scope.order.paymentType == '1') {// 如果是微信支付，跳转到支付页面
					location.href = "pay.html";
				} else {// 如果货到付款，跳转到提示页面
					location.href = "paysuccess.html";
				}
			} else {
				alert(response.message); // 也可以跳转到提示页面
			}
		});
	}

})