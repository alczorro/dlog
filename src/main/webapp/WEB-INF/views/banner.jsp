<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%request.setAttribute("context", request.getContextPath()); %>
<div class="navbar navbar-inverse navbar-fixed-top">
	<div class="navbar-inner">      
		<div class="container" >         
			<div class="nav-collapse top-nav">         
				<ul class="nav pull-right">  
					<li><a href="${empty context?'/':context }">首页</a></li>
					<c:if test="${!empty adminGroups ||loginUser.isSuperAdmin() }">
						<li class="active" id="settingB">
							<a  target="_blank" href="<%=request.getContextPath()%>/user/group/list">管理应用</a>
						</li>
					</c:if>
						<li><a target="_blank" href='http://passport.escience.cn'>${loginUser.trueName }</a></li> 	
						<li><a href="${context }/logout">退出</a></li>
				</ul> 
			</div>     
		</div>
	</div>
</div>
