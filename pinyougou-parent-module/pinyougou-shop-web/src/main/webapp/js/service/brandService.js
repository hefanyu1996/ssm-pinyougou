app.service('brandService',function ($http) {

    this.selectOptionList = function () {
        return $http.get("../brand/selectOptionList.do");
    };

})