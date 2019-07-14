app.controller('cartController', function ($scope, cartService) {


    //查询购物车列表
    $scope.findCartList = function () {

        cartService.findCartList().success(function (data) {

            $scope.cartList = data;

            //页码刷新求购物车合计
            $scope.totalValue = cartService.sum($scope.cartList);


            $scope.getLoginUser();


        })

    }


    //修改商品数量
    $scope.addGoodsToCartList = function (itemId, num) {

        cartService.addGoodsToCartList(itemId, num).success(function (data) {

            if (data.success) {

                $scope.findCartList();//刷新列表

            } else {
                alert(data.message);
            }

        })

    }

    //获取当前登录用户id
    $scope.getLoginUser = function () {

        cartService.getLoginUser().success(function (data) {

            $scope.loginUsername = data.split("\"")[1];


        })

    }

    //检查登录状态
    $scope.checkLogin = function () {
        return $scope.loginUsername == 'anonymousUser';
    }

    //获取当前用户收获地址、信息列表
    $scope.findAddressByUser = function () {
        $scope.getLoginUser();
        cartService.findAddressByUser().success(function (data) {

            var reg = /^(\d{3})\d+(\d{4})$/;

            for(var i = 0; i<data.length ; i++){

                $scope.mobile = data[i].mobile.replace(reg,"$1****$2");

                if(data[i].isDefault == '1'){
                    $scope.address = data[i];
                }
            }

            $scope.addressList = data;


        })

    }


    //选择地址
    $scope.selectAddress = function (address) {

        $scope.address = address;

    }

    //判断是否当前选中地址
    $scope.isSelectedAddress = function (address) {

        return address == $scope.address;

    }

    //支付类型默认为1
    $scope.order = {paymentType:'1'}

    //选择支付方式
    $scope.selectPayType = function (type) {

        $scope.order.paymentType = type;

    }

    //提交订单
    $scope.submitOrder = function () {

        $scope.order.receiverAreaName = $scope.address.address;//收货地址
        $scope.order.receiverMobile = $scope.address.mobile;//联系电话
        $scope.order.receiver = $scope.address.contact;//联系人

        cartService.submitOrder($scope.order).success(function (data) {

            if(data.success){
                if($scope.order.paymentType == '1'){//如果是微信支付 跳转支付页面

                    location.href = "pay.html";
                }else{//货到付款，跳转提示页面

                    location.href = "paysuccess.html";
                }
            }else{//订单提交失败
                alert(data.message);
            }

        })

    }



})