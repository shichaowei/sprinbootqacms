package com.fengdai.qa.dao.admin;


import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.fengdai.qa.annotation.DS;
import com.fengdai.qa.constants.DataSourceConsts;
import com.fengdai.qa.meta.FDSqlInfo;


@DS(value=DataSourceConsts.DEFAULT)
@Mapper
public interface FengdaiSqlDao {

	@Insert("INSERT INTO fengdaisqls(sqlcontent,reverseresult,addtime,businessJdbcUrl) VALUES(#{sqlcontent}, #{reverseresult},now(),#{businessJdbcUrl})")
	public int addSql(FDSqlInfo fdSqlInfo);

	@Select("SELECT NOW()")
	public Date getDbtime();

	@Select( "SELECT * from fengdaisqls where addtime>=#{starttime} and addtime<=#{stoptime} and businessJdbcUrl like #{mode}"
			)
	public List<FDSqlInfo> getDbSQLs(@Param("starttime") Date starttime,@Param("stoptime") Date  stoptime,@Param("mode") String mode);


}
