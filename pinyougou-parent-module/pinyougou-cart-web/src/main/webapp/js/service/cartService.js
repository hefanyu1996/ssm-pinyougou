app.service('cartService',function ($http) {


    //查询购物车列表
    this.findCartList = function () {

        return $http.get('../cart/findCartList.do?');

    }

    //修改商品数量
    this.addGoodsToCartList = function (itemId,num) {

        return $http.get('../cart/addGoodsToCartList.do?itemId='+itemId+'&num='+num);

    }

    //购物车商品合计
    this.sum = function (cartList) {

        var totalValue = {totalNum: 0, totalMoney: 0.00};//合计实体

        for (var i = 0; i < cartList.length; i++) {

            var cart = cartList[i];
            for (var j = 0; j < cart.orderItemList.length; i++) {
                var orderItem = cart.orderItemList[j];//购物车明细
                totalValue.totalNum += orderItem.num;
                totalValue.totalMoney += orderItem.totalFee;

            }

        }
        return totalValue;
    }


});