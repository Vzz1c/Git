 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID

			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			
			$scope.entity.parentId=$scope.parentId;
		
			serviceObject=itemCatService.add( $scope.entity  );//增加 
			
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
					$scope.findByParentId($scope.parentId);
				}else{
					alert(response.message);
				}
				
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		if( $scope.selectIds.lenght>0){
			//获取选中的复选框			
			itemCatService.dele( $scope.selectIds ).success(
				function(response){
					if(response.success){
						$scope.findByParentId($scope.parentId);//刷新列表
						$scope.selectIds=[];
					}				
				}		
			);	
		}else{
			alert("选择后再删除");
		}
					
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	//查询下一级菜单
	$scope.parentId=0;;
    $scope.findByParentId=function(parentId){
    	$scope.parentId=parentId;
    	itemCatService.findByParentId(parentId).success(function(response){
    		$scope.list=response
    	})
    }
    $scope.grade=1;
    $scope.setGrade=function(value){
    	$scope.grade=value;
    }
    $scope.selectList=function(p_entity){
    	if($scope.grade==1){
    		$scope.p1_entity=null;
    		$scope.p2_entity=null;
    	
    	}
    	if($scope.grade==2){
    		$scope.p1_entity=p_entity
    		$scope.p2_entity=null;
    	}
    	if($scope.grade==3){
    		$scope.p2_entity=p_entity;
    	}
    	$scope.findByParentId(p_entity.id); 
    }
    
    
    $scope.selectAll = function () {
       
        if (!$scope.checked) {
            // 如果是选中状态,则将所有id加入进来
            for (var i = 0; i < $scope.list.length; i++) {
                $scope.selectIds.push($scope.list[i].id);
            }
        } else {
            // 如果全选框为未选中,则初始化selectIds
            $scope.selectIds = [];
        }
        
    }

});	
