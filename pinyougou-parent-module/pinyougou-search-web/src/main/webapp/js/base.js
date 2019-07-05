var app = angular.module('pinyougou',[]);

app.filter('highlightFilter',['$sce',function ($sce) {

    return function (data) {
        return $sce.trustAsHtml(data); //对过滤的响应数据中html文本添加信任
    }
}]);