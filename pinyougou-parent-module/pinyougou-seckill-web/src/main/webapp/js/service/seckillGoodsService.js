//服务层
app.service('seckillGoodsService', function ($http) {

    //读取列表数据绑定到表单中
    this.findList = function () {

        return $http.get('seckillGoods/findList.do');

    }


    //商品详情页查询
    this.findOneFromRedis = function (id) {

        return $http.get('seckillGoods/findOneFromRedis.do?id='+id)

    }

    //商品下单
    this.submitOrder = function (id) {

        return $http.get('seckillOrder/submitOrder.do?id='+id);

    }

});
