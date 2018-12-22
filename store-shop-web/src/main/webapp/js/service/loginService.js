app.service("loginService",function($http){
	
	this.showName = function(){
		return $http.get("../shop_login/showName.do");
	}
	
});