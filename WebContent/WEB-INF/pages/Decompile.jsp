<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>反编译界面</title>
</head>
<%
	pageContext.setAttribute("ctx", request.getContextPath());
%>

<body>
	<h1>文件名及文件MD5值</h1>
	<form:form action="${ctx}/getApkInfo" method="post" modelAttribute="apk">
		文件名：<form:input path="filename"/><br><br>
		状态码：<form:input path="code"/>&nbsp;&nbsp;&nbsp;<i><small>0表示成功，107表示之前已经检测过，其他值表示失败</small></i><br><br>
		MD5值：<form:input path="MD5"/><br><br>
		
		<fieldset>
			<legend>检测类型</legend>
			<table>
				<tr>
					<td><label><input name="categories" type="checkbox" onClick="selectAllOnClick()">应用安全类</label></td>
				</tr>
				<tr>
					<td><label><input name="ManifestRisks" type="checkbox">高危配置参数检测</label></td>
					<td><label><input name="StorageRisks" type="checkbox">存储数据风险检测</label></td>
					<td><label><input name="DbInjectRisks" type="checkbox">数据库注入漏洞检测</label></td>
					<td><label><input name="ActivityHijackRisks" type="checkbox">Activity劫持漏洞</label></td>
				</tr>
				<tr>
					<td><label><input name="AddJsInterfaceRisks" type="checkbox">WebView远程代码执行漏洞</label></td>
					<td><label><input name="HttpsCertNotVerifyRisks" type="checkbox">WebView Https证书未校验漏洞</label></td>
					<td><label><input name="SavePasswordRisks" type="checkbox">WebView密码明文保存漏洞</label></td>
					<td><label><input name="FileAccessFromURLsRisks" type="checkbox">WebView文件跨域访问漏洞</label></td>
				</tr>
			</table>
		</fieldset>
		
		<input type="submit" value="提交">
	</form:form>
</body>
<script type="text/javascript" src="js/jquery-3.1.0.min.js"></script>
<script>
	var isCheckAll = false;
	function selectAllOnClick() {
		if(isCheckAll) {
			$("input[type='checkbox']").each(function() {
				this.checked = false;
			});
			isCheckAll = false;
		} else {
			$("input[type='checkbox']").each(function() {
				this.checked = true;
			});
			isCheckAll = true;
		}
	}
</script>
</html>