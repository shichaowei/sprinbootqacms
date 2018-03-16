package com.fengdai.qa.meta;

import java.util.Date;

public class MethodInfo {

	private int id;
	private String classname;
	private String methodname;
	private String methodparams;
	private String methodbody;
	private String flag;
	private String methodpath;
	private Date addtime;


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMethodpath() {
		return methodpath;
	}
	public void setMethodpath(String methodpath) {
		this.methodpath = methodpath;
	}
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


	public String getMethodparams() {
		return methodparams;
	}
	public void setMethodparams(String methodparams) {
		this.methodparams = methodparams;
	}
	public String getMethodbody() {
		return methodbody;
	}
	public void setMethodbody(String methodbody) {
		this.methodbody = methodbody;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public Date getAddtime() {
		return addtime;
	}
	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
	@Override
	public String toString() {
		return "MethodInfo [classname=" + classname + ", methodname=" + methodname + ", methodparams=" + methodparams
				+ ", methodbody=" + methodbody + ", flag=" + flag + ", addtime=" + addtime + "]";
	}






}
