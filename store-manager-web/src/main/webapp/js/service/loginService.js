app.service("loginService",function($http){
	
	this.showName = function(){
		return $http.get("../manager_login/showName.do");
	}
	
});