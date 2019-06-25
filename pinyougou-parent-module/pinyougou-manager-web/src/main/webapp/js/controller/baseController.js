app.controller('baseController', function ($scope) {

    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 8,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();//重新加载
        }
    };

    //刷新列表
    $scope.reloadList = function () {
        $scope.search(
            $scope.paginationConf.currentPage,
            $scope.paginationConf.itemsPerPage
        );
        //每次删除操作后清空id数组
        $scope.selectIds = [];
    };


    //选中的id集合
    $scope.selectIds = [];

    //更新复选
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {
            $scope.selectIds.push(id);//将选中id添加到集合
        } else {
            var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx, 1);//删除集合中非选id
        }
    };


    //全选
    $scope.selectAll = function ($event) {
        if ($event.target.checked) {
            $scope.isAllSelect = true;
            $scope.isSelect = true;
            angular.forEach($scope.list, function (entity, index) {
                $scope.selectIds.push(entity.id);
            });
        } else {
            $scope.isAllSelect = false;
            $scope.isSelect = false;
            $scope.selectIds = [];
        }
    };

    $scope.json2String = function (jsonString, key) {
        var value = "";
        if (jsonString != null) {

            var json = JSON.parse(jsonString);

            for (var i = 0; i < json.length; i++) {
                if (i > 0) {
                    value += "，";
                }
                value += json[i][key];
            }
        }
        return value;
    }


});