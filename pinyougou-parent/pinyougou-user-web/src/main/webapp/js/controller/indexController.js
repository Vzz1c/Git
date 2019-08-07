app.controller('indexController', function($scope,loginService) {

	$scope.showName = function() {
		loginService.showName().success(function(response) {
			alert("经过了showName indexController");
			$scope.loginName=response.loginName;
		});
	}
})
	