app.service('brandService',function ($http) {
    this.search = function (currPage,pageSize,searchEntity) {
        return $http.post('../brand/search.do?currPage=' + currPage + '&pageSize=' + pageSize,searchEntity)
    };


    this.save = function (entity) {
        return $http.post('../brand/save.do', entity);
    };

    this.update = function (entity) {
        return $http.post('../brand/update.do', entity);
    };


    this.findOne = function (id) {
        return $http.get("../brand/findOne.do?id=" + id);
    };


    this.remove = function (selectIds) {
        return $http.get("../brand/remove.do?ids=" + selectIds);
    };

    this.selectOptionList = function () {
        return $http.get("../brand/selectOptionList.do");
    };

});