package com.fengdai.qa.rest.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 *
 * @author hzweisc
 *
 */
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.fengdai.qa.dao.admin.FengdaiSqlDao;
import com.fengdai.qa.meta.BizSqlOper;
import com.fengdai.qa.service.FengdaiSqlService;
import com.fengdai.qa.service.impl.RedisServiceImpl;
import com.fengdai.qa.utils.GetUserUtil;
@RestController
public class FDInterfaceRestSqlController {

	private static final Logger logger = LoggerFactory.getLogger(FDInterfaceRestSqlController.class);

	@Autowired
	public FengdaiSqlService fengdaiSqlServiceImpl;

	@Autowired
	public RedisServiceImpl redisServiceImpl;






	@RequestMapping({ "api/getInterSQL" })
	public String getInterSQL(BizSqlOper bizSqlOper,HttpServletRequest request, HttpServletResponse response, ModelMap map) throws UnsupportedEncodingException {
		String actiontype = bizSqlOper.getActiontype();
		if("start".equals(actiontype)) {
			Date start=fengdaiSqlServiceImpl.getDbtime();
			String username=GetUserUtil.getUserName(request);
			redisServiceImpl.set(username+"_start", start);
			return "{success}";
		}
		if("stop".equals(actiontype)) {
			Date stoptime= fengdaiSqlServiceImpl.getDbtime();
			String username=GetUserUtil.getUserName(request);
			logger.info("start:{},stop:{},mode:{}",redisServiceImpl.get(username+"_start"),fengdaiSqlServiceImpl.getDbtime(),bizSqlOper.getModelist());
			List<FengdaiSqlDao> BizSqlList = new ArrayList<>();
			for(String var: bizSqlOper.getModelist()) {
				BizSqlList.addAll( (Collection<? extends FengdaiSqlDao>) fengdaiSqlServiceImpl.getDbSQLs((Date) redisServiceImpl.get(username+"_start"),stoptime,"%"+var+"%"));
			}
			return JSONObject.toJSONString(BizSqlList);
		}
		return "{success}";

	}




}
