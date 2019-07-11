//服务层
app.service('userService',function($http){

	//增加
	this.add=function(entity){
		return  $http.post('../user/add.do',entity );
	}

	//获取短信验证码
	this.sendSms = function (phone) {
		return $http.get('../user/sendSms.do?phone='+phone)
	}

	//校验短信验证码
	this.checkSms = function (phone, smsCode) {

		return $http.get('../user/checkSms.do?phone='+phone+'&smsCode='+smsCode)

	}

});
