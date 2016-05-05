<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>应用管理</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/resources/js/jquery.tmpl.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/thirdparty/bootstrap/js/bootstrap.min.js"></script>

<link rel="stylesheet"  type="text/css" href="<%=request.getContextPath()%>/resources/thirdparty/bootstrap/css/bootstrap.min.css">
</head>
<body>
<jsp:include page="banner.jsp"></jsp:include>
<!-- 新建 --> 
<div class="modal fade hide" id='createModal'>
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">新建应用</h4>
      </div>
      <div class="modal-body">
      <form class="form-horizontal" id="createForm" action="create" method="post" role="form">
		  <div class="form-group">
		    <label for="groupName" class="col-sm-2 control-label">应用名称：</label>
		    <div class="col-sm-10">
		      <input type="text" class="form-control" name="groupName" id="groupName" placeholder="应用名称，不可重复">
		    </div>
		  </div>
		  <div class="form-group">
		    <label for="groupIndex" class="col-sm-2 control-label">应用索引：</label>
		    <div class="col-sm-10">
		      <input type="text" class="form-control" name="groupIndex" id="groupIndex" placeholder="应用索引，不可重复">
		    </div>
		  </div>
		</form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
        <button type="button" class="btn btn-primary" id="createSubmitBtn">保存</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<div class="modal fade hide" id='updateModal'>
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">更新应用</h4>
      </div>
      <div class="modal-body">
      <form class="form-horizontal" id="updateForm" action="create" method="post" role="form">
		  <input id="groupId" type="hidden" value="" name="groupId"/>
		  <div class="form-group">
		    应用名称：
		    <span class="col-sm-10">
		      <input type="text" class="form-control" name="groupName" id="groupNameToUpdate" placeholder="应用名称，不可重复">
		    </span >
		  </div>
		  		  <div class="form-group">
		    应用索引：
		    <span class="col-sm-10">
		      <input type="text" class="form-control" name="groupIndex" id="groupIndexToUpdate" placeholder="应用索引，不可重复">
		    </span >
		  </div>
		   <div class="form-group">
		   	 新增人员：
		   	 <span class="col-sm-10">
		      <input type="text" class="form-control" name="newMemberCstnetId" id="newMemberCstnetId" placeholder="请输入要加入的人员的邮箱全称">
		      <input type="button" class="btn btn-default" id="addMemberBtn" value="确认新增"/>
		    </span >
		   </div>
		  <div class="form-group">
		    <label class="col-sm-2 ">所属人员：</label>
		      <table class="table " style="width:80%">
		      	<thead>
		      		<tr>
		      			<th>姓名</th>
		      			<th>邮箱</th>
		      			<th>是否管理员</th>
		      			<th>操作</th>
		      		</tr>
		      	</thead>
		      	<tbody id="updateUsers">
		      	</tbody>
		      </table>
		    </div>
		</form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
        <button type="button" class="btn btn-primary" id="updateSubmitBtn">保存</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<table class="table" style="width:60%;margin:50px 15px 15px 15px">
<thead> 
	<tr>
		<th>
			应用名称
		</th>
		<th>
			应用索引
		</th>
		<th>
			操作
		</th>
	</tr>
</thead>
<tbody>
	<c:forEach items="${groups }" var="group">
	<tr>
		<td><c:out value="${group.groupName }"/></td>
		<td><c:out value="${group.groupIndex }"/></td>
		<td>
			<a class="editGroup" data-group-id="${group.id }">编辑</a>
			<c:if test="${loginUser.isSuperAdmin() }">
				<a class="removeGroup" data-group-id="${group.id }">删除</a>
			</c:if>
		</td>
	</tr>
	</c:forEach> 
</tbody>
</table>
<c:if test="${loginUser.isSuperAdmin() }">
	<button type="button" class="btn btn-primary" style="margin:10px" id="createBtn">新增应用</button>
</c:if>
</body>
<script>
$(document).ready(function(){
	//utils
	
		$.fn.extend({ 
		enter: function (callBack) {
		    $(this).keydown(function(event){
		    	if(event.keyCode=='13'){
		    		callBack.apply(event.currentTarget,arguments);
		    		event.preventDefault();
					event.stopPropagation();
		    	}
		    });
		},
		formToJson:function(){  
		    var inputs=$(this).find("input,textarea,select"); 
		    var o = {};  
		    $.each(inputs,function(i,n){  
		        switch(n.nodeName.toUpperCase()){  
		            case "INPUT":  
		                if($(n).is(":checkbox")){  
		                    if($(n).is(":checked")){  
		                        o[n.name]=true;  
		                    }else{  
		                        o[n.name]=false;  
		                    }  
		                }else if($(n).is(":radio")){  
		                    if($(n).is(":checked")){  
		                        o[n.name]=n.value;  
		                    }  
		                }else{  
		                    o[n.name]=n.value;   
		                }  
	
		                break;  
		            case "TEXTAREA":  
		                o[n.name]=$(n).text();  
		                break;  
		            case "SELECT":  
		                o[n.name]=n.value;  
		                break;  
		        }  
		    });  
		    return o;  
		}  
	});
	$('#newMemberCstnetId').enter(function(){
		$('#addMemberBtn').click();
	});
	//utils end
	$('#updateSubmitBtn').on('click',function(){
		$.post('update',$('#updateForm').formToJson()).done(function(rData){
			if(rData.flag){
				window.location.reload();
			}else{
				alert(rData.desc);
			}
		});
	});
	
	$('#addMemberBtn').on('click',function(){  
		var newCstnetId=$('#newMemberCstnetId').val();
		var gid=$('#groupId').val();
		$.post('addMember',{memberCstnetId:newCstnetId,gid:gid}).done(function(data){
			if(data.flag){
				getDetailAndRender(gid);
				$('#newMemberCstnetId').val('');
			}else{
				alert(data.desc);
			}
		});
	});
	$('#groupName').enter(function(){
		$('#createSubmitBtn').click();
	});
	$('#createSubmitBtn').on('click',function(){
		$.post('create',$('#createForm').formToJson()).done(function(data){
			if(data.flag){
				$('#createModal').modal('hide');
				window.location.reload();
			}else{
				alert(data.desc);
			}
		});
	});
	$('#createBtn').on('click',function(){
		$('#createModal').modal('show');
		$('#createForm').get(0).reset();
	});
	function getDetailAndRender(gid){
		$.get('detail/'+gid).done(function(data){
			 if(data.flag){
				 $('#groupNameToUpdate').val(data.data.group.groupName);
				 $('#groupIndexToUpdate').val(data.data.group.groupIndex);
				 $('#groupId').val(data.data.group.id);
				 $('#updateUsers').html($('#userTmpl').tmpl(data.data.users,{
					 isCheckedAdmin:function(isYes){
						 if(isYes){
							 return this.data.admin?"checked='checked'":'';
						 }else{
							 return this.data.admin?'':'checked="checked"';
						 }
					 }
				 }));
			 }else{
				 alert(data.desc);
			 }
		});
	}
	
	$('.editGroup').on('click',function(){
		var gid=$(this).data('groupId');
		 $('#updateModal').modal('show');
		 getDetailAndRender(gid);
	});
	$('.removeMember').live('click',function(){
		var gid=$('#groupId').val();
		var $self=$(this);
		if(confirm("确认删除此成员吗？ 删除后不可恢复")){
			$.post('deleteMember',{gid:gid,uid:$(this).data('userId')}).done(function(data){
				if(data.flag){
					$self.closest('tr').remove();
				}else{
					alert(data.desc);
				}
				
			});
		}
	});
	$('.changeAdmin').live('click',function(){
		var userId=$(this).data('userId');
		var groupId=$('#groupId').val();
		var result=null;
		$.ajax({
			async:false,
			url : 'changeAdmin',
			type : "POST",
			data : {uid:userId,gid:groupId,val:$(this).val()},
			success : function(data){
				result=data;
			}
		});
		if(result.flag){
			
		}else{
			alert(result.desc);
			return false;
		}
		
	});
	
	$('.removeGroup').on('click',function(){
		var gid=$(this).data('groupId');
		var $self=$(this);
		if(confirm('确认不可恢复，确认删除吗？')){
			$.post('delete/'+gid).done(function(data){
				if(data.flag){
					$self.closest('tr').fadeOut(500,function(){
						$self.remove();
					});
				}else { 
					alert('权限不足');
				}
			});
		}
	});
	
});
</script>
<script id="userTmpl" type="text/x-jquery-tmpl">
	<tr>
		<td>{{= trueName}}</td>
		<td>{{= cstnetId}}</td>
		<td>
			<label style="display:inline">
				<input {{= $item.isCheckedAdmin(true) }} type="radio" style="margin:5px" data-user-id="{{= id}}" name="isAdmin{{= id}}" class="changeAdmin" value="true"/>是
			</label>
			<label style="display:inline">
				<input {{= $item.isCheckedAdmin(false) }} type="radio"  style="margin:5px" data-user-id="{{= id}}" name="isAdmin{{= id}}" class="changeAdmin" value="false"/>否
			</label>
		</td>
		<td>
			<a class="removeMember" data-user-id="{{= id }}">删除</a>
		</td>
	</tr>

</script>
</html>