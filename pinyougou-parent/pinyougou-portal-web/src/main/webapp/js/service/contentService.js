app.service("contentService",function($http){
	this.findByCotegoryId=function(cotegoryId){
		return $http.get("portal/findByCotegoryId.do?cotegoryId="+cotegoryId);
	}
})