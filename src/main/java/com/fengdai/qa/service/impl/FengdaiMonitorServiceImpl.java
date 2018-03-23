package com.fengdai.qa.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fengdai.qa.dao.admin.FengdaiMonitorInfoDao;
import com.fengdai.qa.meta.BizMethodMonitor;
import com.fengdai.qa.service.FengdaiMonitorService;

@Service
public class FengdaiMonitorServiceImpl implements FengdaiMonitorService {

	@Autowired
	private FengdaiMonitorInfoDao fengdaiMonitorInfoDao;

	@Override
	public int addmethodMonitor(BizMethodMonitor bizMethodMonitor) {
		// TODO Auto-generated method stub
		return fengdaiMonitorInfoDao.addmethodMonitor(bizMethodMonitor);
	}

	@Override
	public List<BizMethodMonitor> getMethodMonitors( Date starttime, Date  stoptime) {
		// TODO Auto-generated method stub
		return fengdaiMonitorInfoDao.getMethodMonitors(starttime, stoptime);
	}

	@Override
	public Date getDbtime() {
		return fengdaiMonitorInfoDao.getDbtime();
	}

}
