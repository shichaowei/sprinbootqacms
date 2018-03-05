package com.fengdai.qa.service.impl;


import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fengdai.qa.dao.admin.FengdaiSqlDao;
import com.fengdai.qa.meta.FDSqlInfo;
import com.fengdai.qa.service.FengdaiSqlService;

@Service
public class FengdaiSqlServiceImpl implements FengdaiSqlService {


	@Autowired
	private FengdaiSqlDao fengdaiSqlDao;

	@Override
	public int addSql(FDSqlInfo fdSqlInfo) {
		return fengdaiSqlDao.addSql(fdSqlInfo);
	}

	@Override
	public Date getDbtime() {
		return fengdaiSqlDao.getDbtime();
	}

	@Override
	public List<FDSqlInfo> getDbSQLs(Date starttime, Date stoptime,String modelist) {
		// TODO Auto-generated method stub
		return fengdaiSqlDao.getDbSQLs(starttime, stoptime, modelist);
	}

}
