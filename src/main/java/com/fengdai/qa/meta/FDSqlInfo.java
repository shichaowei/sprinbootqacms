package com.fengdai.qa.meta;

import java.util.Date;

public class FDSqlInfo {

	private int id;
	private String sqlcontent;
	private String reverseresult;
	private String businessJdbcUrl ;
	private String businessJdbcName ;
	private String businessJdbcPassword ;
	private Date addtime;



	public String getBusinessJdbcUrl() {
		return businessJdbcUrl;
	}
	public void setBusinessJdbcUrl(String businessJdbcUrl) {
		this.businessJdbcUrl = businessJdbcUrl;
	}
	public String getBusinessJdbcName() {
		return businessJdbcName;
	}
	public void setBusinessJdbcName(String businessJdbcName) {
		this.businessJdbcName = businessJdbcName;
	}
	public String getBusinessJdbcPassword() {
		return businessJdbcPassword;
	}
	public void setBusinessJdbcPassword(String businessJdbcPassword) {
		this.businessJdbcPassword = businessJdbcPassword;
	}
	public String getReverseresult() {
		return reverseresult;
	}
	public void setReverseresult(String reverseresult) {
		this.reverseresult = reverseresult;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSqlcontent() {
		return sqlcontent;
	}
	public void setSqlcontent(String sql) {
		this.sqlcontent = sql.replaceAll("\r|\n|\\s", " ").replaceAll("\\s+", " ");
	}
	public Date getAddtime() {
		return addtime;
	}
	public void setAddtime(Date datetime) {
		this.addtime = datetime;
	}




}
