 //控制层 
app.controller('userController' ,function($scope,$controller   ,userService){

	$scope.entity = {};

	//用户注册
	$scope.add = function () {

		var protocolCb = document.getElementById('protocol_cb');

		if(protocolCb.checked){

			if($scope.entity.password == '' ||$scope.check_password == ''
				||$scope.entity.password == null ||$scope.check_password ==null
				||$scope.entity.username == null ||$scope.username== '' ){
				alert("用户名或密码不能为空");
				return;
			}

			if($scope.entity.password != $scope.check_password){
				alert("两次密码输入不一致");
				$scope.entity.password = '';
				$scope.check_password = '';
				return;
			}

			userService.checkSms($scope.entity.phone,$scope.smsCode).success(function (data) {

				if(data.success){
					userService.add($scope.entity).success(
						function(response){
							if(response.success){
								//跳转登录页
								location.href='https://www.baidu.com';
							}else{
								alert(response.message);
							}
						}
					);
				}else{
					alert(data.message);
				}
			})


		}else{
			alert("同意协议才能注册")
		}

	}


	//获取短信验证码
	$scope.sendSms = function (phone) {

		if(phone==null || phone ==''){
			alert("手机号不能为空");
			return;
		}

		userService.sendSms(phone).success(function (data) {

			if(data.success){
				alert(data.message);
			}else{
				alert(data.message);
			}

		});
	}


    
});	
