app.controller("indexController",function($scope,loginService){
	loginService.loginName().success(function(response){
		$scope.loginName=response.loginName;
	})
})