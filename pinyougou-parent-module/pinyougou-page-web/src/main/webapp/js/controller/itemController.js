app.controller('itemController', function ($scope) {

    //商品数量修改
    $scope.changeGoodsNum = function (n) {

        $scope.num = $scope.num + n;
        if($scope.num<1){
            $scope.num= 1;
        }
    }


    //记录用户选择规格
    $scope.specificationItems = {}
    //规格选中样式
    $scope.selectSpecification = function (key,value) {
        $scope.specificationItems[key]=value;
        searchSku();
    }

    //判断某规格选项是否被用户选中
    $scope.isSelected = function(key,value){
        if($scope.specificationItems[key]==value){
            return true;
        }
        return false;

    }

    //加载选中默认sku规格
    $scope.loadSku = function () {

        $scope.sku = skuList[0];

        $scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));

    }

    //判断两个对象是否相等
    matchObject = function(map1,map2){

        for(key in map1){
            if(map1[key] != map2[key] ){
                return false;
            }
        }

        for(key in map2){
            if(map2[key] != map1[key]){
                return false;
            }
        }

        return true;

    }

    //查询sku
    searchSku = function () {

        for(var i = 0 ; i < skuList.length ; i++){

            if(matchObject(skuList[i].spec,$scope.specificationItems)){
                $scope.sku = skuList[i];
                return;
            }


            $scope.sku = {id:0,title:'暂无',price:0};//如果没有匹配sku

        }


    }


    $scope.addToCart = function () {
        alert("skuid:"+$scope.sku.id);
    }

});