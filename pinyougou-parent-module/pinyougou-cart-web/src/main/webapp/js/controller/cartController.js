app.controller('cartController', function ($scope, cartService) {


    //查询购物车列表
    $scope.findCartList = function () {

        cartService.findCartList().success(function (data) {

            $scope.cartList = data;

            //页码刷新求购物车合计
            $scope.totalValue = cartService.sum($scope.cartList);

        })

    }


    //修改商品数量
    $scope.addGoodsToCartList = function (itemId,num) {

        cartService.addGoodsToCartList(itemId,num).success(function (data) {

            if(data.success){

                $scope.findCartList();//刷新列表

            }else{
                alert(data.message);
            }

        })

    }





})