//控制层
app.controller('goodsController', function ($scope, $controller, goodsService,uploadService,brandService,itemCatService) {

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

    //定义entity数据结构
    $scope.entity = {tbGoods:{},tbGoodsDesc:{itemImages:[]},tbItemList: []}
    //保存
    $scope.save = function () {
        $scope.entity.tbGoodsDesc.introduction = editor.html();
        goodsService.add($scope.entity).success(
            function (response) {
                if (response.success) {
                    //重新查询
                    alert(response.message);
                    $scope.entity={};
                    editor.html('');
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

    $scope.imageEntity ={};
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (data) {
            if(data.success){
                $scope.imageEntity.url = data.message;
            }else{
                alert(data.message);
            }
        }).error(function () {
            alert("请选择您要上传的图片");
        })
    }

    //列表添加图片
    $scope.addImageEntity = function () {
        $scope.entity.tbGoodsDesc.itemImages.push($scope.imageEntity);
    }
    //列表删除图片
    $scope.deleImageEntity = function (index) {
        $scope.entity.tbGoodsDesc.itemImages.splice(index,1);
    }

    $scope.selectBrandOptions = {data:[]};
    //查询品牌列表
    $scope.findBrandOptions = function () {
        brandService.selectOptionList().success(function (data) {
            $scope.selectBrandOptions.data = data;
        })
    }

    //查询一级分类列表
    $scope.selectItemCat1List = function () {
        itemCatService.findByParentId(0).success(function (data) {
            $scope.itemCat1List = data;
        })
    }

    //查询二级分类列表
    $scope.$watch('entity.tbGoods.category1Id',function (newValue, oldValue) {
        if(newValue!=undefined){
            itemCatService.findByParentId(newValue).success(function (data) {
                $scope.itemCat2List = data;
                $scope.itemCat3List = null;
            })
        }

    });

    //查询三级分类列表
    $scope.$watch('entity.tbGoods.category2Id',function (newValue, oldValue) {
        if(newValue!=undefined) {
            itemCatService.findByParentId(newValue).success(function (data) {
                $scope.itemCat3List = data;
            })
        }
    });

    //显示分类1 模板id
    $scope.$watch('entity.tbGoods.category1Id',function (newValue, oldValue) {
        if(newValue!=undefined){
            itemCatService.findOne(newValue).success(function (data) {
                $scope.entity.tbGoods.typeTemplateId = data.typeId;
            })
        }

    })

    //显示分类2模板id
    $scope.$watch('entity.tbGoods.category2Id',function (newValue, oldValue) {
        if(newValue!=undefined){
            itemCatService.findOne(newValue).success(function (data) {
                $scope.entity.tbGoods.typeTemplateId = data.typeId;
            })
        }

    })

    //显示分类3模板id
    $scope.$watch('entity.tbGoods.category3Id',function (newValue, oldValue) {
        if(newValue!=undefined){
            itemCatService.findOne(newValue).success(function (data) {
                $scope.entity.tbGoods.typeTemplateId = data.typeId;
            })
        }

    })




    /*  ---------------------  */
    /*$scope.selectItemCatOptions = {itemCat0:{data:[]},itemCat1:{data:[]},itemCat2:{data:[]}};
    $scope.parentId = 0;
    //查询分类列表
    $scope.findItemCatOptions = function () {

        itemCatService.findItemCatOptions($scope.parentId).success(function (data) {
            if($scope.grade == 0){
                $scope.selectItemCatOptions.itemCat0.data = data;
                $scope.selectItemCatOptions.itemCat1.data = [];
                $scope.selectItemCatOptions.itemCat2.data = [];
            }else if($scope.grade == 1){
                $scope.selectItemCatOptions.itemCat1.data = data;
                $scope.selectItemCatOptions.itemCat2.data = [];
            }else if($scope.grade == 2){
                $scope.selectItemCatOptions.itemCat2.data = data;
            }

        })
    }

    //获取选中option的id
    $scope.getOptionId = function () {
        if($scope.entity.tbGoods.category1Id!=null){
            $scope.parentId = $scope.entity.tbGoods.category1Id;
        }
        if($scope.entity.tbGoods.category2Id!=null){
            $scope.parentId = $scope.entity.tbGoods.category2Id;
        }

    }

    //分类等级设置
    $scope.grade = 0;
    $scope.setGrade = function () {
        $scope.grade++;
    }*/
    /*  ---------------------  */



});	
