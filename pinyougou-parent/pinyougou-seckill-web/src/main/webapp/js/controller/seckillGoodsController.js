 //控制层 
app.controller('seckillGoodsController' ,function($scope,$controller   ,seckillGoodsService,$location,$interval){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		seckillGoodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		seckillGoodsService.findPage(page,rows).success(
			function(response){
				
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){
		$scope.seckillId();
		seckillGoodsService.findOne($scope.seckillId).success(
			function(response){
				$scope.entity= response;
				$scope.endTime=$scope.entity.endTime;
				allsecond=Math.floor((new Date($scope.endTime).getTime()-new Date().getTime())/1000)
				
			    time=$interval(function(){
			    	if(allsecond>0){
			    		allsecond=allsecond-1;
			    		$scope.timeString=convertTimeString(allsecond);
			    		
			    	}else{
			    		 $interval.cancel(time);
			    		 alert("秒杀服务已结束");
			    	}
			    	
			    },1000);
			}
		);				
	}
	convertTimeString=function(allsecond){
		var day = Math.floor(allsecond/3600/24); //天数
		var hour =Math.floor((allsecond-(day*3600*24))/3600); //小时
		var minutes= Math.floor((allsecond-(day*3600*24)-(hour*3600))/60) //分钟
		var second = Math.floor(allsecond-(day*3600*24)-(hour*3600)-(minutes*60)) // 秒数
		var time="";
		if(day>0){
			time+=day+"天 ";
		}
		if(hour>0){
			if(hour<10){
				time+="0"+hour+"小时:";
			}else{
				time+=hour+"小时:";
			}
			
		}
		if(minutes>0){
			if(minutes<10){
				time+="0"+minutes+"分钟";
			}else{
				time+=minutes+"分钟:";
			}
			
		}
		
		if(second<10){
			time=time+"0"+(second+"")+"秒";
		}else{
			time+=second+"秒";
		}
		return time;
	}
	
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=seckillGoodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=seckillGoodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		seckillGoodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	//搜索
	$scope.search=function(page,rows){		
		seckillGoodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    $scope.seckillId=function(){
    	$scope.seckillId=$location.search()['id'];
    }  
    $scope.submitOrder=function(){
    	alert($scope.entity.id);
    	seckillGoodsService.submitOrder($scope.entity.id).success(function(response){
    		if(response.success){
    			alert("下单成功,五分钟内完成支付")
    			location.href="pay.html";
    		}else{
    			location.href="login.html";
    			alert(response.message);
    		}
    	})
    }  
});	
