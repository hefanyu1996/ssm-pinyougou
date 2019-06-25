//控制层
app.controller('sellerController', function ($scope, $controller, sellerService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中
    $scope.findAll = function () {
        sellerService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        sellerService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        sellerService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }

    //保存
    $scope.add=function(){

        var protocolCb = document.getElementById('protocol_cb');
        if(protocolCb.checked){
            sellerService.add($scope.entity).success(
                function(response){
                    if(response.success){
                        //跳转登录页
                        location.href='shoplogin.html';
                    }else{
                        alert(response.message);
                    }
                }
            );
        }else{
            alert("同意协议才能注册")
        }

    }

    //修改
    $scope.update=function(){
        sellerService.update($scope.entity).success(
            function(response){
                if(response.success){
                    //跳转登录页
                    location.href='shoplogin.html';
                }else{
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        sellerService.dele($scope.selectIds).success(
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
        sellerService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录
            }
        );
    }

    //商家审核
    $scope.updateStatus = function (sellerId, status) {
        sellerService.updateStatus(sellerId, status).success(function (data) {
            if (data.success) {
                $scope.reloadList();
            } else {
                alert(data.message)
            }
        })
    }

    $scope.logout = function () {
        if(confirm("确认要注销吗？")){
            location.href = "../logout.do";
        }

    }

});