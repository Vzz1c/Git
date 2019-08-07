app.service("uploadService",function($http){
	this.uploadFile=function(){
		alert("经过了前端uploadService的uploadFile方法");
		var formData = new FormData();
		formData.append("file",file.files[0]);
		return $http({
			url:"../upload.do",
			method:"post",
			data:formData,
			headers:{"Content-Type":undefined},
			transformRequest:angular.identity
		})
	}
}) 