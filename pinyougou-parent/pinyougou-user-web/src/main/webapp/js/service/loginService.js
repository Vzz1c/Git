app.service("loginService",function($http){
	this.showName=function(){
		alert("经过了loginService");
		return $http.get("../showName.do")
	}
})