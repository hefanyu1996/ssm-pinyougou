app.controller('brandController', function ($scope,$controller,brandService) {

    $controller('baseController',{$scope:$scope});


    //分页查询(已代替)
    /*$scope.findPage = function (currPage, pageSize) {
        $http.get('../brand/findByPage.do?currPage=' + currPage + '&pageSize=' + pageSize).success(
            function (data) {
                $scope.list = data.rows;
                $scope.paginationConf.totalItems = data.total;//更新总记录数
            }
        );
    };*/

    //新增/修改品牌
    $scope.save = function () {
        var object = brandService.save($scope.entity);
        if ($scope.entity.id != null) {
            object = brandService.update($scope.entity);
        }

        object.success(
            function (data) {
                if (data.success) {
                    $scope.reloadList();//重新加载
                } else {
                    alert(data.message);
                }
            }
        )
    };

    //修改回显
    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (data) {
                $scope.entity = data;
            }
        )
    };

    //批量删除
    $scope.remove = function () {

        if (confirm("确定要删除吗?")) {
            brandService.remove($scope.selectIds).success(
                function (data) {
                    if (data.success) {
                        $scope.reloadList();
                    } else {
                        alert(data.message);
                    }

                }
            )
        }
        //每次删除操作后情况id数组（建议将此操作放入reloadList）
        // $scope.selectIds = [];
    };

    //查询条件
    $scope.searchEntity= {};

    //条件分页查询
    $scope.search = function (currPage,pageSize) {
        brandService.search(currPage,pageSize,$scope.searchEntity).success(
            function (data) {
                $scope.list = data.rows;
                $scope.paginationConf.totalItems = data.total;//更新总记录数
            }
        )
    }


});