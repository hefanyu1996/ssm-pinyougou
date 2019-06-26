//文件上传服务层
app.service('uploadService',function ($http) {

    this.uploadFile = function () {

        var formData = new FormData();

        formData.append("file",file.files[0]);

        return $http({
           method:'POST',
           url:'../upload.do',
           data:formData,
            //‘Content-Type’: undefined  等于  Content-Type 设置为 multipart/form-data.
            headers:{'Content-Type':undefined},
            //通过设置 transformRequest: angular.identity ，anjularjs transformRequest function 将序列化我们的formdata object.
           transformRequest:angular.identity
        });

    }

})