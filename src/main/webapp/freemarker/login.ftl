<!DOCTYPE HTML>
<html lang="en-US">
<#include "/include/head.ftl">
    <head>
        <meta charset="UTF-8">
        <title>登录</title>
    </head>
    <body>
    		<form action="api/login" method="post">
	  			<p>userName: <input type="text" name="userName" /></p>
	  			<p>userPassword: <input type="password" name="userPassword" /></p>
	  			<input type="submit" value="登录" />
			</form>
			<p class="tc register_info">没有账号？<a ui-sref="register" href="/register">免费注册</a></p>
    </body>
</html>