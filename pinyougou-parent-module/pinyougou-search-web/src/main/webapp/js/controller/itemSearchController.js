app.controller('itemSearchController', function ($scope,$location,itemSearchService) {

    //关键字搜索
    $scope.search = function () {

        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);

        itemSearchService.search($scope.searchMap).success(function (data) {

            $scope.resultMap = data;//返回搜索结果
            //构建分页栏
            buildPageLabel();

        })
    }

    //构建分页栏方法
    buildPageLabel = function () {

        $scope.pageLabel = [];

        var totalPages = $scope.resultMap.totalPages;
        var pageNo = $scope.searchMap.pageNo;
        var start = 1;
        var end = totalPages;

        if(totalPages > 5){

            if(pageNo <= 3 ){

                $scope.prePoints = false;
                $scope.offPoints = true;

                end = 5;
            }else if(pageNo >= totalPages - 2  ){

                $scope.prePoints = true;
                $scope.offPoints = false;

                start = totalPages - 4 ;
            }else{

                $scope.prePoints = true;
                $scope.offPoints = true;

                start = pageNo - 2;
                end = pageNo +2;
            }

        }else{
            $scope.prePoints = false;
            $scope.offPoints = false;
        }

        for (var i = start; i <= end; i++) {
            $scope.pageLabel.push(i)
        }
    }

    //页码范围限定
    $scope.queryByPage = function(pageNo){

        if(pageNo < 1 || pageNo > $scope.resultMap.totalPages){
            return;
        }

        $scope.searchMap.pageNo = pageNo;
        $scope.search();

    }


    $scope.searchMap = {
        'keywords': '',
        'category': '',
        'brand': '',
        'spec': {},
        'price': '',
        "pageNo": 1,
        "pageSize": 20,
        "sort":'',
        "sortField":''
    };

    //添加搜索项

    $scope.addSearchItem = function (key, value) {

        if (key == 'brand' || key == 'category' || key == 'price') {//如果点击的是分类或者是品牌
            if (key == 'category') {//切换分类时初始化选项
                $scope.searchMap.brand = ''
                $scope.searchMap.spec = {};
            }
            $scope.searchMap[key] = value;

        } else {

            $scope.searchMap.spec[key] = value;

        }

        $scope.search();
    }


    //移出搜索项
    $scope.deleteSearchItem = function (key) {

        if (key == 'brand' || key == 'category' || key == 'price') {//如果点击的是分类或者是品牌

            $scope.searchMap[key] = '';

        } else {

            delete $scope.searchMap.spec[key];

        }

        $scope.search();

    }


    //搜索结果排序
    $scope.sortSearch = function (sort,sortField) {

        $scope.searchMap.sort = sort;
        $scope.searchMap.sortField = sortField;

        $scope.search();

    }


    //关键字包含品牌 隐藏品牌选项
    $scope.keywordsIsBrand = function () {

        var brand = $scope.searchMap.keywords;
        var brandList = $scope.resultMap.brandList;

        for(var i = 0 ; i < brandList.length ; i++){
            if(brand.indexOf(brandList[i].text) >= 0 ){
                return false;
            }
        }

        return true;

    }


    //首页搜索对接
    $scope.loadKeywords = function () {

        $scope.searchMap.keywords = $location.search().keywords;

        $scope.search();


    }

});