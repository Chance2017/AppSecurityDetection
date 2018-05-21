<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Apk文件上传</title>
<%
	pageContext.setAttribute("ctx", request.getContextPath());
%>
</head>
<body>
	<form action="${ctx}/uploadApk" method="post" enctype="multipart/form-data">
		<fieldset style="text-align:center;line-height:100px;">
			<legend>文件上传</legend>
			<input type="file" name="file" value="选择文件">&nbsp;&nbsp;&nbsp;
		<input type="submit" id="btnSubmit" value="提交">
		</fieldset>
	</form>
</body>

</html>