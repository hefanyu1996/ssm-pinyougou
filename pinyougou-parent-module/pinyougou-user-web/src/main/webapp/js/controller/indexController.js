app.controller('indexController',function ($scope,loginService) {

    $scope.showName = function () {

        loginService.showName().success(function (data) {

            $scope.loginName = data.loginName;

        })

    }

})