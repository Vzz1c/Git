app.controller('brandController', function($scope,$controller,brandService) {
	$controller("baseController",{$scope:$scope})
		$scope.findAll = function() {
			brandService.findAll().success(function(response) {
				$scope.list = response;
			})
		}
	
	
	 $scope.findPage = function(pageNum, pageSize) {
		 brandService.findPage(pageNum, pageSize).success(function(response) {
			$scope.list = response.rows
			$scope.paginationConf.totalItems=response.total;
		})
	} 
	$scope.save=function(){
		var Object=null;
		if ($scope.entity.id!=null) {
			Object=brandService.update($scope.entity);
		}else{
			Object=brandService.add($scope.entity);
		}
		Object.success(
			function(response) {
				if (response.success) {
					$scope.reloadList();
				}else {
					alert(response.message);
				}
			})
	}
	$scope.findById=function(id){
		brandService.findById(id).success(
				function(response){
					alert(response.name);
					$scope.entity=response;	
		})
	}
	
	$scope.dele=function(){
		if (confirm("确定删除?")) {
			brandService.dele($scope.selectIds).success(function(response){
				if(response.success){
					$scope.selectIds=[];
					$scope.reloadList();//刷新
				}else{
					alert(response.message);
				}	
			})
		}
	};
	$scope.searchEntity={};
	$scope.search=function(pageNum, pageSize){ 
		brandService.search(pageNum, pageSize,$scope.searchEntity).success(function(response) {
				$scope.list = response.rows
				$scope.paginationConf.totalItems=response.total;
			})
	}
})