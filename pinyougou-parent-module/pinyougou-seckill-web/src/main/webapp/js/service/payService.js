app.service('payService',function ($http) {

    //创建本地统一下单
    this.createNative = function () {

        return $http.get('pay/createNative.do');

    }

    //查询订单状态
    this.queryPayStatus = function (out_trade_no) {
        return $http.get('pay/queryPayStatus.do?out_trade_no='+out_trade_no)
    }


})