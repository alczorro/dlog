<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
 <%request.setAttribute("context", request.getContextPath()); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>首页</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/thirdparty/bootstrap/js/bootstrap.min.js"></script>
<link rel="stylesheet"  type="text/css" href="<%=request.getContextPath()%>/resources/thirdparty/bootstrap/css/bootstrap.min.css">
</head>
<body>
<div class="navbar navbar-inverse navbar-fixed-top">
	<div class="navbar-inner">      
		<div class="container" >         
			<div class="nav-collapse top-nav">         
				<ul class="nav pull-right">  
					<li>
						<select style="margin:5px" id="appList">
							<c:forEach items="${groups }" var="group">
								<option data-id="<c:out value="${group.id }"/>" value="<c:out value="${group.groupName }"/>"><c:out value="${group.groupName }"></c:out></option>
							</c:forEach>
						</select> 
					</li>
					<li class="active"><a href="${empty context?'/':context }">首页</a></li>
					<c:if test="${!empty adminGroups ||loginUser.isSuperAdmin() }">
						<li id="settingB">
							<a target="_blank" href="<%=request.getContextPath()%>/user/group/list">管理应用</a>
						</li>
					</c:if>
						<li><a target="_blank" href='http://passport.escience.cn'>${loginUser.trueName }</a></li> 	
						<li><a href="logout">退出</a></li>
				</ul> 
			</div>     
		</div>
	</div>
</div>


<iframe style="display:none;margin-top:40px;border:0px" id="kibana" width=100% >

</iframe>
</body>
<script>
$(document).ready(function(){
	$('#kibana').on('load',function(){
		$(this).height(screen.height);
	});
	$('#appList').on('change',function(){
		var encoded=encodeURIComponent($(this).val());
		$('#kibana').show().attr('src','<%=request.getContextPath()%>/kb/index.html#/dashboard/elasticsearch/'+encoded);
	}).change();
	
});
</script>
</html>