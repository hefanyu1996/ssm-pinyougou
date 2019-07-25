//控制层
app.controller('seckillGoodsController', function ($scope, $location, $interval, seckillGoodsService) {

    /**
     * 查询正在秒杀的商品列表
     */
    $scope.findList = function () {

        seckillGoodsService.findList().success(function (data) {

            $scope.list = data;

        })

    }

    /**
     * 商品详情页查询
     */
    $scope.findOneFromRedis = function () {

        seckillGoodsService.findOneFromRedis($location.search()['id']).success(function (data) {

            $scope.entity = data;

            //获取秒杀商品限时秒数
            allsecond = Math.floor((new Date($scope.entity.endTime).getTime() - new Date().getTime()) / 1000);

            time = $interval(function () {

                if (allsecond > 0) {
                    allsecond = allsecond - 1;
                    $scope.timeString = convertTimeString(allsecond);//时间转字符串 并 格式化
                } else {
                    $interval.cancel(time);
                    alert("该商品秒杀已结束");
                    location.href = 'seckill-index.html';
                }

            }, 1000)

        })

    }


    //转换秒为   天小时分钟秒格式  XXX天 10:22:33

    convertTimeString = function (allsecond) {

        var day = Math.floor(allsecond / (60 * 60 * 24));
        var hours = Math.floor((allsecond - (day * 60 * 60 * 24)) / (60 * 60));
        var minutes = Math.floor((allsecond - day * 60 * 60 * 24 - hours * 60 * 60) / 60);
        var seconds = allsecond - day * 60 * 60 * 24 - hours * 60 * 60 - minutes * 60;

        var timeString = '';

        if (day > 0) {
            timeString = day + "天 ";
        }

        hours =  hours < 10 ? "0" + hours : hours;
        minutes =minutes < 10 ? "0" + minutes : minutes;
        seconds = seconds < 10 ? "0" + seconds : seconds
        return timeString + hours + ":" + minutes + ":" + seconds;

    }


    //商品下单
    $scope.submitOrder = function () {

        seckillGoodsService.submitOrder($scope.entity.id).success(function (data) {

            alert(data.message);

            if(data.message == "用户未登录"){

                location.href = 'login/cas';

            }

            if (data.success) {

                location.href = 'pay.html';//跳转支付页面

            }

        })


    }


});	
