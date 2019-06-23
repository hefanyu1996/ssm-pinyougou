app.controller('indexController',function ($scope,$controller,loginService) {

    $scope.showLoginName = function () {
        loginService.loginName().success(function (data) {
            $scope.loginName = data.loginName;
        })
    }

})