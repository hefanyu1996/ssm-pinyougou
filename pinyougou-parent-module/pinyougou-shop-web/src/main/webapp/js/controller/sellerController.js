 //控制层 
app.controller('sellerController' ,function($scope,$controller   ,sellerService){	
	
	$controller('baseController',{$scope:$scope});//继承
	

	
	//保存 
	$scope.add=function(){

		var protocolCb = document.getElementById('protocol_cb');
		alert(protocolCb.checked);
		if(protocolCb.checked){
			sellerService.add().success(
				function(response){
					if(response.success){
						//跳转登录页
						location.href='shoplogin.html';
					}else{
						alert(response.message);
					}
				}
			);
		}else{
			alert("同意协议才能注册")
		}

	}

	//修改
	$scope.update=function(){
		sellerService.update().success(
			function(response){
				if(response.success){
					//跳转登录页
					location.href='shoplogin.html';
				}else{
					alert(response.message);
				}
			}
		);
	}

    
});	
