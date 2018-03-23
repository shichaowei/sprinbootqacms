package com.fengdai.qa.dao.admin;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.fengdai.qa.annotation.DS;
import com.fengdai.qa.constants.DataSourceConsts;
import com.fengdai.qa.meta.BizMethodMonitor;

@DS(value=DataSourceConsts.DEFAULT)
@Mapper
public interface FengdaiMonitorInfoDao {

	@Insert("insert into fengdaimonitor(classname,methodname,exectime,addtime) values (#{classname},#{methodname},#{exectime},now()) ")
	public int addmethodMonitor(BizMethodMonitor bizMethodMonitor);

	@Select("select * from fengdaimonitor where addtime>=#{starttime} and addtime<=#{stoptime} order by addtime desc")
	public List<BizMethodMonitor> getMethodMonitors(@Param("starttime") Date starttime,@Param("stoptime") Date  stoptime);

	@Select("SELECT NOW()")
	public Date getDbtime();
}
