//控制层
app.controller('itemCatController', function ($scope, $controller, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        itemCatService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        itemCatService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        itemCatService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }


    //select2 config
    $scope.options = {data: []};

    $scope.findTypeOptions = function () {
        typeTemplateService.findTypeOptions().success(function (data) {
            $scope.options = {data: data}
        })
    }

    //保存
    $scope.save = function (parentId) {
        var serviceObject;//服务层对象
        if ($scope.entity.id == null) {//如果有ID
            $scope.entity.parentId = parentId;
            serviceObject = itemCatService.add($scope.entity);//增加
        } else {
            serviceObject = itemCatService.update($scope.entity); //修改
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    $scope.findEntity();
                    $scope.selectList($scope.z_entity);//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        if(confirm("确认要删除吗？")){
            itemCatService.dele($scope.selectIds).success(
                function (response) {
                    if (response.success) {
                        $scope.findEntity();
                        $scope.selectList($scope.z_entity);//刷新列表
                    }else{
                        alert("该分类下存在子分类，不能删除！")
                    }
                }
            );
        }

    }

    //获取父分类对象
    $scope.findEntity = function () {
        if ($scope.entity_2 != null) {
            $scope.z_entity = $scope.entity_2;
        } else if ($scope.entity_1 != null) {
            $scope.z_entity = $scope.entity_1;
        } else {
            $scope.z_entity = {id: 0}
        }
    }

    //根据parentId查询

    $scope.findByParentId = function (parentId) {
        //记录父类型id
        $scope.parentId = parentId;

        itemCatService.findByParentId(parentId).success(function (data) {
            $scope.list = data;
        })
    }

    //设置级别
    $scope.grade = 1;
    //修改层级等级
    $scope.setGrade = function () {
        $scope.grade++;
    }


    //面包屑
    $scope.selectList = function (p_entity) {
        if ($scope.grade == 1) {
            $scope.entity_1 = null;
            $scope.entity_2 = null;
        }
        if ($scope.grade == 2) {
            $scope.entity_1 = p_entity;
            $scope.entity_2 = null;
        }
        if ($scope.grade == 3) {
            $scope.entity_2 = p_entity;
        }
        $scope.findByParentId(p_entity.id);
        $scope.selectIds = [];
    }


});	
