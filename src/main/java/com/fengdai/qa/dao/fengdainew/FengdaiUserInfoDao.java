package com.fengdai.qa.dao.fengdainew;

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.annotation.Resource;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FengdaiUserInfoDao {

	private static final Logger logger = LoggerFactory.getLogger(FengdaiUserInfoDao.class);
//	@Resource(name="newFDJdbcTemplate")
	@Resource
	private JdbcTemplate jdbcTemplate;

	public String deleteAllLoanByLoginname(String loginname) {
		String sqltemp = ""
				+ "DELETE  FROM fengdai_finance.credit_bill_cycle_detail WHERE credit_bill_cycle_id in (select id FROM fengdai_finance.credit_bill_cycle WHERE cellphone='%s');"
				+ "DELETE  FROM  fengdai_finance.credit_user_product WHERE user_id =(select id FROM fengdai_user.sys_user WHERE cellphone = '%s');"
				+ "DELETE FROM fengdai_finance.credit_bill_cycle WHERE cellphone='%s';"
				+ "DELETE  from fengdai_finance.loan_history WHERE loan_apply_id in (select id FROM fengdai_riskcontrol.loan_apply WHERE login_name = '%s');"
				+ "DELETE  FROM  fengdai_finance.loan_bill_detail WHERE loan_apply_id in (select id FROM fengdai_riskcontrol.loan_apply WHERE login_name = '%s');"
				+ "DELETE FROM  fengdai_riskcontrol.loan_apply_fee WHERE  loan_apply_id in (select id FROM fengdai_riskcontrol.loan_apply WHERE login_name = '%s');"
				+ "DELETE from fengdai_riskcontrol.loan_credit_his WHERE account='%s';"
				+ "DELETE FROM fengdai_finance.biz_loan_bill WHERE loan_apply_id in (select id FROM fengdai_riskcontrol.loan_apply WHERE login_name = '%s');"
				+ "DELETE FROM fengdai_finance.biz_loan_bill_log WHERE  loan_apply_id in (select id FROM fengdai_riskcontrol.loan_apply WHERE login_name = '%s');"
				+ "DELETE FROM fengdai_finance.loan_bill_detail WHERE  loan_apply_id  in (select id FROM fengdai_riskcontrol.loan_apply WHERE login_name = '%s');"
				+ "DELETE FROM fengdai_riskcontrol.loan_apply_ext WHERE loan_apply_id in (select id FROM fengdai_riskcontrol.loan_apply WHERE login_name = '%s');"
				+ "DELETE FROM fengdai_riskcontrol.loan_credit WHERE loan_apply_id in (select id FROM fengdai_riskcontrol.loan_apply WHERE login_name = '%s');"
				+ "DELETE FROM fengdai_riskcontrol.dts_claims WHERE apply_id in (SELECT id FROM fengdai_riskcontrol.loan_apply WHERE login_name='%s');"
				+ "DELETE FROM fengdai_riskcontrol.dts_new_claims WHERE apply_id in (SELECT id FROM fengdai_riskcontrol.loan_apply WHERE login_name='%s');"
				+ "DELETE FROM fengdai_riskcontrol.loan_apply WHERE login_name = '%s';"
				+ "DELETE FROM fengdai_activity.activity_people_list WHERE phone ='%s';"
				+ "delete FROM fengdai_shop.shop_order_record_info WHERE order_id in (select id FROM fengdai_shop.shop_order_info WHERE user_id = (select id FROM fengdai_user.sys_user WHERE cellphone='%s'));"
				+ "delete FROM fengdai_shop.shop_order_info WHERE user_id = (select id FROM fengdai_user.sys_user WHERE cellphone='%s');"
				+ "DELETE FROM fengdai_shop.shop_order_after_sales WHERE user_phone ='%s';"
				+ "DELETE FROM fengdai_activity.card_coupons_distribute WHERE user_phone='%s';"
				+ "DELETE FROM fengdai_riskcontrol.entrust_apply WHERE urge_id in(select id FROM fengdai_riskcontrol.urge_record WHERE loan_apply_id in(select id FROM fengdai_riskcontrol.loan_apply WHERE login_name='%s'));"
				+ "DELETE FROM fengdai_riskcontrol.urge_record WHERE loan_apply_id in(select id FROM fengdai_riskcontrol.loan_apply WHERE login_name='%s');"
				+ "DELETE FROM fengdai_riskcontrol.urge_entrust_follow_record WHERE loan_apply_id in(select id FROM fengdai_riskcontrol.loan_apply WHERE login_name='%s');"
				+ "DELETE FROM fengdai_riskcontrol.tel_verify_record WHERE loan_apply_id in(select id FROM fengdai_riskcontrol.loan_apply WHERE login_name='%s');"
				+ "DELETE FROM fengdai_riskcontrol.credit_line_round_check WHERE account='%s';"
				+ "DELETE FROM fengdai_finance.batch_manual_repay WHERE loan_apply_code NOT in (SELECT order_no FROM fengdai_riskcontrol.loan_apply);";

		String sql = String.format(sqltemp, loginname, loginname, loginname, loginname, loginname, loginname, loginname,
				loginname, loginname, loginname, loginname, loginname, loginname, loginname, loginname, loginname,
				loginname, loginname, loginname, loginname, loginname, loginname, loginname, loginname,loginname,
				loginname);
		logger.info(sql);
//		System.out.println(sql.split(";"));
//		System.out.println(jdbcTemplate.getDataSource().toString());
		jdbcTemplate.batchUpdate(sql.split(";"));



		jdbcTemplate.update(String.format("DELETE FROM fengdai_thirdparty.apply_to_third WHERE apply_id in (select id FROM fengdai_riskcontrol.loan_apply WHERE login_name = '%s')", loginname));


		String sqldelZJLS = ""
				+ "DELETE FROM md_funds_db.funds_tradebill WHERE acctCode =(select userId FROM md_account_db.account_user WHERE phone='%s');"
				+ "DELETE  FROM md_funds_db.funds_batchtrade WHERE accountId =(select userId FROM md_account_db.account_user WHERE phone='%s');";
		String sqlZJLS = String.format(sqldelZJLS, loginname, loginname);
		jdbcTemplate.batchUpdate(sqlZJLS.split(";"));
		String sqlResetAccount = ""
				+ "update md_funds_db.funds_account SET totalAmount='%s',balanceAmount=%s,freezeAmount='0' WHERE  "
				+ "acctCode=(select id FROM fengdai_user.sys_user WHERE cellphone='%s')";
		String sqlAccount = String.format(sqlResetAccount, 0, 0, loginname);
		jdbcTemplate.batchUpdate(sqlAccount.split(";"));
		return sql+";"+sqlZJLS+";"+sqlAccount;

	}
	public void deleteAllLoanWithoutCreditByLoginname(String loginname) {

	}
	public String deleteUserByLoginname(String loginname) {
		String var =deleteAllLoanByLoginname(loginname);
		String sqltemp = "use md_funds_db;"
				+ "delete from md_funds_db.funds_account  WHERE acctCode=(select userId FROM md_account_db.account_user WHERE phone='%s');"
				+ "DELETE FROM fengdai_user.user_auth WHERE user_id in (select userId FROM md_account_db.account_user WHERE phone='%s');"
				+ "delete from md_account_db.account_user  WHERE phone='%s';"
				+ "delete from fengdai_user.sys_user WHERE cellphone='%s';"
				+ "DELETE FROM md_funds_db.channel_realname WHERE phone='%s';";
		String sql = String.format(sqltemp, loginname, loginname, loginname, loginname, loginname);
		jdbcTemplate.batchUpdate(sql.split(";"));
		return var+sql;
	}

	public String deleteLoanByLoanName(String loanname) {
		String sqltemp = ""
				+ "DELETE FROM fengdai_riskcontrol.loan_apply WHERE loan_name LIKE '%s';"
				+ "DELETE FROM fengdai_riskcontrol.loan_apply_fee WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);"
				+ "DELETE FROM fengdai_riskcontrol.loan_apply_ext WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);"
				+ "DELETE FROM fengdai_riskcontrol.op_record WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);"
				+ "DELETE FROM fengdai_riskcontrol.tel_verify_record WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);"
				+ "DELETE FROM fengdai_riskcontrol.file WHERE apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);"
				+ "DELETE FROM fengdai_riskcontrol.loan_credit WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);"
				+ "DELETE FROM fengdai_riskcontrol.`loan_credit_his`WHERE loan_apply_id NOT IN(SELECT id FROM`fengdai_riskcontrol`.loan_apply);"
				+ "DELETE FROM fengdai_riskcontrol.loan_urge_back WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);"
				+ "DELETE FROM fengdai_riskcontrol.`claim_record`WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);"
				+ "DELETE FROM fengdai_riskcontrol.`contract_sign_fail_record`WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);"
				+ "DELETE FROM fengdai_riskcontrol.flow_fail_record WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);"
				+ "DELETE FROM fengdai_finance.loan_history WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);"
				+ "DELETE FROM fengdai_finance.loan_manu WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);"
				+ "DELETE FROM fengdai_finance.biz_loan_bill WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);"
				+ "DELETE FROM fengdai_finance.biz_loan_bill_log WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);"
				+ "DELETE FROM fengdai_finance.loan_bill_detail WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);"
				+ "DELETE FROM fengdai_finance.channel_settlement WHERE loan_apply_id NOT IN(SELECT id FROM`fengdai_riskcontrol`.loan_apply);"
				+ "DELETE FROM`fengdai_shop`.`shop_order_info`WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);"
				+ "DELETE FROM`fengdai_shop`.`shop_order_record_info`WHERE order_id NOT IN(SELECT id FROM`fengdai_shop`.`shop_order_info`);"
				+ "DELETE FROM`fengdai_shop`.`shop_order_after_sales`WHERE`loan_apply_id`NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);"
				+ "DELETE FROM`fengdai_riskcontrol`.`apply_astrict`;"
				+ "DELETE FROM fengdai_finance.batch_manual_repay WHERE loan_apply_code NOT in (SELECT order_no FROM fengdai_riskcontrol.loan_apply);"
				+ "DELETE FROM`fengdai_thirdparty`.`apply_to_third`WHERE apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);";
		String sql = String.format(sqltemp, "%"+loanname);
		jdbcTemplate.batchUpdate(sql.split(";"));
//		try {
//
//			jdbcTemplate.update("DELETE FROM`fengdai_thirdparty`.`apply_to_third`WHERE apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply)");
//		} catch (DataAccessException e) {
//			jdbcTemplate.update("DELETE FROM`fengdai_channel`.`apply_to_third`WHERE apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply)");
//		}
		return sql;

	}
	public String deleteLoanByLoanId(String loanapplyid) {
		String sqltemp ="DELETE FROM fengdai_riskcontrol.loan_apply WHERE id='%s';DELETE FROM fengdai_riskcontrol.loan_apply_fee WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);DELETE FROM fengdai_riskcontrol.loan_apply_ext WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);DELETE FROM fengdai_riskcontrol.op_record WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);DELETE FROM fengdai_riskcontrol.tel_verify_record WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);DELETE FROM fengdai_riskcontrol.`file`WHERE apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);DELETE FROM fengdai_riskcontrol.loan_credit WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);DELETE FROM fengdai_riskcontrol.`loan_credit_his`WHERE loan_apply_id NOT IN(SELECT id FROM`fengdai_riskcontrol`.loan_apply);DELETE FROM fengdai_riskcontrol.loan_urge_back WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);DELETE FROM fengdai_riskcontrol.`claim_record`WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);DELETE FROM fengdai_riskcontrol.`contract_sign_fail_record`WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);DELETE FROM fengdai_riskcontrol.flow_fail_record WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);DELETE FROM fengdai_finance.loan_history WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);DELETE FROM fengdai_finance.loan_manu WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);DELETE FROM fengdai_finance.biz_loan_bill WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);DELETE FROM fengdai_finance.biz_loan_bill_log WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);DELETE FROM fengdai_finance.loan_bill_detail WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);DELETE FROM fengdai_finance.channel_settlement WHERE loan_apply_id NOT IN(SELECT id FROM`fengdai_riskcontrol`.loan_apply);DELETE FROM`fengdai_shop`.`shop_order_info`WHERE loan_apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);DELETE FROM`fengdai_shop`.`shop_order_record_info`WHERE order_id NOT IN(SELECT id FROM`fengdai_shop`.`shop_order_info`);DELETE FROM`fengdai_shop`.`shop_order_after_sales`WHERE`loan_apply_id`NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);DELETE FROM`fengdai_thirdparty`.`apply_to_third`WHERE apply_id NOT IN(SELECT id FROM fengdai_riskcontrol.loan_apply);DELETE FROM`fengdai_riskcontrol`.`apply_astrict`;";
		String sql = String.format(sqltemp, loanapplyid);
		jdbcTemplate.batchUpdate(sql.split(";"));
		return sql;
	}
	public String changeProcessSQDToLoanning(String loanname) {
		String sql1 = "UPDATE  fengdai_riskcontrol.loan_apply_ext SET grant_status='GRANT_FAIL' WHERE loan_apply_id = (select id FROM fengdai_riskcontrol.loan_apply WHERE loan_name LIKE  '%s');";
		String sql2 = "update fengdai_riskcontrol.loan_apply SET  front_status='loanning' WHERE loan_name LIKE  '%s';";
//		String sql3 = "DELETE from fengdai_finance.mc_business WHERE relate_id=(select id FROM fengdai_riskcontrol.loan_apply WHERE loan_name LIKE  '%s');";
		String sql4 = "DELETE FROM fengdai_finance.loan_history WHERE loan_apply_id=(select id FROM fengdai_riskcontrol.loan_apply WHERE loan_name LIKE  '%s');";
		String sql5 = "DELETE from fengdai_finance.mc_business WHERE relate_id=(select id FROM fengdai_riskcontrol.loan_apply WHERE loan_name LIKE  '%s');";
		ArrayList<String> SQLlist = new ArrayList<>();
		SQLlist.add(String.format(sql1, "%" + loanname));
		SQLlist.add(String.format(sql2, "%" + loanname));
//		SQLlist.add(String.format(sql3, "%" + loanname));
		SQLlist.add(String.format(sql4, "%" + loanname));
		SQLlist.add(String.format(sql5, "%" + loanname));
		SQLlist.forEach(sql -> jdbcTemplate.update(sql));
		return ToStringBuilder.reflectionToString(SQLlist);
	}

	public String changeSQDToLoanning(String loanname) {

		String sql1 = "UPDATE  fengdai_riskcontrol.loan_apply_ext SET "
				+ "grant_status='PENDING' WHERE loan_apply_id = (select id FROM fengdai_riskcontrol.loan_apply WHERE loan_name LIKE  '%s');";
		String sql2 = "update fengdai_riskcontrol.loan_apply SET  front_status='loanning' WHERE loan_name LIKE  '%s';";
		ArrayList<String> SQLlist = new ArrayList<>();
		SQLlist.add(String.format(sql1, "%" + loanname));
		SQLlist.add(String.format(sql2, "%" + loanname));
		SQLlist.forEach(sql -> jdbcTemplate.update(sql));
		return ToStringBuilder.reflectionToString(SQLlist);
	}

	public String changeUserAccount(String username,BigDecimal moneynum) {
		String sqltemp = ""
				+ "update md_funds_db.funds_account SET totalAmount='%s',balanceAmount='%s',freezeAmount='0' WHERE  "
				+ "acctCode=(select id FROM fengdai_user.sys_user WHERE cellphone='%s')";
		String sql = String.format(sqltemp, moneynum, moneynum, username);
		logger.info("SQL IS {}",sql);
		jdbcTemplate.update(sql);
		return sql;

	}




}
