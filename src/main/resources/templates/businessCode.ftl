<!DOCTYPE html>
<html>
<#include "/include/head.ftl">
<body>
<div>
	<#include "/include/menu.ftl">
	<#include "/include/userinfo.ftl">
</div>
<div class="container col-lg-offset-1 col-lg-9 bianjie">
<p>请修改common项目分支为所要测试的分支</p>
    		<form  role="form" action="api/sourcetome" method="post">
    			<div class="form-group">
    				<label for="name" class="control-label">transferServerIp:</label>
    				<select  class="form-control" name="jenkinstype">
					    <option value="master">master环境</option>
						<option value="online">online环境</option>
					</select>
				</div>
	  			
	  			<div class="form-group">
	  				<button type="submit" class="btn btn-default">提交</button>
				</div>
			</form>
</div>


</body>
</html>

