<%-- <%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Apk文件相关信息</title>
<%
	pageContext.setAttribute("ctx", request.getContextPath());
%>
</head>
<body>
	<h1>Apk文件相关信息</h1>
	<form:form action="${ctx}/risksDetection" method="post" modelAttribute="apkInfo">
		MD5值：<form:input path="MD5"/><br><br>
		应用程序版本：<form:input path="version"/><br><br>
		应用程序名称：<form:input path="appName"/><br><br>
		应用程序包名：<form:input path="packageName"/><br><br>
		<input type="submit" value="风险分析">
	</form:form>
</body>
</html> --%>