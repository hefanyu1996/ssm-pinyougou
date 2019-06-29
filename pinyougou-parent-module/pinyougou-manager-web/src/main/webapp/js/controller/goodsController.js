//控制层
app.controller('goodsController', function ($scope, $controller, goodsService, itemCatService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.reloadList();//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //商品状态
    $scope.statusName = ["未审核", "审核通过", "审核未通过", "已关闭"];

    //商品分类
    $scope.itemCatList = [];

    //查询所有商品分类
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(function (data) {
            for (var i = 0; i < data.length; i++) {

                $scope.itemCatList[data[i].id] = data[i].name;

            }
        })
    }


    //审核商品
    $scope.auditGoods = function (status) {
        var msg = '';
        if (status == 1) {
            msg = '审核通过确认';
        }
        if (status == 2) {
            msg = '审核驳回确认';
        }
        if (status == 3) {
            msg = '审核关闭确认';
        }

        if (confirm(msg)) {
            goodsService.auditGoods(status, $scope.selectIds).success(function (data) {
                if (data.success) {
                    alert(data.message);
                    $scope.reloadList();
                } else {
                    alert(data.message)
                }
            })
        }

    }

});	
