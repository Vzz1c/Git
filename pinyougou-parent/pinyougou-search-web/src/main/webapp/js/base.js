var app = angular.module('pinyougou', []);
//定义一个过滤器
app.filter("trustHtml",["$sce",function($sce){
	return function(data){
		return $sce.trustAsHtml(data);//返回过滤后的内容
	}
}])