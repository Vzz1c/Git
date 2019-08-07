app.controller("baseController",function($scope){
	$scope.selectIds=[];
	$scope.updateSelection=function($event,id){
		if ($event.target.checked) {
			$scope.selectIds.push(id);
		}else{
			var index= $scope.selectIds.indexOf(id);//查找值的 位置
			$scope.selectIds.splice(index,1);//参数1：移除的位置 参数2：移除的个数  
		}
		
	};
	$scope.paginationConf = {
		
			//currentPage 当前页 ;totalItems 记录; itemsPerPage 每页记录数 ; perPageOptions分页下拉选择
			currentPage : 1,
			totalItems : 10,
			itemsPerPage : 10,
			perPageOptions : [ 10, 20, 30, 40, 50 ],
			onChange : function() {
				$scope.reloadList(); 
			}
		};
		$scope.reloadList=function(){
			$scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
		};
    /**
     * @return {string}
     */
    $scope.JsonToString=function (jsonString,key) {
		var json = JSON.parse(jsonString);
		var value="";
        for(var i=0; i<json.length;i++) {
        	if(i>0) {
                value += "," + json[i][key]
            }else {
				value+=json[i][key]
            }

        }
		return value;
    }
    //从集合中按照Key值查询
    $scope.searchObjectByKey=function(list,key,keyValue){
    	for(var i=0;i<list.length;i++){
    	if(list[i][key]==keyValue){
    	return list[i];
    	}
    	}
    	return null;
    	}
    
});