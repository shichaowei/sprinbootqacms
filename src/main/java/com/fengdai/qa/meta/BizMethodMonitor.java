package com.fengdai.qa.meta;

import java.io.Serializable;
import java.util.Date;

/**
 * 注意需要序列化
 * @author hzweisc
 *
 */
public class BizMethodMonitor implements Serializable{

	private String classname;
	private String methodname;
	private long exectime;
	private Date addtime;
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public String getMethodname() {
		return methodname;
	}
	public void setMethodname(String methodname) {
		this.methodname = methodname;
	}
	public long getExectime() {
		return exectime;
	}
	public void setExectime(long exectime) {
		this.exectime = exectime;
	}
	public Date getAddtime() {
		return addtime;
	}
	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}



}
