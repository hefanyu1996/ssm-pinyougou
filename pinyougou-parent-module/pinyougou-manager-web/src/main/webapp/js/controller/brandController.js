app.controller('brandController', function ($scope, $http) {
    //查询所有
    $scope.reloadList = function () {
        $scope.search(
            $scope.paginationConf.currentPage,
            $scope.paginationConf.itemsPerPage
        );
        //每次删除操作后清空id数组
        $scope.selectIds = [];
    };

    //分页属性配置
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 8,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();//重新加载
        }
    };
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