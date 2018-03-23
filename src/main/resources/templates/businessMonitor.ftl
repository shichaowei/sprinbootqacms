<!DOCTYPE html>
<html>
<#include "/include/head.ftl">
<body>
<div>
	<#include "/include/menu.ftl">
	<#include "/include/userinfo.ftl">
	<div class="container col-lg-offset-1 col-lg-9 bianjie">
		<form role="form"   action="/api/captureMonitor" method="post">
					<div class="form-group">
						<label for="name" class="control-label">捕获动作:</label>
							<select  class="form-control" name="actiontype" >
							  <option value="start">开始捕获</option>
							  <option value="stop">结束捕获</option>
							</select>
						</div>
					<button type="submit" class="btn btn-default  ">提交</button>
		</form> 
	</div>
	</div>
</body>
</html>

