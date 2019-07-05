app.controller('contentController',function ($scope,contentService) {

    $scope.contentList = [];//广告集合

    $scope.findByCategoryId = function (categoryId) {
        contentService.findByCategoryId(categoryId).success(function (data) {
            $scope.contentList[categoryId] = data;
        });
    }


    $scope.search = function () {

        location.href = 'http://localhost:9006/search.html#?keywords='+$scope.keywords;

    }

});