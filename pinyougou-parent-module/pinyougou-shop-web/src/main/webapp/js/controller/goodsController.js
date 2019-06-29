//控制层
app.controller('goodsController', function ($scope, $controller,$location, goodsService,uploadService,itemCatService,typeTemplateService) {

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

    //查询实体  (修改数据回显)
    $scope.findOne = function () {

        var id = $location.search().id;

        if(id== null){
            return;
        }

        goodsService.findOne(id).success(
            function (response) {

                $scope.entity = response;
                editor.html($scope.entity.tbGoodsDesc.introduction);
                $scope.entity.tbGoodsDesc.itemImages = JSON.parse($scope.entity.tbGoodsDesc.itemImages);
                $scope.entity.tbGoodsDesc.specificationItems = JSON.parse($scope.entity.tbGoodsDesc.specificationItems);
                $scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse($scope.entity.tbGoodsDesc.customAttributeItems);

                for(var i = 0 ; i< $scope.entity.tbItemList.length ;i++ ){
                    $scope.entity.tbItemList[i].spec = JSON.parse($scope.entity.tbItemList[i].spec);
                }
            }
        );
    }


    /*//跳转修改页面
    $scope.jumpUpdate = function(id){
        location.href = "../admin/goods_edit.html#?id="+ id;
    }*/

    //save数据
    $scope.save = function(){
        if($scope.entity.tbGoods.id==null){
            $scope.add();
        }else{
            $scope.update();
        }
    }

    //定义entity数据结构
    $scope.entity = {tbGoods:{},tbGoodsDesc:{itemImages:[],specificationItems:[],customAttributeItems:[]},tbItemList:[]};

    //更新
    $scope.update = function () {
        $scope.entity.tbGoodsDesc.introduction = editor.html();
        goodsService.update($scope.entity).success(
            function (response) {
                if (response.success) {
                    //重新查询
                    alert(response.message);
                    // $scope.entity={};//tbGoods:{},tbGoodsDesc:{itemImages:[],customAttributeItems:[],specificationItems:[]},tbItemList:[]
                    // editor.html('');
                    location.href="goods.html";
                } else {
                    alert(response.message);
                }
            }
        );
    }

    //保存
    $scope.add = function () {
        $scope.entity.tbGoodsDesc.introduction = editor.html();
        goodsService.add($scope.entity).success(
            function (response) {
                if (response.success) {
                    //重新查询
                    alert(response.message);
                    // $scope.entity={};//tbGoods:{},tbGoodsDesc:{itemImages:[],customAttributeItems:[],specificationItems:[]},tbItemList:[]
                    // editor.html('');
                    location.href="goods.html";
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


    //显示模板中品牌列表
    $scope.$watch('entity.tbGoods.typeTemplateId',function (newValue, oldValue) {



        if(newValue!=undefined){
            typeTemplateService.findOne(newValue).success(function (data) {

                $scope.typeTemplate = data;
                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);

                if($location.search().id == null){
                    $scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
                }else{
                    // $scope.entity.tbGoodsDesc.customAttributeItems = ;---------------------------------------------------------------------------
                }

            })


            //查询规格列表
            typeTemplateService.findSpecList(newValue).success(function (data) {
                $scope.specList = data;
            });
        }

    })


    //查询一级分类列表
    $scope.selectItemCat1List = function () {
        itemCatService.findByParentId(0).success(function (data) {
            $scope.itemCat1List = data;
        })
    }

    //查询二级分类列表
    $scope.$watch('entity.tbGoods.category1Id',function (newValue, oldValue) {
        if(newValue!=undefined){
            $scope.typeTemplate = null;

            itemCatService.findByParentId(newValue).success(function (data) {
                $scope.itemCat2List = data;
                $scope.itemCat3List = null;
            })
        }

    });

    //查询三级分类列表
    $scope.$watch('entity.tbGoods.category2Id',function (newValue, oldValue) {
        if(newValue!=undefined) {
            //置空品牌列表
            $scope.typeTemplate = null;

            itemCatService.findByParentId(newValue).success(function (data) {
                $scope.itemCat3List = data;
            })



        }
    });

    //显示分类1 模板id
    $scope.$watch('entity.tbGoods.category1Id',function (newValue, oldValue) {
        if(newValue!=undefined){
            itemCatService.findOne(newValue).success(function (data) {
                $scope.entity.tbGoods.typeTemplateId = null;
            })
        }

    })

    //显示分类2模板id
    $scope.$watch('entity.tbGoods.category2Id',function (newValue, oldValue) {
        if(newValue!=undefined){
            itemCatService.findOne(newValue).success(function (data) {
                $scope.entity.tbGoods.typeTemplateId = null;
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



    //插入选中规格信息
    $scope.updateSpecAttribute = function ($event,name, value) {

        var object = $scope.searchObjectByKey($scope.entity.tbGoodsDesc.specificationItems,"attributeName",name);

        if(object !=null){
            if($event.target.checked){
                object.attributeValue.push(value);
            }else{
                var index = object.attributeValue.indexOf(value);//获取要移除选项索引
                object.attributeValue.splice(index,1);//移出选项
                //如果选项都取消
                if(object.attributeValue.length <=0){
                    var indexObj = $scope.entity.tbGoodsDesc.specificationItems.indexOf(object);
                    $scope.entity.tbGoodsDesc.specificationItems.splice(indexObj,1);
                }
            }
        }else{
            $scope.entity.tbGoodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
        }
    }



    //创建SKU列表
    $scope.createItemList = function () {
        //初始化
        $scope.entity.tbItemList = [{spec:{},price:0,num:99999,status:'0',isDefault:'0'}];

        var items = $scope.entity.tbGoodsDesc.specificationItems;

        for (var i =0 ;i<items.length; i++){

            $scope.entity.tbItemList = addColumn($scope.entity.tbItemList,items[i].attributeName,items[i].attributeValue);

        }

    }

    //添加SKU列值
    addColumn = function (list, name, values) {

        var newList=[];//新的集合

        for (var i = 0 ; i<list.length ; i++){

            var oldRow = list[i];

            for (var j = 0; j < values.length; j++) {

                var newRow = JSON.parse(JSON.stringify(oldRow));
                newRow.spec[name] = values[j];
                newList.push(newRow);
            }

        }

        return newList;
    }

    //商品状态
    $scope.statusName = ["未审核","审核通过","审核未通过","已关闭"];


    //商品分类
    $scope.itemCatList = [];

    //查询所有商品分类
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(function (data) {
            for(var i = 0 ; i < data.length ; i++){

                $scope.itemCatList[data[i].id] = data[i].name;

            }
        })
    }


    //回显规格选项
    $scope.checkEcho = function (specName,optionName) {

        var specificationItems = $scope.entity.tbGoodsDesc.specificationItems;

        var specObject = $scope.searchObjectByKey(specificationItems,'attributeName',specName);

        if(specObject == null){
            return false;
        }else{
            return specObject.attributeValue.indexOf(optionName) >= 0;
        }

    }

});
