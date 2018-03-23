package com.fengdai.qa.service;

import java.util.Date;
import java.util.List;

import com.fengdai.qa.meta.BizMethodMonitor;

public interface FengdaiMonitorService {

	public int addmethodMonitor(BizMethodMonitor bizMethodMonitor);

	public List<BizMethodMonitor> getMethodMonitors(Date starttime, Date  stoptime);

	public Date getDbtime();
}
