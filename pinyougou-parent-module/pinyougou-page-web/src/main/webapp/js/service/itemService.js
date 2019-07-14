app.service('itemService', function ($http) {

    this.addToCart = function (itemId, num) {

        return $http.get('http://localhost:9011/cart/addGoodsToCartList.do?itemId=' + itemId + '&num=' + num,{'withCredentials':true});

    }



});