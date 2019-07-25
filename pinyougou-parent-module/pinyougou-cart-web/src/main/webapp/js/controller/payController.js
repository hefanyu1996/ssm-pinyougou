app.controller('payController',function ($scope, payService,$location,$interval) {

    $scope.createNative = function () {

        payService.createNative().success(function (data) {

            $scope.money = (data.total_fee/100).toFixed(2);//付款总金额
            $scope.out_trade_no = data.out_trade_no;//订单号
            //二维码
            var qr = new QRious({
                element:document.getElementById("qrious"),
                size:250,
                level:'H',
                value:data.code_url
            })

            $scope.queryPayStatus(data.out_trade_no);//查询支付状态

            //二维码计时
            $interval(function () {

                $scope.time2dbc = $scope.time2dbc - 1;

            },1000,60)

        })
    }


    //二维码刷新标记
    $scope.time2dbc = 59;

    $scope.queryPayStatus = function (out_trade_no) {

        payService.queryPayStatus(out_trade_no).success(function (data) {

            if(data.success){

                location.href = 'paysuccess.html#?money='+$scope.money;

            }else{
                if(data.message=='二维码超时'){

                    $scope.time2dbc = 0;


                }else{

                    location.href = 'payfail.html#?message='+data.message;

                }

            }

        })
    }

    //显示金额
    $scope.getMoney = function () {

        return $location.search()['money'];

    }

    //显示支付失败原因
    $scope.getMessage = function () {

        return $location.search()['message'];
    }

    //重新生成二维码 重置时间
    $scope.reCreate2dbc = function () {

        $scope.time2dbc = 59;

        $scope.createNative();

    }




})