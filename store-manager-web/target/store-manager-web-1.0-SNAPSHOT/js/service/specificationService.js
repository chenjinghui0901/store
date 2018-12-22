//服务层
app.service('specificationService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../specification/findAll.do');		
	}
	//分页 
	this.findPage=function(page,size){
		return $http.get('../specification/findPage.do?page='+page+'&size='+size);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../specification/findOne.do?id='+id);
	}
	//增加 
	this.add=function(entity){
		return  $http.post('../specification/add.do',entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../specification/update.do',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../specification/delete.do?ids='+ids);
	}
	//搜索
	this.search=function(page,size,searchEntity){
		return $http.post('../specification/search.do?page='+page+"&size="+size, searchEntity);
	}  
	//下拉列表
	this.selectOptionList=function(){
		return $http.get("../specification/selectOptionList.do");
	}
});
