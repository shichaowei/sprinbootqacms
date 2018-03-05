package com.fengdai.qa.service;


import java.util.Date;
import java.util.List;

import com.fengdai.qa.meta.FDSqlInfo;

public interface FengdaiSqlService {

	public int addSql(FDSqlInfo fdSqlInfo);

	public Date getDbtime();

	public List<FDSqlInfo> getDbSQLs(Date starttime,Date  stoptime ,String modelist);

}
