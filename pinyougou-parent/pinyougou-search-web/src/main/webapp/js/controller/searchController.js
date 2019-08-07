app.controller("searchController", function($scope,$location, searchService) {
	$scope.searchMap = {
		"keywords" : "", // 关键字
		"category" : "", // 分类
		"brand" : "", // 品牌
		"price" : "", // 价格
		"spec" : {}, // 规格
		"pageNo" : 1, // 当前页
		"pageSize" :20 , // 每页显示条数
		"sortValue":"",
		"sortField":""
	}
	
	$scope.search = function() {
		
		searchService.search($scope.searchMap).success(function(response) {
			$scope.resultMap = response;
			buildPageLabel();
		})
	}
	$scope.addSearchItem = function(key, value) {
		if (key == "category" || key == "brand" || key == "price") {
			$scope.searchMap[key] = value;
		} else {
			$scope.searchMap.spec[key] = value;
		}
		$scope.search();
	}
	$scope.removeSearchItem = function(key) {
		if (key == "category" || key == "brand" || key == "price") {
			$scope.searchMap[key] = '';
		} else {
			delete $scope.searchMap.spec[key];
		}
		$scope.search();
	}
	// 构建分页标签
	
	buildPageLabel = function() {
		$scope.pageLabel = [];
		var firstPage = 1; // 开始页码
		var lastPage = $scope.resultMap.totalPages; // 最大页码
		$scope.firstDot=true;//前面有点
		$scope.lastDot=true;//后边有点
		
		
		//如果最大页码小于五 则跳过此if判断 例如 页码为4  则firstPage=1 ,lastPage=4 走下面的for循环4次
		if ($scope.resultMap.totalPages > 5){
		//进入到这里代表总页数大于5    再判断 当前页面是否小于等于3 如果在这个区间  让lastPage = 5  循环结束 然后for循环5次  直接显示1到5页即可 页码大于5的不予显示 
			if ($scope.searchMap.pageNo <= 3) {
				$scope.firstDot=false;
				lastPage = 5;
		//如果当前页面大于3  此时不会走第一个if判断 进入到第二个判断 如果当前页面大于response返回来的总页码-2 显示最大页码减去4 例如返回100页 此时lastPage=100 然后这里 的情况 都让开始索引为96  显示 96 97 98 99 100	
			} else if ($scope.searchMap.pageNo >= $scope.resultMap.totalPages-2) {
				firstPage = $scope.resultMap.totalPages-4;
		//以上两个情况排除后 正常显示为开始索引为当前页-2 结束索引为 当前页+2 加起来为显示 5页	
				$scope.lastDot=false;
			}else{
				 firstPage=$scope.searchMap.pageNo-2;
				lastPage=$scope.searchMap.pageNo+2;
			}
		}else{
			$scope.firstDot=false;
			$scope.lastDot=false;
		}
		for(var i=firstPage;i <=lastPage; i++ ){
			//这里push的是开始索引和结束索引
			$scope.pageLabel.push(i);
		}
	}
	
	//判断当前页是否为第一页
	$scope.isTopPage=function(){
		if($scope.searchMap.pageNo==1){
			return true;
		}else{
			return false;
		}		
	}
	
	//判断当前页是否为最后一页
	$scope.isEndPage=function(){
		if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
			return true;
		}else{
			return false;
		}	
	}
	//这个方法的 定义//前端传参问题 ......

	$scope.queryByPage=function(pageNo){
		$scope.searchMap.pageNo= parseInt($scope.searchMap.pageNo);

		if(pageNo<1 || pageNo>$scope.resultMap.totalPages){
			return ;
		}		
		$scope.searchMap.pageNo=pageNo;
		$scope.search();//查询
	}
	$scope.sortSearch=function(sortField,sortValue){
		alert(sortField+sortValue);
		$scope.searchMap.sortValue=sortValue;
		$scope.searchMap.sortField=sortField;
		$scope.search();
	}
	$scope.keywordsisBrand=function(){
			var brandList=	$scope.resultMap.brandList;
			for(var i=0 ; i < brandList.lenght;i++){
				if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
					return true;
				}
			}
			return false;
	}
	$scope.loadkeywords=function(){
		$scope.searchMap.keywords= $location.search()['keywords'];
		$scope.search();
	}
	
})